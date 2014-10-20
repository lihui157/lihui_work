package com.jhgzs.mobsite;

import java.io.IOException;

import org.apache.tools.ant.taskdefs.Get;

import com.jhgzs.mobsite.http.WFM_CustomWebServer;

import android.app.Application;

public class ServerApplication extends Application {

	private static WFM_CustomWebServer server;
	
	public static final String http_root = "/webroot";

	public static WFM_CustomWebServer getServer() {
		return server;
	}

	public static void setServer(WFM_CustomWebServer webServer) {
		server = webServer;
	}
	
	
}
