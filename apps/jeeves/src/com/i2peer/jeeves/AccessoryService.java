package com.i2peer.jeeves;

import java.io.IOException;

import com.i2peer.jeeves.accessories.Accessory;
import com.i2peer.jeeves.protocol.MotorPacket;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class AccessoryService extends Service {

	public static final int MSG_START_MOTOR = 1;

	private static final String ACTION_USB_PERMISSION = "com.i2peer.action.USB_PERMISSION";

	private static final String TAG = "Jeeves";

	private PendingIntent mUsbPermissionIntent;

	private boolean mPermissionRequestPending;

	private UsbManager mUsbManager;

	private Accessory mAccessory;

	private final Messenger mMessenger = new Messenger(new MessageHandler());

	private class MessageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (mAccessory == null) {
				UsbAccessory[] accessories = mUsbManager.getAccessoryList();
				UsbAccessory accessory = (accessories == null ? null
						: accessories[0]);
				if (mAccessory == null && accessory != null) {
					mAccessory = Accessory.createAccessory(
							AccessoryService.this.getApplicationContext(),
							accessory);
				}
			}
			if (mAccessory == null) {
				return;
			}
			switch (msg.what) {
			case MSG_START_MOTOR:
				MotorPacket mp = new MotorPacket(mAccessory);
				mp.setId(1);
				mp.setSpeed(100);
				try {
					mp.send();
				} catch (IOException e) {
					e.printStackTrace();
				}

				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mUsbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAccessory != null) {
			mAccessory.close();
		}
		unregisterReceiver(mUsbReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mAccessory != null) {
			return START_STICKY;
		}
		UsbAccessory[] ua = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (ua == null ? null : ua[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				mAccessory = Accessory.createAccessory(getApplicationContext(),
						accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mUsbPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		}
		return START_STICKY;
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			UsbAccessory accessory = intent
					.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						mAccessory = Accessory.createAccessory(
								getApplicationContext(), accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				if (accessory != null && accessory.equals(mAccessory)) {
					mAccessory.close();
					mAccessory = null;
				}
			}
		}
	};
}
