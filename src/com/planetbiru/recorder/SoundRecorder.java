package com.planetbiru.recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class SoundRecorder {
	private static WAVERecorder recorder;
	
	public static void startRecording(String path, int sampleRate, short bitDepth, short channel)
	{
		//Sampling Rate 
		 // encoding format PCM
		Encoding audioFormatEncoding = Encoding.PCM_SIGNED;
		 //Frame size 16 
		 // Is the big end?
		boolean bigEndian = true;
		 //Number of channels
		
		AudioFormat audioFormat = new AudioFormat(audioFormatEncoding, sampleRate, bitDepth, channel, (bitDepth / 8) * channel, sampleRate, bigEndian);
		recorder = new WAVERecorder(path, audioFormat);
		recorder.start();
		
		
	}
	public static void stopRecording()
	{
		recorder.stopRecording();
	}
}
