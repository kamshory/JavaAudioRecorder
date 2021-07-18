package com.planetbiru.recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class SoundRecorder {
	
	private static WAVERecorder recorder;
	private static Broadcaster broadcaster;
	
	private static boolean recording = false;
	private static boolean paused = false;
	private static long dataSize = 0;
	private static int sampleRate;
	private static short bitDepth;
	private static short channel;
	private static int frameSize;

	private SoundRecorder()
	{
		
	}
	public static void startRecording(String path, int sampleRate, short bitDepth, short channel)
	{
		paused = false;			
		if(!recording)
		{
			int frameSize = (bitDepth / 8) * channel;
			SoundRecorder.sampleRate = sampleRate;
			SoundRecorder.bitDepth = bitDepth;
			SoundRecorder.channel = channel;
			SoundRecorder.frameSize = frameSize;
			
			dataSize = 0;
			broadcaster = new Broadcaster(); 
			Encoding audioFormatEncoding = Encoding.PCM_SIGNED;
			boolean bigEndian = true;
			AudioFormat audioFormat = new AudioFormat(audioFormatEncoding, sampleRate, bitDepth, channel, frameSize, sampleRate, bigEndian);
			recording = true;
			recorder = new WAVERecorder(path, audioFormat);
			recorder.start();
			broadcaster.start();
		}
	}
	public static void pauseRecording()
	{
		paused = true;
	}
	public static void resumeRecording()
	{
		paused = false;
	}
	public static void stopRecording()
	{
		recording = false;
	}
	
	public static long getDuration() {
		return (long) (((float) dataSize / (float) (sampleRate * frameSize)) * 1000);
	}
	public static void setDataSize(long dataSize) {
		SoundRecorder.dataSize = dataSize;
	}
	public static WAVERecorder getRecorder() {
		return recorder;
	}
	public static void setRecorder(WAVERecorder recorder) {
		SoundRecorder.recorder = recorder;
	}
	public static Broadcaster getBroadcaster() {
		return broadcaster;
	}
	public static void setBroadcaster(Broadcaster broadcaster) {
		SoundRecorder.broadcaster = broadcaster;
	}
	public static boolean isRecording() {
		return recording;
	}
	public static void setRecording(boolean recording) {
		SoundRecorder.recording = recording;
	}
	public static boolean isPaused() {
		return paused;
	}
	public static void setPaused(boolean paused) {
		SoundRecorder.paused = paused;
	}
	public static int getSampleRate() {
		return sampleRate;
	}
	public static void setSampleRate(int sampleRate) {
		SoundRecorder.sampleRate = sampleRate;
	}
	public static short getBitDepth() {
		return bitDepth;
	}
	public static void setBitDepth(short bitDepth) {
		SoundRecorder.bitDepth = bitDepth;
	}
	public static short getChannel() {
		return channel;
	}
	public static void setChannel(short channel) {
		SoundRecorder.channel = channel;
	}
	public static int getFrameSize() {
		return frameSize;
	}
	public static void setFrameSize(int frameSize) {
		SoundRecorder.frameSize = frameSize;
	}
	public static long getDataSize() {
		return dataSize;
	}
	
	
}
