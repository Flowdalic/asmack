package de.measite.smack;

import java.util.Map;

import com.novell.sasl.client.DigestMD5SaslClient;

import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.apache.qpid.management.common.sasl.PlainSaslClient;

public class SaslClientFactory implements
		org.apache.harmony.javax.security.sasl.SaslClientFactory {

	@Override
	public SaslClient createSaslClient(String[] mechanisms,
			String authorizationId, String protocol, String serverName,
			Map<String, ?> props, CallbackHandler cbh) throws SaslException {
		for (String mech: mechanisms) {
			if ("PLAIN".equals(mech)) {
				return new PlainSaslClient(authorizationId, cbh);
			} else
			if ("DIGEST-MD5".equals(mech)) {
				return DigestMD5SaslClient.getClient(
					authorizationId,
					protocol,
					serverName,
					props,
					cbh
				);
			}
		}
		return null;
	}

	@Override
	public String[] getMechanismNames(Map<String, ?> props) {
		return new String[]{
			"PLAIN",
			"DIGEST-MD5"
		};
	}

}
