package com.i2peer.jeeves.accessories;

import java.io.IOException;

import android.os.ParcelFileDescriptor;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;

public class Accessory {

	private final UsbAccessory mAccessory;

	private final UsbManager mUsbManager;

	private final Context mContext;

	private ParcelFileDescriptor mDescriptor;

	public static Accessory createAccessory(Context context, UsbAccessory ua)
			throws SecurityException {
		return new Accessory(context, ua);
	}

	private Accessory(Context context, UsbAccessory ua)
			throws SecurityException {
		if (context == null) {
			throw new IllegalArgumentException("Context is null");
		}

		if (ua == null) {
			throw new IllegalArgumentException("UsbAccessory is null");
		}

		mUsbManager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);
		if (!mUsbManager.hasPermission(ua)) {
			throw new SecurityException(
					"Do not have permission for this accessory");
		}

		mContext = context;
		mAccessory = ua;
	}

	/**
	 * Returns the file descriptor for this USB accessory
	 * 
	 * @return
	 */
	public ParcelFileDescriptor getDescriptor() throws IOException {
		if (mDescriptor != null) {
			return mDescriptor;
		}
		mDescriptor = mUsbManager.openAccessory(mAccessory);
		if (mDescriptor == null) {
			throw new IOException("Unable to open accessory");
		}
		return mDescriptor;
	}

	public void close() {
		if (mDescriptor != null) {
			try {
				mDescriptor.close();
			} catch (IOException e) {
			}
		}
		mDescriptor = null;
	}

	public UsbAccessory getUsbAccessory() {
		return mAccessory;
	}

}
