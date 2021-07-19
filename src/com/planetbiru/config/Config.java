package com.planetbiru.config;

public class Config {
	private static int serverPort = 80;
	private static long sessionLifetime = 14400000;
	private static String sessionName = "SESSID";
	private static String baseDirConfig = "C:/bitbucket/JavaAudioRecorder/resources";
	private static String documentRoot = "C:/bitbucket/JavaAudioRecorder/resources/www";
	private static String sessionFilePath = "/data/session";
	private static boolean logConfigNotFound = false;
	private static String dhcpSettingPath = "/data/network/dhcp.json";
	private static String logDir = "";
	private static String rebootCommand = "reboot";
	private static String cleanupCommand = "";
	private static String osEthernetConfigPath = "";
	private static String generalSettingPath = "/data/setting/general.json";
	private static String recordingPath = "C:/bitbucket/JavaAudioRecorder/resources/recording";
	private static String osDHCPConfigPath = "";
	private static String wlanSettingPath = "/data/network/ethernet.json";
	private static String osWLANConfigPath = "";
	private static String osSSIDKey = "";
	private static String ethernetSettingPath = "/data/network/ethernet.json";
	private static String storageDir = "C:/bitbucket/JavaAudioRecorder/resources/recording";
	private static String userSettingPath = "/data/user/user.json";	
	
	private static String imageName = "";
	private static String  emailSettingPath = "";
	private static String  dhcpSettingPathDefault = "";
	private static String  wlanSettingPathDefault = "";
	private static String  ethernetSettingPathDefault = "";
	private static String  deviceName = "";
	private static String  deviceVersion = "";
	private static String  restartCommand = "";
	private static String  mimeSettingPath = "";
	private static boolean  timeUpdate = true;
	private static boolean printMailConsole = true;
	private static String serverStatusSettingPath = "";

	
	public static boolean isPrintMailConsole() {
		return printMailConsole;
	}

	public static void setPrintMailConsole(boolean printMailConsole) {
		Config.printMailConsole = printMailConsole;
	}

	public static String getServerStatusSettingPath() {
		return serverStatusSettingPath;
	}

	public static void setServerStatusSettingPath(String serverStatusSettingPath) {
		Config.serverStatusSettingPath = serverStatusSettingPath;
	}

	public static String getImageName() {
		return imageName;
	}

	public static void setImageName(String imageName) {
		Config.imageName = imageName;
	}

	public static String getEmailSettingPath() {
		return emailSettingPath;
	}

	public static void setEmailSettingPath(String emailSettingPath) {
		Config.emailSettingPath = emailSettingPath;
	}

	public static String getDhcpSettingPathDefault() {
		return dhcpSettingPathDefault;
	}

	public static void setDhcpSettingPathDefault(String dhcpSettingPathDefault) {
		Config.dhcpSettingPathDefault = dhcpSettingPathDefault;
	}

	public static String getWlanSettingPathDefault() {
		return wlanSettingPathDefault;
	}

	public static void setWlanSettingPathDefault(String wlanSettingPathDefault) {
		Config.wlanSettingPathDefault = wlanSettingPathDefault;
	}

	public static String getEthernetSettingPathDefault() {
		return ethernetSettingPathDefault;
	}

	public static void setEthernetSettingPathDefault(String ethernetSettingPathDefault) {
		Config.ethernetSettingPathDefault = ethernetSettingPathDefault;
	}

	public static String getDeviceName() {
		return deviceName;
	}

	public static void setDeviceName(String deviceName) {
		Config.deviceName = deviceName;
	}

	public static String getDeviceVersion() {
		return deviceVersion;
	}

	public static void setDeviceVersion(String deviceVersion) {
		Config.deviceVersion = deviceVersion;
	}

	public static String getRestartCommand() {
		return restartCommand;
	}

	public static void setRestartCommand(String restartCommand) {
		Config.restartCommand = restartCommand;
	}

	public static String getMimeSettingPath() {
		return mimeSettingPath;
	}

	public static void setMimeSettingPath(String mimeSettingPath) {
		Config.mimeSettingPath = mimeSettingPath;
	}

	public static boolean isTimeUpdate() {
		return timeUpdate;
	}

	public static void setTimeUpdate(boolean timeUpdate) {
		Config.timeUpdate = timeUpdate;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public static void setServerPort(int serverPort) {
		Config.serverPort = serverPort;
	}

	public static long getSessionLifetime() {
		return sessionLifetime;
	}

	public static void setSessionLifetime(long sessionLifetime) {
		Config.sessionLifetime = sessionLifetime;
	}

	public static String getSessionName() {
		return sessionName;
	}

	public static void setSessionName(String sessionName) {
		Config.sessionName = sessionName;
	}

	public static String getBaseDirConfig() {
		return baseDirConfig;
	}

	public static void setBaseDirConfig(String baseDirConfig) {
		Config.baseDirConfig = baseDirConfig;
	}

	public static String getDocumentRoot() {
		return documentRoot;
	}

	public static void setDocumentRoot(String documentRoot) {
		Config.documentRoot = documentRoot;
	}

	public static String getSessionFilePath() {
		return sessionFilePath;
	}

	public static void setSessionFilePath(String sessionFilePath) {
		Config.sessionFilePath = sessionFilePath;
	}

	public static boolean isLogConfigNotFound() {
		return logConfigNotFound;
	}

	public static void setLogConfigNotFound(boolean logConfigNotFound) {
		Config.logConfigNotFound = logConfigNotFound;
	}

	public static String getDhcpSettingPath() {
		return dhcpSettingPath;
	}

	public static void setDhcpSettingPath(String dhcpSettingPath) {
		Config.dhcpSettingPath = dhcpSettingPath;
	}

	public static String getLogDir() {
		return logDir;
	}

	public static void setLogDir(String logDir) {
		Config.logDir = logDir;
	}

	public static String getRebootCommand() {
		return rebootCommand;
	}

	public static void setRebootCommand(String rebootCommand) {
		Config.rebootCommand = rebootCommand;
	}

	public static String getCleanupCommand() {
		return cleanupCommand;
	}

	public static void setCleanupCommand(String cleanupCommand) {
		Config.cleanupCommand = cleanupCommand;
	}

	public static String getOsEthernetConfigPath() {
		return osEthernetConfigPath;
	}

	public static void setOsEthernetConfigPath(String osEthernetConfigPath) {
		Config.osEthernetConfigPath = osEthernetConfigPath;
	}

	public static String getGeneralSettingPath() {
		return generalSettingPath;
	}

	public static void setGeneralSettingPath(String generalSettingPath) {
		Config.generalSettingPath = generalSettingPath;
	}

	public static String getRecordingPath() {
		return recordingPath;
	}

	public static void setRecordingPath(String recordingPath) {
		Config.recordingPath = recordingPath;
	}

	public static String getOsDHCPConfigPath() {
		return osDHCPConfigPath;
	}

	public static void setOsDHCPConfigPath(String osDHCPConfigPath) {
		Config.osDHCPConfigPath = osDHCPConfigPath;
	}

	public static String getWlanSettingPath() {
		return wlanSettingPath;
	}

	public static void setWlanSettingPath(String wlanSettingPath) {
		Config.wlanSettingPath = wlanSettingPath;
	}

	public static String getOsWLANConfigPath() {
		return osWLANConfigPath;
	}

	public static void setOsWLANConfigPath(String osWLANConfigPath) {
		Config.osWLANConfigPath = osWLANConfigPath;
	}

	public static String getOsSSIDKey() {
		return osSSIDKey;
	}

	public static void setOsSSIDKey(String osSSIDKey) {
		Config.osSSIDKey = osSSIDKey;
	}

	public static String getEthernetSettingPath() {
		return ethernetSettingPath;
	}

	public static void setEthernetSettingPath(String ethernetSettingPath) {
		Config.ethernetSettingPath = ethernetSettingPath;
	}

	public static String getStorageDir() {
		return storageDir;
	}

	public static void setStorageDir(String storageDir) {
		Config.storageDir = storageDir;
	}

	public static String getUserSettingPath() {
		return userSettingPath;
	}

	public static void setUserSettingPath(String userSettingPath) {
		Config.userSettingPath = userSettingPath;
	}
	
	
}
