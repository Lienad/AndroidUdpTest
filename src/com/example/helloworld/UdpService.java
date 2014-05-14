package com.example.helloworld;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UdpService extends Service{
	
	private DatagramSocket socketSend;
	private final int SEND_PORT = 1860;
	private final String SERVER_IP_ADDRESS = "24.246.87.155";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		UdpBinder udpBinder = new UdpBinder();
		return udpBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initializeSocketSend();
		Log.i("UDP", "Service started");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		if (socketSend != null){
			socketSend.close();
		}
	}
	
	private void initializeSocketSend(){
		try {
			InetAddress serverAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
			socketSend = new DatagramSocket(SEND_PORT);
			Toast.makeText(getApplicationContext(), "Socket opened", Toast.LENGTH_SHORT).show();
		} catch (SocketException | UnknownHostException e) {
			Toast.makeText(getApplicationContext(), "failed to open socket: " + e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	public void sendMessageOverSocketSend(String udpMessage){
		new SendUdpMessage(socketSend).execute(udpMessage);
	}
	
	public class SendUdpMessage extends AsyncTask<String, Integer, Integer>{
		
		private final int RESULT_OK = 0;
		private final int RESULT_FAIL = -1;
		
		private DatagramSocket socketSend;
		
		public SendUdpMessage(DatagramSocket socketSend){
			this.socketSend = socketSend;
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			int result = RESULT_OK;
			if (params.length > 0){
				String udpMessage = params[0];
				result = sendUdpMessageOverSocket(socketSend, udpMessage);
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Integer result){
			Toast.makeText(getApplicationContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
		}
		
		private int sendUdpMessageOverSocket(DatagramSocket socketSend, String udpMessage){
			byte[] data = udpMessage.getBytes();
			InetAddress serverAddress;
			try {
				serverAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return  RESULT_FAIL;
			}
			DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, SEND_PORT);
			try {
				socketSend.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
				return RESULT_FAIL;
			} catch (NullPointerException e){
				return RESULT_FAIL;
			}
			return RESULT_OK;
		}
		
	}
	
	public class UdpBinder extends Binder{
		UdpService getService(){
			return UdpService.this;
		}
	}

}
