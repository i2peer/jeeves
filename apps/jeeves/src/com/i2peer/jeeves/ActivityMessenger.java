package com.i2peer.jeeves;

import com.i2peer.jeeves.connector.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

public final class ActivityMessenger extends Activity {

	private Messenger mService;

	private boolean mBound;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			mBound = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.motor_control);

		Button b = (Button) this.findViewById(R.id.motor_left);
		b.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startMotor();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, AccessoryService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	private void startMotor() {
		if (!mBound) {
			return;
		}

		Message msg = Message.obtain(null, AccessoryService.MSG_START_MOTOR, 0,
				0);
		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
