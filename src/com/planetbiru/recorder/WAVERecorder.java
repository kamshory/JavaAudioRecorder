package com.planetbiru.recorder;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class WAVERecorder extends Thread {

	private AudioFormat audioFormat;
	private String path;
	private int bufferSize = 4096;
	private boolean separateFile = false;
	private String extension = ".wav";
	public WAVERecorder(String path, AudioFormat audioFormat, int bufferSize, boolean separateFile) {
		this.audioFormat = audioFormat;
		this.path = path;
		this.bufferSize = bufferSize;
		this.separateFile = separateFile;
	}

	@Override
	public void run()
	{
		this.recordSingleFile();
		if(this.separateFile)
		{
			separateFile();
		}
	}
	private void separateFile() {
		File fileInput = new File(this.path+this.extension);
		
		List<RandomAccessFile> output = new ArrayList<>();
		try(
				RandomAccessFile input = new RandomAccessFile(fileInput, "r")
		)
		{
			
			for(int i = 0; i<this.audioFormat.getChannels(); i++)
			{
				output.add(new RandomAccessFile(this.path+"-"+i+this.extension, "rw")); 
			}
			
			int singleChannelSize = (int) (SoundRecorder.getDataSize() / this.audioFormat.getChannels());
			for(int i = 0; i<this.audioFormat.getChannels(); i++)
			{
				writeString(output.get(i), "RIFF"); 
			    writeInt(output.get(i), 36 + singleChannelSize); 
			    writeString(output.get(i), "WAVE"); 
			    writeString(output.get(i), "fmt "); 
			    writeInt(output.get(i), 16); 
			    writeShort(output.get(i), (short) 1); 
			    writeShort(output.get(i), (short) 1); 
			    writeInt(output.get(i), (int) audioFormat.getSampleRate()); 
			    writeInt(output.get(i), (audioFormat.getSampleSizeInBits() * 1)); 
			    writeShort(output.get(i), (short) (audioFormat.getSampleSizeInBits() * 1 / 8));
			    writeShort(output.get(i), (short) audioFormat.getSampleSizeInBits()); 
			    writeString(output.get(i), "data"); 
			    writeInt(output.get(i), singleChannelSize); 
			}
			input.seek(44);
			int bsize = audioFormat.getSampleSizeInBits() / 8;
			byte[] buffer = new byte[bsize];
			int countRead = (int) (SoundRecorder.getDataSize() * 8 / (this.audioFormat.getChannels() * audioFormat.getSampleSizeInBits()) );
			for(int k = 0; k<countRead; k++)
			{
				for(int i = 0; i<this.audioFormat.getChannels(); i++)
				{
					input.read(buffer);
					output.get(i).write(buffer);
				}
			}
			for(int i = 0; i<this.audioFormat.getChannels(); i++)
			{
				if(output.get(i) != null)
				{
					output.get(i).close();
				}
			}
		}
		catch(IOException e)
		{
			/**
			 * Do nothing
			 */
		}
		finally {
			/**
			 * Do nothing
			 */
		}
	}

	private void recordSingleFile() {
		File file = new File(this.path+this.extension);		
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
			
			byte[] byteToRead = new byte[this.bufferSize];
			
			int flag;
			int dataSize = 0;
			int i = 0;
			while((flag = targetDataLine.read(byteToRead, 0, byteToRead.length))> 0 && SoundRecorder.isRecording()) 
			{

				if(!SoundRecorder.isPaused())
				{
					output.write(byteToRead);
					dataSize += flag;				
					if(i % 10 == 0)
					{
						writeInt(output, dataSize + 36, 4);
						writeInt(output, dataSize, 40);
						output.seek(output.length());
					}	
					SoundRecorder.setDataSize(dataSize);
					i++;
				}
			}			
			writeInt(output, dataSize + 36, 4);
			writeInt(output, dataSize, 40);
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
