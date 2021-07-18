package com.planetbiru.web;

import java.io.IOException;

import com.planetbiru.config.Config;
import com.planetbiru.recorder.SoundRecorder;
import com.planetbiru.util.Utility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerWebManagerAudio implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		httpExchange.getResponseHeaders().add("Content-Type", "application/json");
		httpExchange.sendResponseHeaders(200, 2);
		httpExchange.getResponseBody().write("{}".getBytes());
		httpExchange.close();
		
		String path = httpExchange.getRequestURI().getPath();
		if(path.contains("audio/recording/start"))
		{
			System.out.println("Start recording");
			String audioPath = Config.getRecordingPath() + "/" + Utility.now("yyyy-MM-dd-HH-mm-ss")+".wav";
			int sampleRate = 48000;
			short bitDepth = 16;
			short channel = 2;
			SoundRecorder.startRecording(audioPath, sampleRate, bitDepth, channel);
		}
		else if(path.contains("audio/recording/stop"))
		{
			System.out.println("Stop recording");
			SoundRecorder.stopRecording();
		}
		else if(path.contains("audio/recording/pause"))
		{
			System.out.println("Pause recording");
			SoundRecorder.pauseRecording();
		}
		else if(path.contains("audio/recording/resume"))
		{
			System.out.println("Resume recording");
			SoundRecorder.resumeRecording();
		}
		
	}

}
