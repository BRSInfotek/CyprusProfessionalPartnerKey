package org.cyprusbrs.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.model.MClient;

public class GetOAuthMicrosoftToken {

    private static final String MICROSOFT_AUTH_URL = "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize";
    private static final String MICROSOFT_TOKEN_URL = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";
    private static final String MICROSOFT_SCOPE = "https://graph.microsoft.com/Mail.Send offline_access openid profile";
    private static final String STATE = "microsoft-oauth-" + System.currentTimeMillis();

    public static String getAccessToken(MClient client) throws Exception {
        String refreshToken = client.get_ValueAsString("OAuthRefreshTokenMicrosoft");
        if (!Util.isEmpty(refreshToken)) {
            try {
                return refreshAccessToken(client, refreshToken);
            } catch (Exception e) {
                System.err.println("Refresh failed, falling back to full auth: " + e.getMessage());
            }
        }
 
        return performFullOAuthFlow(client);
    }

    private static String performFullOAuthFlow(MClient client) throws Exception {
        int callbackPort = OAuthCallbackHandler.start(client, "M");
        String redirectUri = "http://localhost:" + callbackPort + "/callback";

        try {
            String tenantId = client.get_ValueAsString("OAuthTenantID_Microsoft");
            String clientId = client.get_ValueAsString("OAuthClientID_Microsoft");
            String authUrl = buildMicrosoftAuthUrl(clientId, tenantId, redirectUri);

            Desktop.getDesktop().browse(URI.create(authUrl));
            waitForCallback(client);

            String code = client.get_ValueAsString("OAuthCodeMicrosoft");
            if (Util.isEmpty(code)) throw new CyprusException("Authorization code not received.");

            Map<String, String> tokens = exchangeCodeForTokens(client, code, redirectUri);
            validateTokens(tokens);
            saveTokens(client, tokens);
            return tokens.get("access_token");
        } finally {
            OAuthCallbackHandler.stop();
            client.set_ValueOfColumn("OAuthCodeMicrosoft", null);
            client.saveEx();
        }
    }

    private static String buildMicrosoftAuthUrl(String clientId, String tenantId, String redirectUri) throws Exception {
        return String.format(MICROSOFT_AUTH_URL, tenantId) + "?" +
                "client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&response_mode=query" +
                "&scope=" + URLEncoder.encode(MICROSOFT_SCOPE, "UTF-8") +
                "&prompt=consent" +
                "&state=" + URLEncoder.encode(STATE, "UTF-8");
    }

    private static void waitForCallback(MClient client) throws Exception {
        int timeout = 120;
        long start = System.currentTimeMillis();
        while (true) {
            String code = client.get_ValueAsString("OAuthCodeMicrosoft");
            if (!Util.isEmpty(code)) return;
            if (System.currentTimeMillis() - start > timeout * 1000) {
                throw new CyprusException("Timeout waiting for Microsoft OAuth authorization.");
            }
            Thread.sleep(1000);
        }
    }

    private static Map<String, String> exchangeCodeForTokens(MClient client, String code, String redirectUri) throws Exception {
        String clientId = client.get_ValueAsString("OAuthClientID_Microsoft");
        String clientSecret = client.get_ValueAsString("OAuthClientSecretID_Microsoft");
        String tenantId = client.get_ValueAsString("OAuthTenantID_Microsoft");

        String params = String.format(
                "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s&scope=%s",
                URLEncoder.encode(clientId, "UTF-8"),
                URLEncoder.encode(clientSecret, "UTF-8"),
                URLEncoder.encode(code, "UTF-8"),
                URLEncoder.encode(redirectUri, "UTF-8"),
                URLEncoder.encode(MICROSOFT_SCOPE, "UTF-8")
        );

        return sendPostRequest(String.format(MICROSOFT_TOKEN_URL, tenantId), params);
    }

    private static String refreshAccessToken(MClient client, String refreshToken) throws Exception {
        String clientId = client.get_ValueAsString("OAuthClientID_Microsoft");
        String clientSecret = client.get_ValueAsString("OAuthClientSecretID_Microsoft");
        String tenantId = client.get_ValueAsString("OAuthTenantID_Microsoft");

        String params = String.format(
        	    "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
        	    URLEncoder.encode(clientId, "UTF-8"),
        	    URLEncoder.encode(clientSecret, "UTF-8"),
        	    URLEncoder.encode(refreshToken, "UTF-8")
        	);


        System.out.println("Sending refresh request to: " + MICROSOFT_TOKEN_URL.replace("%s", tenantId));
        System.out.println("Params: " + params);

        Map<String, String> tokens = sendPostRequest(String.format(MICROSOFT_TOKEN_URL, tenantId), params);
        System.out.println("Response: " + tokens);

        validateTokens(tokens);
        saveTokens(client, tokens);
        return tokens.get("access_token");
    }


    private static Map<String, String> sendPostRequest(String url, String params) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        return parseJsonResponse(readResponse(conn));
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        InputStream is = conn.getResponseCode() < 400 ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        return response.toString();
    }

    private static Map<String, String> parseJsonResponse(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replaceAll("[{}]", "");
        for (String pair : json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim().replaceAll("^\"|\"$", ""), kv[1].trim().replaceAll("^\"|\"$", ""));
            }
        }
        return map;
    }

    private static void validateTokens(Map<String, String> tokens) throws CyprusException {
        if (!tokens.containsKey("access_token")) throw new CyprusException("Access token not received");
    }

    private static void saveTokens(MClient client, Map<String, String> tokens) {
        if (tokens.containsKey("access_token")) {
            client.set_ValueOfColumn("OAuthTokenMicrosoft", tokens.get("access_token"));
        }
        if (tokens.containsKey("refresh_token")) {
            client.set_ValueOfColumn("OAuthRefreshTokenMicrosoft", tokens.get("refresh_token"));
        }       
        client.saveEx();
    }
}
