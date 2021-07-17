package com.planetbiru;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import com.planetbiru.config.Config;
import com.planetbiru.config.ConfigEmail;
import com.planetbiru.config.ConfigGeneral;
import com.planetbiru.config.PropertyLoader;
import com.planetbiru.user.WebUserAccount;
import com.planetbiru.util.FileConfigUtil;
import com.planetbiru.util.FileNotFoundException;
import com.planetbiru.util.ServerStatus;
import com.planetbiru.util.Utility;

public class ConfigLoader {
	private static Properties properties = new Properties();
	
	private ConfigLoader()
	{
		
	}
	
	public static void loadRelative(String configPath)
	{
		InputStream inputStream; 
		inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(configPath);
		if(inputStream != null) 
		{
			try 
			{
				ConfigLoader.properties.load(inputStream);
				
				for (Entry<Object, Object> entry : ConfigLoader.properties.entrySet()) {
				    String key = (String) entry.getKey();
				    String keyEnv = key.toUpperCase().replace(".", "_");
				    String value = (String) entry.getValue();		    
				    String valueEnv = System.getenv(keyEnv);
				    if(valueEnv != null)
				    {
				    	value = valueEnv;
				    }
				    ConfigLoader.properties.setProperty(key, value);			    
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
	}
	public static void load(String configPath) throws FileNotFoundException
	{
		configPath = FileConfigUtil.fixFileName(configPath);
		try(
				FileInputStream inputStream = new FileInputStream(configPath)
		) 
		{
			ConfigLoader.properties.load(inputStream);			
			for (Entry<Object, Object> entry : ConfigLoader.properties.entrySet()) {
			    String key = (String) entry.getKey();
			    String keyEnv = key.toUpperCase().replace(".", "_");
			    String value = (String) entry.getValue();		    
			    String valueEnv = System.getenv(keyEnv);
			    if(valueEnv != null)
			    {
			    	value = valueEnv;
			    }
			    ConfigLoader.properties.setProperty(key, value);			    
			}
		} 
		catch (IOException e) 
		{
			throw new FileNotFoundException(e.getMessage());
		}
	}
	
	public static void init()
	{
		
		String dhcpSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.dhcp");
		String wlanSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.wlan");
		String ethernetSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.ethernet");
		String dhcpSettingPathDefault = ConfigLoader.getConfig("otpbroker.path.setting.dhcp.default");
		String wlanSettingPathDefault = ConfigLoader.getConfig("otpbroker.path.setting.wlan.default");
		String ethernetSettingPathDefault = ConfigLoader.getConfig("otpbroker.path.setting.ethernet.default");
		String osWLANConfigPath = ConfigLoader.getConfig("otpbroker.path.os.wlan");
		String osSSIDKey = ConfigLoader.getConfig("otpbroker.path.os.ssid.key");
		String osEthernetConfigPath = ConfigLoader.getConfig("otpbroker.path.os.ethernet");
		String osDHCPConfigPath = ConfigLoader.getConfig("otpbroker.path.os.dhcp");
		String baseDirConfig = ConfigLoader.getConfig("otpbroker.path.base.setting");
		String rebootCommand = ConfigLoader.getConfig("otpbroker.ssh.reboot.command");
		
		String emailSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.email");
		
		String restartCommand = ConfigLoader.getConfig("otpbroker.ssh.restart.command");
		
		String deviceName = ConfigLoader.getConfig("otpbroker.device.name");
		String deviceVersion = ConfigLoader.getConfig("otpbroker.device.version");
		String sessionName = ConfigLoader.getConfig("otpbroker.web.session.name");
		long sessionLifetime = ConfigLoader.getConfigLong("otpbroker.web.session.lifetime");
		String sessionFilePath = ConfigLoader.getConfig("otpbroker.web.session.file.path");
		String documentRoot = ConfigLoader.getConfig("otpbroker.web.document.root");
		String mimeSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.all");
		String userSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.user");
		String generalSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.general");
		String cleanupCommand = ConfigLoader.getConfig("otpbroker.ssh.cleanup.command");
		String logDir = ConfigLoader.getConfig("otpbroker.log.dir");	
		String storageDir = ConfigLoader.getConfig("otpbroker.storage.dir");
		int serverPort = ConfigLoader.getConfigInt("server.port");

		boolean timeUpdate = ConfigLoader.getConfigBoolean("otpbroker.cron.enable.ntp");
		String serverStatusSettingPath = ConfigLoader.getConfig("otpbroker.path.setting.server.status");	
		
		String imageName = ConfigLoader.getConfig("otpbroker.image.name");
		boolean logConfigNotFound = ConfigLoader.getConfigBoolean("otpbroker.log.config.not.found");
	
		Config.setLogConfigNotFound(logConfigNotFound);
		Config.setImageName(imageName);

		
		Config.setEmailSettingPath(emailSettingPath);
		/**
		 * This configuration must be loaded first
		 */
		Config.setBaseDirConfig(baseDirConfig);
		Config.setDhcpSettingPath(dhcpSettingPath);
		Config.setWlanSettingPath(wlanSettingPath);
		Config.setEthernetSettingPath(ethernetSettingPath);
		Config.setDhcpSettingPathDefault(dhcpSettingPathDefault);
		Config.setWlanSettingPathDefault(wlanSettingPathDefault);
		Config.setEthernetSettingPathDefault(ethernetSettingPathDefault);			
		Config.setOsWLANConfigPath(osWLANConfigPath);
		Config.setOsSSIDKey(osSSIDKey);
		Config.setOsEthernetConfigPath(osEthernetConfigPath);
		Config.setOsDHCPConfigPath(osDHCPConfigPath);		
		Config.setRebootCommand(rebootCommand);
			
		Config.setBaseDirConfig(baseDirConfig);		
		Config.setUserSettingPath(userSettingPath);
		Config.setDocumentRoot(documentRoot);
		Config.setDeviceName(deviceName);
		Config.setDeviceVersion(deviceVersion);
		Config.setSessionFilePath(sessionFilePath);
		Config.setSessionName(sessionName);
		Config.setSessionLifetime(sessionLifetime);
		Config.setGeneralSettingPath(generalSettingPath);
		Config.setRestartCommand(restartCommand);
		Config.setCleanupCommand(cleanupCommand);
		Config.setMimeSettingPath(mimeSettingPath);
		Config.setLogDir(logDir);
		Config.setStorageDir(storageDir);
		Config.setServerPort(serverPort);
		Config.setTimeUpdate(timeUpdate);
		Config.setServerStatusSettingPath(serverStatusSettingPath);

		ServerStatus.load(Config.getServerStatusSettingPath());

		/**
		 * Override email setting if exists
		 */
		ConfigEmail.load(Config.getEmailSettingPath());
		ConfigGeneral.load(Config.getGeneralSettingPath());
		WebUserAccount.load(Config.getUserSettingPath());			
		PropertyLoader.load(Config.getMimeSettingPath());	
    	
	}

	public static String getConfig(String name) {
		String value = ConfigLoader.properties.getProperty(name);
		if(value == null)
		{
			value = "";
		}
		return value;
	}

	public static long getConfigLong(String name) {
		String value = ConfigLoader.properties.getProperty(name);
		if(value == null || value.isEmpty())
		{
			value = "0";
		}
		value = value.trim();
		return Utility.atol(value);
	}

	public static int getConfigInt(String name) {
		String value = ConfigLoader.properties.getProperty(name);
		if(value == null || value.isEmpty())
		{
			value = "0";
		}
		value = value.trim();
		return Utility.atoi(value);
	}

	public static boolean getConfigBoolean(String name) {
		String value = ConfigLoader.properties.getProperty(name);
		if(value == null || value.isEmpty())
		{
			value = "false";
		}
		value = value.trim().toLowerCase();
		return value.equals("true");
	}

}
