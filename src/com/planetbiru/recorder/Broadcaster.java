package com.planetbiru.recorder;

import com.planetbiru.web.ServerWebSocketServerAdmin;

public class Broadcaster extends Thread {

	@Override
	public void run()
	{
		while(SoundRecorder.isRecording())
		{
			ServerWebSocketServerAdmin.broadcastMessage(SoundRecorder.getStatus().toString());
			try 
			{
				Thread.sleep(490);
			} 
			catch (InterruptedException e) 
			{
				Thread.currentThread().interrupt();
			}
		}
		
		ServerWebSocketServerAdmin.broadcastMessage(SoundRecorder.getStatus().toString());
	}
}
