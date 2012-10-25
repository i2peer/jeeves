package com.i2peer.jeeves.protocol;

import com.i2peer.jeeves.accessories.Accessory;

public class MotorPacket extends Packet {

	private int mSpeed;

	private int mId;

	public MotorPacket(Accessory ua) {
		super(ua);
	}

	public void setSpeed(int speed) {
		mSpeed = speed;
	}

	public void setId(int id) {
		mId = id;
	}

	@Override
	public int getControlId() {
		return 0x0;
	}

	@Override
	public byte[] getData() {
		byte[] data = new byte[2];
		data[0] = (byte) mId;
		data[1] = (byte) mSpeed;
		return data;
	}
}
