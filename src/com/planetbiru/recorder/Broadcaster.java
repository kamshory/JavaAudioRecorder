package com.planetbiru.recorder;

import org.json.JSONObject;

import com.planetbiru.web.ServerWebSocketServerAdmin;

public class Broadcaster extends Thread {

	@Override
	public void run()
	{
		while(SoundRecorder.isRecording())
		{
			JSONObject info = new JSONObject();
			JSONObject data = new JSONObject();
			
			data.put("isRecording", SoundRecorder.isRecording());
			data.put("isPaused", SoundRecorder.isPaused());
			data.put("duration", SoundRecorder.getDuration());
			data.put("fileSize", SoundRecorder.getDataSize());
			
			info.put("command", "recording-status");
			info.put("data", data);
			String message = info.toString();
			ServerWebSocketServerAdmin.broadcastMessage(message);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
