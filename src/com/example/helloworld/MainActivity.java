package com.example.helloworld;

import com.example.helloworld.UdpService.UdpBinder;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	private Button mTest;
	private UdpService udpService;
	private boolean mBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		mTest = (Button) findViewById(R.id.test_button);
		
		mTest.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				sendTestUdpMessage();
			}
		});
		
		Intent intent = new Intent(this, UdpService.class);
		startService(intent);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Intent intent = new Intent(this, UdpService.class);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    if (mBound) {
	        unbindService(mConnection);
	        mBound = false;
	    }
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Intent intent = new Intent(this, UdpService.class);
		stopService(intent);
		udpService = null;
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName name, IBinder service) {
	        try {
	            UdpBinder binder = (UdpBinder) service;
	            udpService = (UdpService) binder.getService();
	            mBound = true;
	        } catch (ClassCastException e) {
	            // Pass
	        }
	    }

	    @Override
	    public void onServiceDisconnected(ComponentName name) {
	        mBound = false;
	    }
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	private void sendTestUdpMessage(){
		if (udpService != null){
			udpService.sendMessageOverSocketSend("test");
		}else{
			Toast.makeText(getApplicationContext(), "service null", Toast.LENGTH_LONG).show();
		}
	}

}
