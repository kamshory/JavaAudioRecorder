package com.planetbiru.web;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.planetbiru.WebSocketConnection;
import com.planetbiru.constant.JsonKey;
import com.planetbiru.cookie.CookieServer;
import com.planetbiru.user.NoUserRegisteredException;
import com.planetbiru.user.WebUserAccount;

public class ServerWebSocketServerAdmin extends WebSocketServer{

	private static Collection<WebSocketConnection> clients = new ArrayList<>();
	public ServerWebSocketServerAdmin(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onClose(WebSocket conn, int code, String message, boolean arg3) {
		this.remove(conn);		
	}

	@Override
	public void onError(WebSocket conn, Exception e) {
		this.remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		/**
		 * Do nothing
		 */
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake request) {
		
		String rawCookie = request.getFieldValue("Cookie");
		CookieServer cookie = new CookieServer(rawCookie);
		String username = cookie.getSessionData().optString(JsonKey.USERNAME, "");
		String password = cookie.getSessionData().optString(JsonKey.PASSWORD, "");
		try 
		{
			if(WebUserAccount.checkUserAuth(username, password))
			{
				ServerWebSocketServerAdmin.clients.add(new WebSocketConnection(conn, request));
			}
			else
			{
				conn.close();
			}
		} 
		catch (NoUserRegisteredException e) 
		{
			conn.close();
		}
	}
	
	@Override
	public void onStart() {
		/**
		 * Do nothing
		 */
	}
	
	private void remove(WebSocket conn) {
		for(WebSocketConnection client : ServerWebSocketServerAdmin.clients)
		{
			if(client.getConn().equals(conn))
			{
				ServerWebSocketServerAdmin.clients.remove(client);
				break;
			}
		}		
	}
	
	public static void broadcastMessage(String message)
	{
		for(WebSocketConnection client : ServerWebSocketServerAdmin.clients)
		{
			client.send(message);
		}
	}
	
	public static void broadcastMessage(String message, String path)
	{
		for(WebSocketConnection client : ServerWebSocketServerAdmin.clients)
		{
			if(client.getPath().contains(path))
			{
				client.send(message);
			}
		}
	}
	public static void broadcastMessage(String message, WebSocket sender)
	{
		for(WebSocketConnection client : ServerWebSocketServerAdmin.clients)
		{
			if(!client.getConn().equals(sender))
			{
				client.send(message);
			}
		}
	}
}
