package com.planetbiru.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.planetbiru.constant.JsonKey;
import com.planetbiru.web.ServerWebSocketServerAdmin;

public class ServerInfo {
	
	private static final String TOTAL = "total";
	private static final String USED = "used";
	private static final String FREE = "free";
	private static final String RAM = "ram";
	private static final String SWAP = "swap";
	private static final String PERCENT_USED = "percentUsed";

	private ServerInfo()
	{
		
	}
	
	public static void sendWSStatus(boolean connected, String message) 
    {
		JSONArray data = new JSONArray();
		JSONObject info = new JSONObject();
		
		JSONObject ws = new JSONObject();
		ws.put(JsonKey.NAME, "otp-ws-connected");
		ws.put(JsonKey.VALUE, connected);
		ws.put(JsonKey.MESSAGE, message);
		data.put(ws);
		
		info.put(JsonKey.COMMAND, "server-info");
		info.put(JsonKey.DATA, data);
	
		ServerWebSocketServerAdmin.broadcastMessage(info.toString(4));				
	}
	
	public static void sendWSStatus(boolean connected) {
		ServerInfo.sendWSStatus(connected, "");		
	}
	
	public static void sendAMQPStatus(boolean connected)
	{
		JSONArray data = new JSONArray();
		JSONObject info = new JSONObject();
		
		JSONObject ws = new JSONObject();
		ws.put(JsonKey.NAME, "otp-amqp-connected");
		ws.put(JsonKey.VALUE, connected);
		data.put(ws);
		
		info.put(JsonKey.COMMAND, "server-info");
		info.put(JsonKey.DATA, data);
	
		ServerWebSocketServerAdmin.broadcastMessage(info.toString(4));
	}

	

	private static String cacheServerInfo = "";
	private static long cacheServerInfoExpire = 0;
	private static long cacheLifetime = 5000;
	public static String getInfo() {
		JSONObject info = new JSONObject();
		info.put("cpu", ServerInfo.cpuTemperatureInfo());
		info.put("storage", ServerInfo.storageInfo());
		info.put("memory", ServerInfo.memoryInfo());
		return info.toString();
	}
	
	public static JSONObject memoryInfo()
	{
		String command = "free";
		String result = CommandLineExecutor.exec(command).toString();
		
		
		result = fixingRawData(result);

		JSONObject info = new JSONObject();
		
		String[] lines = result.split("\r\n");
		for(int i = 0; i<lines.length;i++)
		{
			lines[i] = lines[i].replaceAll("\\s+", " ").trim();
			if(lines[i].contains("Mem:"))
			{
				String[] arr2 = lines[i].split(" ");
				if(arr2.length >= 4)
				{
					String totalStr = arr2[1];
					String usedStr = arr2[2];
					String freeStr = arr2[3];		
					double total = Utility.atof(totalStr);
					double used = Utility.atof(usedStr);
					double free = Utility.atof(freeStr);
					double percentUsed  = 100 * used/total;
					JSONObject ram = new JSONObject();
					
					ram.put(ServerInfo.TOTAL, total);
					ram.put(ServerInfo.USED, used);
					ram.put(ServerInfo.FREE, free);				
					ram.put(ServerInfo.PERCENT_USED, percentUsed);				
					info.put(ServerInfo.RAM, ram);
				}
			}
			
			if(lines[i].contains("Swap:"))
			{
				String[] arr2 = lines[i].split(" ");
				if(arr2.length >= 4)
				{
					String totalStr = arr2[1];
					String usedStr = arr2[2];
					String freeStr = arr2[3];		
					int total = Utility.atoi(totalStr);
					int used = Utility.atoi(usedStr);
					int free = Utility.atoi(freeStr);
					float percentUsed  = 100 * ((float)used/(float)total);
					JSONObject swap = new JSONObject();
					
					swap.put(ServerInfo.TOTAL, total);
					swap.put(ServerInfo.USED, used);
					swap.put(ServerInfo.FREE, free);				
					swap.put(ServerInfo.PERCENT_USED, percentUsed);				
					info.put(ServerInfo.SWAP, swap);
				}
			}
		}
		return info;
	}
	
	
	
	
	public static JSONObject storageInfo()
	{
		String command = "df -h";
		String result = CommandLineExecutor.exec(command).toString();
		result = fixingRawData(result);	
		String[] lines = result.split("\r\n");	
		JSONObject info = new JSONObject();	
		if(lines.length > 1)
		{
			for(int i = 1; i<lines.length;i++)
			{
				lines[i] = lines[i].replaceAll("\\s+", " ").trim();
				String[] arr2 = lines[i].split(" ", 6);
				if(arr2.length >= 6 && arr2[5].equals("/"))
				{
					String total = arr2[1];
					String used = arr2[2];
					String avail = arr2[3];
					String percent = arr2[4];
					
					double factorTotal = getFactor(total);
					double factorUsed = getFactor(used);
					double factorAvail = getFactor(avail);
					
					info.put(ServerInfo.TOTAL, Utility.atof(total) * factorTotal);
					info.put(ServerInfo.USED, Utility.atof(used) * factorUsed);
					info.put("available", Utility.atof(avail) * factorAvail);
					info.put(ServerInfo.PERCENT_USED, Utility.atof(percent));
				}
			}
		}	
		return info;
	}
	
	public static double getFactor(String value)
	{
		double factor = 1;
		if(value.contains("G"))
		{
			factor = 1000000;
		}
		else if(value.contains("M"))
		{
			factor = 1000;
		}
		else if(value.toUpperCase().contains("K"))
		{
			factor = 1;
		}
		return factor;
	}
	
	public static String fixingRawData(String result)
	{
		result = result.replace("\n", "\r\n");
		result = result.replace("\r\r\n", "\r\n");
		result = result.replace("\r", "\r\n");
		result = result.replace("\r\n\n", "\r\n");
		return result;
	}
	
	public static JSONObject cpuTemperatureInfo()
	{
		String command = "/bin/sensors";
		String result = CommandLineExecutor.exec(command).toString();
		result = result.replace("??", "&deg;");
		result = fixingRawData(result);
		
		String adapter = getCPUSensorAdapter(result);
		
		JSONArray cores = getCPUTemperatureCore(result);
		
		JSONObject info = new JSONObject();
		info.put("adapter", adapter);
		info.put("temperature", cores);
		info.put("usage", cpuUsage());
		return info;
	}
	
	public static JSONObject cpuTemperatureInfo2()
	{
		String command = "sensors";
		String result = CommandLineExecutor.exec(command).toString();

		
		result = result.replace("??", "&deg;");
		result = fixingRawData(result);
		
		String adapter = getCPUSensorAdapter(result);
		
		JSONArray cores = getCPUTemperatureCore(result);
		
		JSONObject info = new JSONObject();
		info.put("adapter", adapter);
		info.put("temperature", cores);
		info.put("usage", cpuUsage());
		return info;
	}
	
	public static JSONObject cpuUsage()
	{
		String command = "/bin/mpstat";
		String result = CommandLineExecutor.exec(command).toString();
		

		result = fixingRawData(result);
		result = result.replace("\r\n\r\n", "\r\n");
		
		JSONObject info = new JSONObject();
		String[] lines = result.split("\r\n");
		String iddle = "0";
		if(lines.length > 1)
		{
			String[] keys = new String[1];
			String[] values = new String[1];
			Map<String, String> pairs = new HashMap<>();
			for(int i = 0; i<lines.length;i++)
			{
				lines[i] = lines[i].replaceAll("\\s+", " ").trim();
				if(lines[i].contains("CPU "))
				{
					keys = lines[i].split(" ");
				}
				if(lines[i].contains("all "))
				{
					values = lines[i].split(" ");
				}
			}
			for(int i = 0; i<values.length && i <keys.length; i++)
			{
				pairs.put(keys[i].replace("%", ""), values[i]);
			}
			iddle = pairs.getOrDefault("idle", "0");
		}
		double idle = Utility.atof(iddle);
		double used = 100 - idle;
		info.put("idle", idle);
		info.put(ServerInfo.USED, used);
		info.put(ServerInfo.PERCENT_USED, used);
		return info;	
	}

	private static String getCPUSensorAdapter(String result) {
		
		String[] lines = result.split("\r\n");
		String adapter = "";
		for(int i = 0; i<lines.length;i++)
		{
			if(lines[i].contains("Adapter:"))
			{
				String[] arr2 = lines[i].split("\\:", 2);
				if(arr2.length == 2)
				{
					adapter = arr2[1].trim();
				}
			}
		}
		return adapter;
	}
	
	public static JSONArray getCPUTemperature(String result) {
		String[] lines = result.split("\r\n");
		JSONArray cores = new JSONArray();
		for(int i = 0; i<lines.length;i++)
		{
			
			if(lines[i].contains(":") && !lines[i].contains("Adapter"))
			{
				String[] arr2 = lines[i].split("\\:", 2);
				if(arr2.length == 2)
				{
					JSONObject core = getCPUTemperature(arr2);
					if(core != null)
					{
						cores.put(core);
					}
				}
			}
		}
		return cores;
	}

	public static JSONArray getCPUTemperatureCore(String cpuInfo) {
		String[] arr3 = cpuInfo.split("\r\n");
		JSONArray temp = new JSONArray();
		for(int i = 0; i<arr3.length; i++)
		{
			arr3[i] = arr3[i].replaceAll("\\s+", " ").trim();
			if(arr3[i].contains("temp1:"))
			{
				String[] arr4 = arr3[i].split(" ");
				if(arr4.length > 1)
				{
					JSONObject core = new JSONObject();
					JSONObject raw = new JSONObject();
					JSONObject value = new JSONObject();
					String currentTemperatureentTemp = arr4[1];
					raw.put("currentTemperature", currentTemperatureentTemp.replace("+", ""));				
					value.put("currentTemperature", Utility.atof(currentTemperatureentTemp));
					
					core.put("label", "Core 0");
					core.put("raw", raw);
					core.put("value", value);
					temp.put(core);
				}
			}
		}
		return temp;
	}
	public static JSONArray getOpenPort() {
		JSONArray info = new JSONArray();
		String command = "/sbin/lsof -i -P -n";
		String result = CommandLineExecutor.exec(command).toString();
		result = fixingRawData(result).trim();
		String[] arr = result.split("\r\n");
		if(arr.length > 1)
		{
			String[] arr1 = arr[0].replaceAll("\\s+", " ").trim().split(" ", 9);
			for(int i = 0; i<arr1.length; i++)
			{
				arr1[i] = arr1[i].replace("/", "_").replace(" ", "_").toLowerCase().trim();
			}
			for(int j = 1; j < arr.length; j++)
			{
				JSONObject item = parseOpenPortLine(arr, arr1, j);
				if(item != null)
				{
					info.put(item);
				}
			}
		}
		return info;		
	}
	private static JSONObject parseOpenPortLine(String[] arr, String[] arr1, int j) {
		String[] arr2 = arr[j].replaceAll("\\s+", " ").trim().split(" ", 9);
		for(int i = 0; i<arr2.length; i++)
		{
			arr2[i] = arr2[i].trim();
		}
		if(arr1.length >= 9 && arr2.length >= 9)
		{
			JSONObject item = new JSONObject();
			for(int k = 0; k<9; k++)
			{
				item.put(arr1[k], arr2[k]);
			}
			item.put("port", extractPort(item.optString("name", "")));
			String sizeOff = item.optString("size_off", "");
			if(sizeOff.contains("t"))
			{
				String[] arr3 = sizeOff.split("t");
				int size = Utility.atoi(arr3[0]);
				int offset = Utility.atoi(arr3[1]);
				item.put("size", size);
				item.put("offset", offset);
			}
			return item;
		}
		return null;
	}

	public static int extractPort(String nodeNmae)
	{
		int port = 0;
		String[] arr = nodeNmae.split(" ");
		for(int i = 0; i<arr.length; i++)
		{
			if(arr[i].contains(":"))
			{
				String[] p = arr[i].split("\\:", 2);
				port = Utility.atoi(p[1]);
			}
		}
		return port;
	}
	public static JSONObject getCPUTemperature(String[] arr2) {
		String cpuLabel = arr2[0].trim();
		String cpuInfo = arr2[1].trim();
		cpuInfo = cpuInfo.replaceAll("\\s+"," ");
		cpuInfo = cpuInfo.replace("(", " ");
		cpuInfo = cpuInfo.replace(")", " ");
		String[] arr3 = cpuInfo.split(" ", 2);
		JSONObject core = null;
		if(arr3.length == 2)
		{
			String currentTemperatureentTemp = arr3[0];
			String[] arr4 = arr3[1].split("\\,");
			String high = "";
			String crit = "";
			
			for(int j = 0; j < arr4.length; j++)
			{
				if(arr4[j].contains("high"))
				{
					high = arr4[j];
					high = high.replace("high", "");
					high = high.replace("=", "");
					high = high.replaceAll("\\s+", "");
				}
				if(arr4[j].contains("crit"))
				{
					crit = arr4[j];
					crit = crit.replace("crit", "");
					crit = crit.replace("=", "");
					crit = crit.replaceAll("\\s+", "");
				}
			}
			core = new JSONObject();
			JSONObject raw = new JSONObject();
			JSONObject value = new JSONObject();
			
			raw.put("currentTemperature", currentTemperatureentTemp.replace("+", ""));
			raw.put("hightTemperature", high.replace("+", ""));
			raw.put("criticalTemperature", crit.replace("+", ""));
			
			value.put("currentTemperature", Utility.atof(currentTemperatureentTemp));
			value.put("hightTemperature", Utility.atof(high));
			value.put("criticalTemperature", Utility.atof(crit));
			
			core.put("label", cpuLabel);
			core.put("raw", raw);
			core.put("value", value);
		}
		return core;
	}

	public static String getCacheServerInfo() {
		return cacheServerInfo;
	}

	public static void setCacheServerInfo(String cacheServerInfo) {
		ServerInfo.cacheServerInfo = cacheServerInfo;
	}

	public static long getCacheServerInfoExpire() {
		return cacheServerInfoExpire;
	}

	public static void setCacheServerInfoExpire(long cacheServerInfoExpire) {
		ServerInfo.cacheServerInfoExpire = cacheServerInfoExpire;
	}

	public static long getCacheLifetime() {
		return cacheLifetime;
	}

	public static void setCacheLifetime(long cacheLifetime) {
		ServerInfo.cacheLifetime = cacheLifetime;
	}
}






