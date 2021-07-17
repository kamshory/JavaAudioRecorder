package com.planetbiru.web;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import com.planetbiru.config.Config;
import com.planetbiru.config.ConfigGeneral;
import com.planetbiru.config.ConfigNetDHCP;
import com.planetbiru.config.ConfigNetEthernet;
import com.planetbiru.config.ConfigNetWLAN;
import com.planetbiru.constant.ConstantString;
import com.planetbiru.constant.JsonKey;
import com.planetbiru.cookie.CookieServer;
import com.planetbiru.user.NoUserRegisteredException;
import com.planetbiru.user.WebUserAccount;
import com.planetbiru.util.FileConfigUtil;
import com.planetbiru.util.FileNotFoundException;
import com.planetbiru.util.FileUtil;
import com.planetbiru.util.ServerInfo;
import com.planetbiru.util.ServerStatus;
import com.planetbiru.util.Utility;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerWebManagerData implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().getPath();
		if(path.startsWith("/data/general-setting/get"))
		{
			this.handleGeneralSetting(httpExchange);
		}
		else if(path.startsWith("/data/log/list"))
		{
			this.handleLogFile(httpExchange);
		}
		else if(path.startsWith("/data/recording/list"))
		{
			this.handleRecordingList(httpExchange);
		}
		else if(path.startsWith("/data/recording/download/"))
		{
			this.handleDownloadRecordingFile(httpExchange);
		}
		else if(path.startsWith("/data/log/download/"))
		{
			this.handleDownloadLogFile(httpExchange);
		}
		else if(path.startsWith("/data/network-dhcp-setting/get"))
		{
			this.handleDHCPSetting(httpExchange);
		}
		else if(path.startsWith("/data/network-wlan-setting/get"))
		{
			this.handleWLANSetting(httpExchange);
		}
		else if(path.startsWith("/data/network-ethernet-setting/get"))
		{
			this.handleEthernetSetting(httpExchange);
		}
		else if(path.startsWith("/data/server-info/get"))
		{
			this.handleServerInfo(httpExchange);
		}
		else if(path.startsWith("/data/server-status/get"))
		{
			this.handleServerStatus(httpExchange);
		}
		else if(path.startsWith("/data/user/self"))
		{
			this.handleSelfAccount(httpExchange);
		}
		else if(path.startsWith("/data/user/list"))
		{
			this.handleUserList(httpExchange);
		}
		else if(path.startsWith("/data/user/detail/"))
		{
			this.handleUserGet(httpExchange);
		}
		else if(path.startsWith("/data/port/open"))
		{
			this.handleOpenPort(httpExchange);
		}
		else
		{
			httpExchange.sendResponseHeaders(404, 0);
			httpExchange.close();
		}
	}
	
	public void handleOpenPort(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				responseBody = ServerInfo.getOpenPort().toString(4).getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}		
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();		
	}
	
	//@GetMapping(path="/data/user/self")
	public void handleSelfAccount(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String loggedUsername = (String) cookie.getSessionValue(JsonKey.USERNAME, "");
				String list = WebUserAccount.getUser(loggedUsername).toString();
				responseBody = list.getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}		
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();		
	}
	
		
		
	//@GetMapping(path="/user/list")
	public void handleUserList(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String list = WebUserAccount.listAsString();
				responseBody = list.getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();		
	}
	
	//@GetMapping(path="/data/user/detail/{username}")
	public void handleUserGet(HttpExchange httpExchange) throws IOException
	{
		String path = httpExchange.getRequestURI().getPath();
		String id = path.substring("/data/user/detail/".length());
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String data = WebUserAccount.getUser(id).toString();
				responseBody = data.getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();		
	}
	
	
	
	//@GetMapping(path="/data/general-setting/get")
	public void handleGeneralSetting(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String list = ConfigGeneral.toJSONObject().toString();
				responseBody = list.getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);
		
		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
		
	

	//@GetMapping(path="/data/log/list")
	public void handleLogFile(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				File directory = new File(Config.getLogDir());
				JSONArray list = FileUtil.listFile(directory);
				responseBody = list.toString().getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	//@GetMapping(path="/data/recording/list")
	public void handleRecordingList(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				File directory = new File(Config.getStorageDir());
				JSONArray list = FileUtil.listFile(directory);
				responseBody = list.toString().getBytes();
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();
	}
	
	//@GetMapping(path="/data/recording/download/**")
	public void handleDownloadRecordingFile(HttpExchange httpExchange) throws IOException
	{
		String path = httpExchange.getRequestURI().getPath();
		path = path.substring("/data/recording/download".length());
		
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String fullname = Config.getStorageDir() + "/" + path;
				fullname = FileConfigUtil.fixFileName(fullname);	
				byte[] list = "".getBytes();
				try 
				{
					list = FileUtil.readResource(fullname);
					responseBody = list;
					String contentType = HttpUtil.getMIMEType(path);
					String baseName = HttpUtil.getBaseName(path);
					responseHeaders.add(ConstantString.CONTENT_TYPE, contentType);
					responseHeaders.add("Content-disposition", "attachment; filename=\""+baseName+"\"");
				} 
				catch (FileNotFoundException e) 
				{
					statusCode = HttpStatus.NOT_FOUND;
				}
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	//@GetMapping(path="/data/log/download/**")
	public void handleDownloadLogFile(HttpExchange httpExchange) throws IOException
	{
		String path = httpExchange.getRequestURI().getPath();
		path = path.substring("/data/log/download".length());
		
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String fullname = Config.getLogDir() + "/" + path;
				fullname = FileConfigUtil.fixFileName(fullname);	
				byte[] list = "".getBytes();
				try 
				{
					list = FileUtil.readResource(fullname);
					responseBody = list;
					String contentType = HttpUtil.getMIMEType(path);
					String baseName = HttpUtil.getBaseName(path);
					responseHeaders.add(ConstantString.CONTENT_TYPE, contentType);
					responseHeaders.add("Content-disposition", "attachment; filename=\""+baseName+"\"");
				} 
				catch (FileNotFoundException e) 
				{
					statusCode = HttpStatus.NOT_FOUND;
				}
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	
	
	
	
	//@GetMapping(path="/data/network-dhcp-setting/get")
	public void handleDHCPSetting(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				ConfigNetDHCP.load(Config.getDhcpSettingPath());		
				responseBody = ConfigNetDHCP.toJSONObject().toString().getBytes();
				
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}	
	
	//@GetMapping(path="/data/network-wlan-setting/get")
	public void handleWLANSetting(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				ConfigNetWLAN.load(Config.getWlanSettingPath());		
				responseBody = ConfigNetWLAN.toJSONObject().toString().getBytes();
				
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	//@GetMapping(path="/data/network-ethernet-setting/get")
	public void handleEthernetSetting(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				ConfigNetEthernet.load(Config.getEthernetSettingPath());
				responseBody = ConfigNetEthernet.toJSONObject().toString().getBytes();				
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	//@GetMapping(path="/data/server-info/get")
	public void handleServerInfo(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				responseBody = ServerInfo.getInfo().getBytes();	
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	//@GetMapping(path="/data/server-status/get")
	public void handleServerStatus(HttpExchange httpExchange) throws IOException
	{
		Headers requestHeaders = httpExchange.getRequestHeaders();
		Headers responseHeaders = httpExchange.getResponseHeaders();
		String query = httpExchange.getRequestURI().getQuery();
		Map<String, String> request = Utility.parseQueryPairs(query);
		CookieServer cookie = new CookieServer(requestHeaders, Config.getSessionName(), Config.getSessionLifetime());
		byte[] responseBody = "".getBytes();
		int statusCode = HttpStatus.OK;
		try
		{
			if(WebUserAccount.checkUserAuth(requestHeaders))
			{
				String timeStr = request.getOrDefault("time", "");
				long from = 0;
				long to = System.currentTimeMillis();
				if(timeStr.equals("1h"))
				{
					from = System.currentTimeMillis() - 3600000;
				}
				else if(timeStr.equals("2h"))
				{
					from = System.currentTimeMillis() - (3600000 * 2);
				}
				else if(timeStr.equals("3h"))
				{
					from = System.currentTimeMillis() - (3600000 * 3);
				}
				else if(timeStr.equals("6h"))
				{
					from = System.currentTimeMillis() - (3600000 * 6);
				}
				else if(timeStr.equals("12h"))
				{
					from = System.currentTimeMillis() - (3600000 * 12);
				}
				else if(timeStr.equals("24h"))
				{
					from = System.currentTimeMillis() - (3600000 * 24);
				}
				else if(timeStr.contains(","))
				{
					String[] tm = timeStr.split(",");
					from = Utility.atol(tm[0]);
					to = Utility.atol(tm[1]);
				}
				responseBody = ServerStatus.load(from, to).toString(4).getBytes();	
			}
			else
			{
				statusCode = HttpStatus.UNAUTHORIZED;			
			}
		}
		catch(NoUserRegisteredException e)
		{
			/**
			 * Do nothing
			 */
			statusCode = HttpStatus.UNAUTHORIZED;
		}
		cookie.saveSessionData();
		cookie.putToHeaders(responseHeaders);
		responseHeaders.add(ConstantString.CONTENT_TYPE, ConstantString.APPLICATION_JSON);
		responseHeaders.add(ConstantString.CACHE_CONTROL, ConstantString.NO_CACHE);

		httpExchange.sendResponseHeaders(statusCode, responseBody.length);	 
		httpExchange.getResponseBody().write(responseBody);
		httpExchange.close();	
	}
	
	

}
