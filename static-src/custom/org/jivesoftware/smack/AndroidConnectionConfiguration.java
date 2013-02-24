package org.jivesoftware.smack;

import java.io.File;

import android.os.Build;

import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.HostAddress;

import java.util.List;

/**
 * This class wraps DNS SRV lookups for a new ConnectionConfiguration in a 
 * new thread, since Android API >= 11 (Honeycomb) does not allow network 
 * activity in the main thread. 
 * 
 * @author Florian Schmaus fschmaus@gmail.com
 *
 */
public class AndroidConnectionConfiguration extends ConnectionConfiguration {
    private static final int DEFAULT_TIMEOUT = 10000;
    
    /**
     * Creates a new ConnectionConfiguration for the specified service name.
     * A DNS SRV lookup will be performed to find out the actual host address
     * and port to use for the connection.
     *
     * @param serviceName the name of the service provided by an XMPP server.
     */
    public AndroidConnectionConfiguration(String serviceName) throws XMPPException {
        super();
        AndroidInit(serviceName, DEFAULT_TIMEOUT);
    }
    
    /**
     * 
     * @param serviceName
     * @param timeout
     * @throws XMPPException
     */
    public AndroidConnectionConfiguration(String serviceName, int timeout) throws XMPPException {
        super();
        AndroidInit(serviceName, timeout);
    }

    public AndroidConnectionConfiguration(String host, int port, String name) {
	super(host, port, name);
	AndroidInit();
    }

    private void AndroidInit() {
    	// API 14 is Ice Cream Sandwich
	if (Build.VERSION.SDK_INT >= 14) {
	    setTruststoreType("AndroidCAStore");
	    setTruststorePassword(null);
	    setTruststorePath(null);
	} else {
	    setTruststoreType("BKS");
	    String path = System.getProperty("javax.net.ssl.trustStore");
	    if (path == null)
		path = System.getProperty("java.home") + File.separator + "etc"
		    + File.separator + "security" + File.separator
		    + "cacerts.bks";
	    setTruststorePath(path);
	}
    }

    /**
     * 
     * @param serviceName
     * @param timeout
     * @throws XMPPException
     */
    private void AndroidInit(String serviceName, int timeout) throws XMPPException {
	AndroidInit();
        class DnsSrvLookupRunnable implements Runnable {
            String serviceName;
            List<HostAddress> addresses;

            public DnsSrvLookupRunnable(String serviceName) {
                this.serviceName = serviceName;
            }

            @Override
            public void run() {
                addresses = DNSUtil.resolveXMPPDomain(serviceName);
            }

            public List<HostAddress> getHostAddresses() {
                return addresses;
            }
        }

        DnsSrvLookupRunnable dnsSrv = new DnsSrvLookupRunnable(serviceName);
        Thread t = new Thread(dnsSrv, "dns-srv-lookup");
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException e) {
            throw new XMPPException("DNS lookup timeout after " + timeout + "ms", e);
        }

        hostAddresses = dnsSrv.getHostAddresses();
        if (hostAddresses == null) {
        	throw new XMPPException("DNS lookup failure");
        }

        ProxyInfo proxy = ProxyInfo.forDefaultProxy();

        init(serviceName, proxy);
    }
}
