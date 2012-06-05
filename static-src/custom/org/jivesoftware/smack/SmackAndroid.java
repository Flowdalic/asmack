package org.jivesoftware.smack;

import org.jivesoftware.smackx.ConfigureProviderManager;
import org.xbill.DNS.ResolverConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SmackAndroid {
    private static SmackAndroid sSmackAndroid = null;
    
    private SmackAndroid(Context ctx) {
        ConfigureProviderManager.configureProviderManager();
        ctx.registerReceiver(new ConnectivtyChangedReciever(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
    
    public static SmackAndroid init(Context ctx) {
        if (sSmackAndroid == null) {
            sSmackAndroid = new SmackAndroid(ctx);
        }
        return sSmackAndroid;
    }
    
    class ConnectivtyChangedReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ResolverConfig.refresh();
        }
        
    }
}
