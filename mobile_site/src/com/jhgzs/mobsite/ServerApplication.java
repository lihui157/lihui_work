package com.jhgzs.mobsite;

import java.io.IOException;

import org.apache.tools.ant.taskdefs.Get;

import com.jhgzs.mobsite.http.CustomWebServer;

import android.app.Application;

public class ServerApplication extends Application {

	private static CustomWebServer server;
	
	public static final String http_root = "/webroot";

	public static CustomWebServer getServer() {
		return server;
	}

	public static void setServer(CustomWebServer webServer) {
		server = webServer;
	}
	
	
}
