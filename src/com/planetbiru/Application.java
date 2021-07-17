package com.planetbiru;

import com.planetbiru.config.Config;
import com.planetbiru.user.WebUserAccount;
import com.planetbiru.util.FileNotFoundException;
import com.planetbiru.web.ServerWebAdmin;

public class Application {
	private static ServerWebAdmin server = new ServerWebAdmin();
	
	
	
	public static void main(String[] args)
	{
		WebUserAccount.load(Config.getUserSettingPath());
		server.start(80);
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
		// TODO Auto-generated method stub
		
	}
}
