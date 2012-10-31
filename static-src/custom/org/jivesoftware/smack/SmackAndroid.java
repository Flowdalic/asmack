package org.jivesoftware.smack;

import org.jivesoftware.smackx.ConfigureProviderManager;
import org.jivesoftware.smackx.InitStaticCode;
import org.xbill.DNS.ResolverConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SmackAndroid {
    private static SmackAndroid sSmackAndroid = null;

    private BroadcastReceiver mConnectivityChangedReceiver;
    private Context mCtx;
    private boolean mReg;

    private SmackAndroid(Context ctx) {
        mReg = false;
        ConfigureProviderManager.configureProviderManager();
        InitStaticCode.initStaticCode(ctx);
        mConnectivityChangedReceiver = new ConnectivtyChangedReceiver();
    }

    public static SmackAndroid init(Context ctx) {
        if (sSmackAndroid == null) {
            sSmackAndroid = new SmackAndroid(ctx);
        }
	if (!sSmackAndroid.mReg) {
	    ctx.registerReceiver(sSmackAndroid.mConnectivityChangedReceiver, 
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            sSmackAndroid.mCtx = ctx;
	    sSmackAndroid.mReg = true;
	}
        return sSmackAndroid;
    }

    public void exit() {
        if (mReg) {	
            mCtx.unregisterReceiver(mConnectivityChangedReceiver);
	    mReg = false;
	}
    }

    class ConnectivtyChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ResolverConfig.refresh();
        }

    }
}
