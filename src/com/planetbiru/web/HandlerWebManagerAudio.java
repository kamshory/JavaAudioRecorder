package com.planetbiru.web;

import java.io.IOException;

import org.json.JSONObject;

import com.planetbiru.config.Config;
import com.planetbiru.recorder.SoundRecorder;
import com.planetbiru.util.Utility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerWebManagerAudio implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		byte[] responseBody;
		httpExchange.getResponseHeaders().add("Content-Type", "application/json");
		
		String path = httpExchange.getRequestURI().getPath();
		if(path.contains("audio/recording/start"))
		{
			System.out.println("Start recording");
			String audioPath = Config.getRecordingPath() + "/" + Utility.now("yyyy-MM-dd-HH-mm-ss");
			int sampleRate = 48000;
			short bitDepth = 16;
			short channel = 2;
			int bufferSize = 4096;
			boolean splitFile = true;
			SoundRecorder.startRecording(audioPath, sampleRate, bitDepth, channel, bufferSize, splitFile);
			
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
		else if(path.contains("audio/recording/status"))
		{
			System.out.println("Status recording");
		}
		JSONObject info = SoundRecorder.getStatus();
		responseBody = info.toString().getBytes();
		httpExchange.sendResponseHeaders(200, responseBody.length);
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();
		
	}

}
