package com.jhgzs.http;
public interface WebServerPluginInfo {
    String[] getMimeTypes();

    String[] getIndexFilesForMimeType(String mime);

    WebServerPlugin getWebServerPlugin(String mimeType);
}