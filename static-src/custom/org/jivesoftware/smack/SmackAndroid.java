package org.jivesoftware.smack;

import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.DNSJavaResolver;
import org.xbill.DNS.ResolverConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class SmackAndroid {
	private static SmackAndroid sSmackAndroid = null;

	private BroadcastReceiver mConnectivityChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ResolverConfig.refresh();
		}
	};

	private static boolean receiverRegistered = false;
	private Context mCtx;

	private SmackAndroid(Context ctx) {
		mCtx = ctx;
		DNSUtil.setDNSResolver(DNSJavaResolver.getInstance());
	}

	/**
	 * Init Smack for Android. Make sure to call
	 * SmackAndroid.onDestroy() in all the exit code paths of your
	 * application.
	 */
	public static synchronized SmackAndroid init(Context ctx) {
		if (sSmackAndroid == null) {
			sSmackAndroid = new SmackAndroid(ctx);
		}
		sSmackAndroid.maybeRegisterReceiver();
		return sSmackAndroid;
	}

	/**
	 * Cleanup all components initialized by init(). Make sure to call
	 * this method in all the exit code paths of your application.
	 */
	public synchronized void onDestroy() {
		if (receiverRegistered) {
			mCtx.unregisterReceiver(mConnectivityChangedReceiver);
			receiverRegistered = false;
		}
	}

	private void maybeRegisterReceiver() {
		if (!receiverRegistered) {
			mCtx.registerReceiver(mConnectivityChangedReceiver, new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION));
			receiverRegistered = true;
		}
	}
}
