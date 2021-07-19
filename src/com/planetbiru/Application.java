package com.planetbiru;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.planetbiru.config.Config;
import com.planetbiru.config.PropertyLoader;
import com.planetbiru.user.WebUserAccount;
import com.planetbiru.util.FileNotFoundException;
import com.planetbiru.web.ServerWebAdmin;
import com.planetbiru.web.ServerWebSocketServerAdmin;

public class Application {
	private static ServerWebAdmin server = new ServerWebAdmin();
	private static ServerWebSocketServerAdmin serverWS;
	
	
	public static void main(String[] args)
	{
		WebUserAccount.load(Config.getUserSettingPath());
		
		PropertyLoader.load("/config/config.ini");
		
		server.start(80);
		InetSocketAddress address = new InetSocketAddress(8080);
		serverWS = new ServerWebSocketServerAdmin(address); 
		serverWS.start();
	}
	public static boolean loadConfig(String currentRootDirectoryPath, String fileName)
	{
		boolean loaded = false;
		try 
		{
			ConfigLoader.load(fileName);
			loaded = true;
		} 
		catch (FileNotFoundException e) 
		{
			try 
			{
				ConfigLoader.load(currentRootDirectoryPath+"/"+fileName);
				loaded = true;
			} 
			catch (FileNotFoundException e1) 
			{
				e1.printStackTrace();
			}
		}
		return loaded;	
	}
	public static void restartService() {
		if(serverWS != null)
		{
			try {
				serverWS.stop();
			} catch (IOException | InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		serverWS = null;
		
	}
}
