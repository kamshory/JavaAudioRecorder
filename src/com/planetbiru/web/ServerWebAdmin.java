package com.planetbiru.web;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.planetbiru.ServiceHTTP;
import com.sun.net.httpserver.HttpServer;


public class ServerWebAdmin {
	
	private static Logger logger = Logger.getLogger(ServerWebAdmin.class);
	
	public void start(int port) 
	{
		try 
		{
			ServiceHTTP.setHttpServer(HttpServer.create(new InetSocketAddress(port), 0));
	        ServiceHTTP.getHttpServer().createContext("/", new HandlerWebManager());
	        ServiceHTTP.getHttpServer().createContext("/login.html", new HandlerWebManagerLogin());
	        ServiceHTTP.getHttpServer().createContext("/logout.html", new HandlerWebManagerLogout());
	        ServiceHTTP.getHttpServer().createContext("/user/add", new HandlerWebManagerUserAdd());
	        ServiceHTTP.getHttpServer().createContext("/user/init", new HandlerWebManagerUserInit());
	        ServiceHTTP.getHttpServer().createContext("/audio/", new HandlerWebManagerAudio());
	        ServiceHTTP.getHttpServer().createContext("/data/", new HandlerWebManagerData());
	        ServiceHTTP.getHttpServer().createContext("/ping/", new HandlerWebManagerPing());
	        ServiceHTTP.getHttpServer().start();
		} 
		catch (IOException e) 
		{
			logger.error(e.getMessage(), e);
		}
	}

	public void stop() {
		if(ServiceHTTP.getHttpServer() != null)
		{
			ServiceHTTP.getHttpServer().stop(0);
		}
		
	}
	

}
