package com.i2peer.jeeves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;

public class AccessoryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (Constants.ACTION_USB_PERMISSION.equals(action)) {
			synchronized (this) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
						false)) {

					UsbAccessory ua = intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					// TODO: Do something
				}
			}
		} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {

		}
	}
}
