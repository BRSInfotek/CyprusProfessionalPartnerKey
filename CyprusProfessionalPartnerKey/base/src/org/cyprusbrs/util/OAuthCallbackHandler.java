package org.cyprusbrs.util;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.cyprusbrs.model.MClient;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class OAuthCallbackHandler {
    
    private static HttpServer server;
    private static int currentPort;
    
    public static int start(MClient client, String provider) throws IOException {

    	int port = 8895;
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/callback", exchange -> {
                    handleCallback(exchange, client, provider);
                });
                server.start();
                currentPort = port;
                return port;
            } catch (BindException e) {

            }
        
        throw new IOException("No available ports found");
    }
    
    private static Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                params.put(pair[0], pair[1]);
            }
        }
        return params;
    }
    
    private static void handleCallback(HttpExchange exchange, MClient client, String provider) throws IOException {
        try {
        	String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = getQueryParams(query);
            String code = params.get("code");
            
            if ("G".equalsIgnoreCase((String) client.get_Value("OAuthProvider"))) {
            	client.set_ValueOfColumn("OAuthCode", code);
            }
            else if ("M".equalsIgnoreCase((String) client.get_Value("OAuthProvider"))) {
            	client.set_ValueOfColumn("OAuthCodeMicrosoft", code);
            }
            client.saveEx();
            
            exchange.sendResponseHeaders(200, "Success".length());
            exchange.getResponseBody().write("Success".getBytes(StandardCharsets.UTF_8));
        } finally {
            exchange.close();
            stop();
        }
    }
    
    public static void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }
    
    public static int getCurrentPort() {
        return currentPort;
    }
}