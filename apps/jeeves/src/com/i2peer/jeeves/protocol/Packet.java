package com.i2peer.jeeves.protocol;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.i2peer.jeeves.accessories.Accessory;

import android.os.ParcelFileDescriptor;

public abstract class Packet {

	private final Accessory mAccessory;

	public Packet(Accessory ua) {
		if (ua == null) {
			throw new IllegalArgumentException("ua is null");
		}
		mAccessory = ua;
	}

	public abstract int getControlId();

	public abstract byte[] getData();

	public void close() {

	}

	public void send() throws IOException {
		byte[] data = getData();
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}
		ParcelFileDescriptor mFileDescriptor = mAccessory.getDescriptor();

		if (mFileDescriptor == null) {
			throw new IllegalArgumentException("descriptor is null");
		}

		OutputStream os = new FileOutputStream(
				mFileDescriptor.getFileDescriptor());
		os.write(getControlId());
		os.write(data.length);
		os.write(data);

		os.close();
		mFileDescriptor.close();
	}
}
