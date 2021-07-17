package com.planetbiru.web;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.planetbiru.DeviceAPI;
import com.planetbiru.config.Config;
import com.planetbiru.config.ConfigGeneral;
import com.planetbiru.config.ConfigNetDHCP;
import com.planetbiru.config.ConfigNetEthernet;
import com.planetbiru.config.ConfigNetWLAN;
import com.planetbiru.constant.ConstantString;
import com.planetbiru.constant.JsonKey;
import com.planetbiru.cookie.CookieServer;
import com.planetbiru.user.NoUserRegisteredException;
import com.planetbiru.user.User;
import com.planetbiru.user.WebUserAccount;
import com.planetbiru.util.FileConfigUtil;
import com.planetbiru.util.FileNotFoundException;
import com.planetbiru.util.FileUtil;
import com.planetbiru.util.Utility;
import com.planetbiru.util.WebManagerContent;
import com.planetbiru.util.WebManagerTool;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerWebManager implements HttpHandler {

	private static Logger logger = Logger.getLogger(HandlerWebManager.class);
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		long from = System.nanoTime();
		String path = httpExchange.getRequestURI().getPath();
		String method = httpExchange.getRequestMethod();
		if(path.endsWith(".html") && method.equals("POST"))
		{
			this.handlePost(httpExchange, path);
		}
		WebResponse response = this.handleGet(httpExchange, path);
		long length = 0;
		if(response.getResponseBody() != null)
		{
			length = response.getResponseBody().length;
		}
		httpExchange.sendResponseHeaders(response.getStatusCode(), length);	
		if(response.getResponseBody() != null)
		{
			httpExchange.getResponseBody().write(response.getResponseBody());
		}
		httpExchange.close();
		long dur = System.nanoTime() - from;
		logger.info("Finish in "+dur+" : "+(dur/1000000)+" ms");
	}

	private WebResponse handleGet(HttpExchange httpExchange, String path) {
		return this.serveDocumentRoot(httpExchange, path);
	}

	private WebResponse serveDocumentRoot(HttpExchange httpExchange, String path) {
		if(path.equals("/"))
		{
			path = "/index.html";
		}
		WebResponse response = new WebResponse();
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		int statusCode = HttpStatus.OK;		
		String fileName = WebManagerTool.getFileName(path);
		byte[] responseBody = "".getBytes();
		try 
		{
			responseBody = FileUtil.readResource(fileName);
		} 
		catch (FileNotFoundException e) 
		{
			statusCode = HttpStatus.NOT_FOUND;
			if(fileName.endsWith(ConstantString.EXT_HTML))
			{
				try 
				{
					responseBody = FileUtil.readResource(WebManagerTool.getFileName("/404.html"));
				} 
				catch (FileNotFoundException e1) 
				{
					/**
					 * Do nothing
					 */
					responseBody = e.getMessage().getBytes();
				}
			}
		}
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());		
		WebManagerContent newContent = this.updateContent(fileName, responseHeaders, responseBody, statusCode, cookie);	
		responseBody = newContent.getResponseBody();
		responseHeaders = newContent.getResponseHeaders();
		statusCode = newContent.getStatusCode();
		String contentType = HttpUtil.getMIMEType(fileName);
		
		responseHeaders.add(ConstantString.CONTENT_TYPE, contentType);
		
		if(fileName.endsWith(ConstantString.EXT_HTML))
		{
			cookie.saveSessionData();
		}
		else
		{
			int lifetime = HttpUtil.getCacheLifetime(fileName);
			if(lifetime > 0)
			{
				responseHeaders.add(ConstantString.CACHE_CONTROL, "public, max-age="+lifetime+", immutable");				
			}
		}
		response.setResponseHeaders(responseHeaders);
		response.setStatusCode(statusCode);
		response.setResponseBody(responseBody);
		
		return response;
	}

	private WebManagerContent updateContent(String fileName, Headers responseHeaders, byte[] responseBody, int statusCode, CookieServer cookie) {
		System.out.println(fileName);
		String contentType = HttpUtil.getMIMEType(fileName);
		WebManagerContent webContent = new WebManagerContent(fileName, responseHeaders, responseBody, statusCode, cookie, contentType);
		boolean requireLogin = false;
		String fileSub = "";
		
		if(fileName.toLowerCase().endsWith(ConstantString.EXT_HTML))
		{
			JSONObject authFileInfo = WebManagerTool.processAuthFile(responseBody);
			requireLogin = authFileInfo.optBoolean(JsonKey.CONTENT, false);
			fileSub = WebManagerTool.getFileName(authFileInfo.optString("data-file", ""));
		}
		
		String username = cookie.getSessionData().optString(JsonKey.USERNAME, "");
		String password = cookie.getSessionData().optString(JsonKey.PASSWORD, "");
		if(requireLogin)
		{
			responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);
			webContent.setResponseHeaders(responseHeaders);
			try
			{
				if(!WebUserAccount.checkUserAuth(username, password))	
				{
					try 
					{
						responseBody = FileUtil.readResource(fileSub);
						return this.updateContent(fileSub, responseHeaders, responseBody, statusCode, cookie);
					} 
					catch (FileNotFoundException e) 
					{
						statusCode = HttpStatus.NOT_FOUND;
						webContent.setStatusCode(statusCode);
					}	
				}
				responseBody = WebManagerTool.removeMeta(responseBody);
			}
			catch(NoUserRegisteredException e)
			{
				/**
				 * Do nothing
				 */
				statusCode = HttpStatus.PERMANENT_REDIRECT;
				webContent.setStatusCode(statusCode);
				
				responseHeaders.add(ConstantString.LOCATION, ConstantString.ADMIN_INIT);
				webContent.setResponseHeaders(responseHeaders);
				
				responseBody = "".getBytes();
			}
			webContent.setResponseBody(responseBody);
		}
		return webContent;
	}

	private void handlePost(HttpExchange httpExchange, String path) {
		Headers headers = httpExchange.getRequestHeaders();
		byte[] req = HttpUtil.getRequestBody(httpExchange);
		String requestBody = "";
		if(req != null)
		{
			requestBody = new String(req);
		}
		try 
		{
			if(WebUserAccount.checkUserAuth(headers))
			{
				CookieServer cookie = new CookieServer(headers, Config.getSessionName(), Config.getSessionLifetime());
				if(path.equals("/admin.html"))
				{
					this.processAdmin(requestBody, cookie);
				}
				else if(path.equals("/account-update.html"))
				{
					this.processAccount(requestBody, cookie);
				}
				else if(path.equals("/general-setting.html"))
				{
					this.processGeneralSetting(requestBody);
				}
				else if(path.equals("/network-setting.html"))
				{
					this.processNetworkSetting(requestBody);
				}
				else if(path.equals("/logs.html"))
				{
					this.processDeleteLog(requestBody);
				}
				else if(path.equals("/sms-report.html"))
				{
					this.processDeleteReport(requestBody);
				}
			}
		} 
		catch (NoUserRegisteredException e) 
		{
			/**
			 * Do nothing
			 */
		}	
	}
	
	
	
	private void processDeleteLog(String requestBody) {
		Map<String, String> queryPairs = Utility.parseQueryPairs(requestBody);
		if(queryPairs.containsKey(JsonKey.DELETE))
		{
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id["))
				{
					String path = FileConfigUtil.removeParentWithDot("/"+value);
					String dir = Config.getLogDir();
					String fileName = FileConfigUtil.fixFileName(dir+path);
					File file = new File(fileName);
					try 
					{
						FileConfigUtil.deleteDirectoryWalkTree(file.toPath());
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void processDeleteReport(String requestBody) {
		Map<String, String> queryPairs = Utility.parseQueryPairs(requestBody);
		if(queryPairs.containsKey(JsonKey.DELETE))
		{
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id["))
				{
					String path = FileConfigUtil.removeParentWithDot("/"+value);
					String dir = Config.getRecordingPath();
					String fileName = FileConfigUtil.fixFileName(dir+path);
					File file = new File(fileName);
					try 
					{
						FileConfigUtil.deleteDirectoryWalkTree(file.toPath());
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	private void processGeneralSetting(String requestBody) {
		Map<String, String> queryPairs = Utility.parseQueryPairs(requestBody);
		if(queryPairs.containsKey("save_general_setting"))
		{
			ConfigGeneral.load(Config.getGeneralSettingPath());
			
			String deviceName2 = queryPairs.getOrDefault("device_name", "").trim();
			String deviceTimeZone = queryPairs.getOrDefault("device_time_zone", "").trim();
			String ntpServer = queryPairs.getOrDefault("ntp_server", "").trim();
			String ntpUpdateInterval = queryPairs.getOrDefault("ntp_update_interval", "").trim();
			
			ConfigGeneral.setDeviceName(deviceName2);
			ConfigGeneral.setDeviceTimeZone(deviceTimeZone);
			ConfigGeneral.setNtpServer(ntpServer);
			ConfigGeneral.setNtpUpdateInterval(ntpUpdateInterval);

			ConfigGeneral.save();
			DeviceAPI.setTimeZone(deviceTimeZone);
		}
	}	

	private void processNetworkSetting(String requestBody) {
		Map<String, String> queryPairs = Utility.parseQueryPairs(requestBody);
		if(queryPairs.containsKey("save_dhcp"))
		{
			String domainName = queryPairs.getOrDefault("domainName", "").trim();
			String domainNameServersStr = queryPairs.getOrDefault("domainNameServers", "").trim();
			String ipRouter = queryPairs.getOrDefault("ipRouter", "").trim();
			String netmask = queryPairs.getOrDefault("netmask", "").trim();
			String subnetMask = queryPairs.getOrDefault("subnetMask", "").trim();
			String domainNameServersAddress = queryPairs.getOrDefault("domainNameServersAddress", "").trim();
			String defaultLeaseTime = queryPairs.getOrDefault("defaultLeaseTime", "").trim();
			String maxLeaseTime = queryPairs.getOrDefault("maxLeaseTime", "").trim();
			String ranges = queryPairs.getOrDefault("ranges", "").trim();
			
			JSONArray nsList = new JSONArray();
			
			String[] arr1 = domainNameServersStr.split("\\,");
			for(int i = 0; i<arr1.length; i++)
			{
				String str1 = arr1[i].trim();
				if(!str1.isEmpty())
				{
					nsList.put(str1);
				}
			}
			JSONArray rangeList = new JSONArray();
			String[] arr2 = ranges.split("\\,");
			for(int i = 0; i<arr2.length; i++)
			{
				String str2 = arr2[i].trim();
				if(!str2.isEmpty())
				{
					String[] arr3 = str2.split("\\-");
					String str3 = arr3[0].trim();
					String str4 = arr3[1].trim();
					if(!str3.isEmpty() && !str4.isEmpty())
					{
						JSONObject obj1 = new JSONObject();
						obj1.put("begin", str3);
						obj1.put("end", str4);
						rangeList.put(obj1);
					}
				}
			}
			
			ConfigNetDHCP.load(Config.getDhcpSettingPath());
			ConfigNetDHCP.setDomainName(domainName);
			ConfigNetDHCP.setIpRouter(ipRouter);
			ConfigNetDHCP.setNetmask(netmask);
			ConfigNetDHCP.setSubnetMask(subnetMask);
			ConfigNetDHCP.setDomainNameServersAddress(domainNameServersAddress);
			ConfigNetDHCP.setDefaultLeaseTime(defaultLeaseTime);
			ConfigNetDHCP.setMaxLeaseTime(maxLeaseTime);
			ConfigNetDHCP.setRanges(rangeList);
			ConfigNetDHCP.setDomainNameServers(nsList);
			ConfigNetDHCP.save();	
			ConfigNetDHCP.apply(Config.getOsDHCPConfigPath());
		}
		
		if(queryPairs.containsKey("save_wlan"))
		{
			ConfigNetWLAN.load(Config.getWlanSettingPath());
			ConfigNetWLAN.setEssid(queryPairs.getOrDefault("essid", "").trim());
			ConfigNetWLAN.setKey(queryPairs.getOrDefault("key", "").trim());
			ConfigNetWLAN.setKeyMgmt(queryPairs.getOrDefault("keyMgmt", "").trim());
			ConfigNetWLAN.setIpAddress(queryPairs.getOrDefault("ipAddress", "").trim());
			ConfigNetWLAN.setPrefix(queryPairs.getOrDefault("prefix", "").trim());
			ConfigNetWLAN.setNetmask(queryPairs.getOrDefault("netmask", "").trim());
			ConfigNetWLAN.setGateway(queryPairs.getOrDefault("gateway", "").trim());
			ConfigNetWLAN.setDns1(queryPairs.getOrDefault("dns1", "").trim());
			ConfigNetWLAN.save();
			ConfigNetWLAN.apply(Config.getOsWLANConfigPath(), Config.getOsSSIDKey());
		}

		if(queryPairs.containsKey("save_ethernet"))
		{
			ConfigNetEthernet.load(Config.getEthernetSettingPath());
			ConfigNetEthernet.setIpAddress(queryPairs.getOrDefault("ipAddress", "").trim());
			ConfigNetEthernet.setPrefix(queryPairs.getOrDefault("prefix", "").trim());
			ConfigNetEthernet.setNetmask(queryPairs.getOrDefault("netmask", "").trim());
			ConfigNetEthernet.setGateway(queryPairs.getOrDefault("gateway", "").trim());
			ConfigNetEthernet.setDns1(queryPairs.getOrDefault("dns1", "").trim());
			ConfigNetEthernet.setDns2(queryPairs.getOrDefault("dns2", "").trim());
			ConfigNetEthernet.save();
			ConfigNetEthernet.apply(Config.getOsEthernetConfigPath());
		}
	}

	
	private void processAccount(String requestBody, CookieServer cookie) {
		Map<String, String> queryPairs = Utility.parseQueryPairs(requestBody);
		String loggedUsername = (String) cookie.getSessionValue(JsonKey.USERNAME, "");
		String phone = queryPairs.getOrDefault(JsonKey.PHONE, "");
		String password = queryPairs.getOrDefault(JsonKey.PASSWORD, "");
		String email = queryPairs.getOrDefault(JsonKey.EMAIL, "");
		String name = queryPairs.getOrDefault(JsonKey.NAME, "");
		if(queryPairs.containsKey(JsonKey.UPDATE))
		{
			User user;
			try 
			{
				user = WebUserAccount.getUser(loggedUsername);
				user.setName(name);
				user.setPhone(phone);
				user.setEmail(email);
				if(!password.isEmpty())
				{
					user.setPassword(password);
				}
				WebUserAccount.updateUser(user);
				WebUserAccount.save();
			} 
			catch (NoUserRegisteredException e) 
			{
				/**
				 * Do nothing
				 */
			}
		}		
	}
	
	private void processAdmin(String requestBody, CookieServer cookie) {
		Map<String, String> queryPairs = Utility.parseQueryPairs(requestBody);
		String loggedUsername = (String) cookie.getSessionValue(JsonKey.USERNAME, "");
		if(queryPairs.containsKey(JsonKey.DELETE))
		{
			/**
			 * Delete
			 */
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id[") && !value.equals(loggedUsername))
				{
					WebUserAccount.deleteUser(value);
				}
			}
			WebUserAccount.save();
		}
		else if(queryPairs.containsKey(JsonKey.DEACTIVATE))
		{
			/**
			 * Deactivate
			 */
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id[") && !value.equals(loggedUsername))
				{
					try 
					{
						WebUserAccount.deactivate(value);
					} 
					catch (NoUserRegisteredException e) 
					{
						/**
						 * Do nothing
						 */
					}
				}
			}
			WebUserAccount.save();
		}
		else if(queryPairs.containsKey(JsonKey.ACTIVATE))
		{
			/**
			 * Activate
			 */
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id["))
				{
					try 
					{
						WebUserAccount.activate(value);
					} 
					catch (NoUserRegisteredException e) 
					{
						/**
						 * Do nothing
						 */
					}
				}
			}
			WebUserAccount.save();
		}
		else if(queryPairs.containsKey("block"))
		{
			/**
			 * Block
			 */
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id[") && !value.equals(loggedUsername))
				{
					try 
					{
						WebUserAccount.block(value);
					} 
					catch (NoUserRegisteredException e) 
					{
						/**
						 * Do nothing
						 */
					}
				}
			}
			WebUserAccount.save();		
		}
		else if(queryPairs.containsKey("unblock"))
		{
			/**
			 * Unblock
			 */
			for (Map.Entry<String, String> entry : queryPairs.entrySet()) 
			{
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith("id["))
				{
					try 
					{
						WebUserAccount.unblock(value);
					} 
					catch (NoUserRegisteredException e) 
					{
						/**
						 * Do nothing
						 */
					}
				}
			}
			WebUserAccount.save();
		}
		else if(queryPairs.containsKey("update-data"))
		{
			String pkID = queryPairs.getOrDefault("pk_id", "");
			String field = queryPairs.getOrDefault("field", "");
			String value = queryPairs.getOrDefault("value", "");
			if(!field.equals(JsonKey.USERNAME))
			{
				User user;
				try 
				{
					user = WebUserAccount.getUser(pkID);
					if(field.equals(JsonKey.PHONE))
					{
						user.setPhone(value);
					}
					if(field.equals(JsonKey.NAME))
					{
						user.setName(value);
					}
					WebUserAccount.updateUser(user);
					WebUserAccount.save();
				} 
				catch (NoUserRegisteredException e) 
				{
					/**
					 * Do nothing
					 */
				}
			}
		}
		else if(queryPairs.containsKey(JsonKey.UPDATE))
		{
			String username = queryPairs.getOrDefault(JsonKey.USERNAME, "").trim();
			String name = queryPairs.getOrDefault(JsonKey.NAME, "").trim();
			String phone = queryPairs.getOrDefault(JsonKey.PHONE, "").trim();
			String email = queryPairs.getOrDefault(JsonKey.EMAIL, "").trim();
			String password = queryPairs.getOrDefault(JsonKey.PASSWORD, "").trim();
			boolean blocked = queryPairs.getOrDefault(JsonKey.BLOCKED, "").equals("1");
			boolean active = queryPairs.getOrDefault(JsonKey.ACTIVE, "").equals("1");

			if(!username.isEmpty())
			{
				User user;
				try 
				{
					user = WebUserAccount.getUser(username);
					if(!username.equals(loggedUsername) && !user.getUsername().isEmpty())
					{
						user.setUsername(username);
					}
					if(!name.isEmpty())
					{
						user.setName(name);
					}
					user.setPhone(phone);
					user.setEmail(email);
					if(!password.isEmpty())
					{
						user.setPassword(password);
					}
					if(!username.equals(loggedUsername))
					{
						user.setBlocked(blocked);
					}
					if(!username.equals(loggedUsername))
					{
						user.setActive(active);
					}
					WebUserAccount.updateUser(user);
					WebUserAccount.save();
				} 
				catch (NoUserRegisteredException e) 
				{
					/**
					 * Do nothing
					 */
				}
			}
		}
		else if(queryPairs.containsKey(JsonKey.ADD))
		{
			String username = queryPairs.getOrDefault(JsonKey.USERNAME, "");
		    String password = queryPairs.getOrDefault(JsonKey.PASSWORD, "");
		    String email = queryPairs.getOrDefault(JsonKey.EMAIL, "");
		    String name = queryPairs.getOrDefault(JsonKey.NAME, "");
		    String phone = queryPairs.getOrDefault(JsonKey.PHONE, "");
	
		    JSONObject jsonObject = new JSONObject();
			jsonObject.put(JsonKey.USERNAME, username);
			jsonObject.put(JsonKey.NAME, name);
			jsonObject.put(JsonKey.EMAIL, email);
			jsonObject.put(JsonKey.PASSWORD, password);
			jsonObject.put(JsonKey.PHONE, phone);
			jsonObject.put(JsonKey.BLOCKED, false);
			jsonObject.put(JsonKey.ACTIVE, true);
			
			if(!username.isEmpty())
			{
				WebUserAccount.addUser(new User(jsonObject));		
				WebUserAccount.save();
			}		
		}
	}
	
}
