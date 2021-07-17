package com.planetbiru.recorder;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class WAVERecorder extends Thread {

	private AudioFormat audioFormat;
	private String path;
	private boolean running = true;

	public WAVERecorder(String path, AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
		this.path = path;
	}

	public void stopRecording() {
		this.running = false;
		
	}
	@Override
	public void run()
	{
		File file = new File(this.path);
		
		TargetDataLine targetDataLine;
		
		try (
				RandomAccessFile output = new RandomAccessFile(file, "rw"))
		{
			
			byte[] rawData = "".getBytes();
			
			writeString(output, "RIFF"); 
		    writeInt(output, 36 + rawData.length); 
		    writeString(output, "WAVE"); 
		    writeString(output, "fmt "); 
		    writeInt(output, 16); 
		    writeShort(output, (short) 1); 
		    writeShort(output, (short) audioFormat.getChannels()); 
		    writeInt(output, (int) audioFormat.getSampleRate()); 
		    writeInt(output, (audioFormat.getSampleSizeInBits() * audioFormat.getChannels())); 
		    writeShort(output, (short) (audioFormat.getSampleSizeInBits() * audioFormat.getChannels() / 8));
		    writeShort(output, (short) audioFormat.getSampleSizeInBits()); 
		    writeString(output, "data"); 
		    writeInt(output, rawData.length); 		
			
			targetDataLine = AudioSystem.getTargetDataLine(this.audioFormat);
			targetDataLine.open();
			targetDataLine.start();
			
			byte[] byteToRead = new byte[256];
			
			int flag;
			int size = 0;
			int i = 0;
			while((flag = targetDataLine.read(byteToRead, 0, byteToRead.length))>0 && this.running) 
			{
				//Collect data from the sound card
				output.write(byteToRead);
				size += flag;
				
				if(i % 10 == 0)
				{
					writeInt(output, size + 36, 4);
					writeInt(output, size, 40);
					output.seek(output.length());
				}
				
				i++;
			}			
			writeInt(output, size + 36, 4);
			writeInt(output, size, 40);
			targetDataLine.stop();
			targetDataLine.close();
		} 
		catch (LineUnavailableException | IOException e) 
		{
			e.printStackTrace();
		}

	}
	private void writeInt(RandomAccessFile output, int value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
		output.write(value >> 16);
		output.write(value >> 24);		
	}

	private void writeShort(RandomAccessFile output, short value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
	}

	private void writeString(RandomAccessFile output, String value) throws IOException {
		for (int i = 0; i < value.length(); i++) {
		    output.write(value.charAt(i));
	    }
	}
	private void writeInt(RandomAccessFile output, int value, int foffset) throws IOException {
		output.seek(foffset);
		output.write(value >> 0);
		output.write(value >> 8);
		output.write(value >> 16);
		output.write(value >> 24);
	}

	@SuppressWarnings("unused")
	private void writeShort(RandomAccessFile output, short value, int foffset) throws IOException {
		output.seek(foffset);
		output.write(value >> 0);
		output.write(value >> 8);
	}

	@SuppressWarnings("unused")
	private void writeString(RandomAccessFile output, String value, int foffset) throws IOException {
		output.seek(foffset);
		for (int i = 0; i < value.length(); i++) {
		    output.write(value.charAt(i));
	    }
	}
}
