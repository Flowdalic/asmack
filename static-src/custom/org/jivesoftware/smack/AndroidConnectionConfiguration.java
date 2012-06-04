package org.jivesoftware.smack;

import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.util.DNSUtil;

/**
 * This class wraps DNS SRV lookups for a new ConnectionConfiguration in a 
 * new thread, since Android API >= 11 (Honeycomb) does not allow network 
 * activity in the main thread. 
 * 
 * @author Florian Schmaus fschmaus@gmail.com
 *
 */
public class AndroidConnectionConfiguration extends ConnectionConfiguration {
    private static final int DEFAULT_TIMEOUT = 1000;
    
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
    
    /**
     * 
     * @param serviceName
     * @param timeout
     * @throws XMPPException
     */
    private void AndroidInit(String serviceName, int timeout) throws XMPPException {
        class DnsSrvLookupRunnable implements Runnable {
            String serviceName;
            volatile DNSUtil.HostAddress address;

            public DnsSrvLookupRunnable(String serviceName) {
                this.serviceName = serviceName;
            }

            @Override
            public void run() {
                // Perform DNS lookup to get host and port to use
                address = DNSUtil.resolveXMPPDomain(serviceName);
            }

            public DNSUtil.HostAddress getHostAddress() {
                return address;
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

        DNSUtil.HostAddress address = dnsSrv.getHostAddress();
        if (address == null) {
        	throw new XMPPException("DNS lookup failure");
        }
        
        String host = address.getHost();
        int port = address.getPort();
        ProxyInfo proxy = ProxyInfo.forDefaultProxy();
        
        init(host, port, serviceName, proxy);       
    }
}