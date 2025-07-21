package org.cyprusbrs.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.model.MClient;

public class GetOAuthGoogleToken {

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_SCOPE = "https://mail.google.com/";
    private static final String state = "google-oauth-" + System.currentTimeMillis();

    public static String getAccessToken(MClient client) throws Exception {
        String refreshToken = client.get_ValueAsString("OAuthRefreshToken");
        if (!Util.isEmpty(refreshToken)) {
            try {
                return refreshAccessToken(client, refreshToken);
            } catch (Exception e) {
                System.err.println("Refresh token failed, falling back to full auth: " + e.getMessage());
            }
        }

        return performFullOAuthFlow(client);
    }

    private static String performFullOAuthFlow(MClient client) throws Exception {
        int callbackPort = OAuthCallbackHandler.start(client, "G");
        String redirectUri = "http://localhost:" + callbackPort + "/callback";

        try {
            String clientId = client.get_ValueAsString("OAuthClientID");
            String authUrl = buildGoogleAuthUrl(clientId, redirectUri);
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(authUrl));

            waitForCallback(client);
            String code = client.get_ValueAsString("OAuthCode");
            if (Util.isEmpty(code)) throw new CyprusException("Authorization code was not received.");

            Map<String, String> tokens = exchangeCodeForTokens(client, code, redirectUri);
            validateTokens(tokens);
            saveTokens(client, tokens);
            return tokens.get("access_token");
        } finally {
            OAuthCallbackHandler.stop();
            client.set_ValueOfColumn("OAuthCode", null);
            client.saveEx();
        }
    }

    private static String buildGoogleAuthUrl(String clientId, String redirectUri) throws Exception {
        return GOOGLE_AUTH_URL + "?" +
                "client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode(GOOGLE_SCOPE, "UTF-8") +
                "&access_type=offline" +
                "&prompt=consent" +
                "&state=" + URLEncoder.encode(state, "UTF-8");
    }

    private static void waitForCallback(MClient client) throws Exception {
        int timeout = 120;
        int interval = 1000;
        long start = System.currentTimeMillis();

        while (true) {
            String code = client.get_ValueAsString("OAuthCode");
            if (!Util.isEmpty(code)) return;
            if ((System.currentTimeMillis() - start) > timeout * 1000) {
                throw new CyprusException("Timeout waiting for Google OAuth authorization.");
            }
            Thread.sleep(interval);
        }
    }

    private static Map<String, String> exchangeCodeForTokens(MClient client, String code, String redirectUri) throws Exception {
        String clientId = client.get_ValueAsString("OAuthClientID");
        String clientSecret = client.get_ValueAsString("OAuthClientSecretID");

        String params = String.format(
                "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                URLEncoder.encode(clientId, "UTF-8"),
                URLEncoder.encode(clientSecret, "UTF-8"),
                URLEncoder.encode(code, "UTF-8"),
                URLEncoder.encode(redirectUri, "UTF-8")
        );

        HttpURLConnection conn = (HttpURLConnection) new URL(GOOGLE_TOKEN_URL).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        return parseJsonResponse(readResponse(conn));
    }

    private static String refreshAccessToken(MClient client, String refreshToken) throws Exception {
        String clientId = client.get_ValueAsString("OAuthClientID");
        String clientSecret = client.get_ValueAsString("OAuthClientSecretID");

        String params = String.format(
                "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
                URLEncoder.encode(clientId, "UTF-8"),
                URLEncoder.encode(clientSecret, "UTF-8"),
                URLEncoder.encode(refreshToken, "UTF-8")
        );

        HttpURLConnection conn = (HttpURLConnection) new URL(GOOGLE_TOKEN_URL).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        Map<String, String> tokens = parseJsonResponse(readResponse(conn));
        validateTokens(tokens);
        saveTokens(client, tokens);
        return tokens.get("access_token");
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        InputStream is = (conn.getResponseCode() == 200) ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        return response.toString();
    }

    private static Map<String, String> parseJsonResponse(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replaceAll("[{}]", "");
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
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
            client.set_ValueOfColumn("OAuthToken", tokens.get("access_token"));
        }
        if (tokens.containsKey("refresh_token")) {
            client.set_ValueOfColumn("OAuthRefreshToken", tokens.get("refresh_token"));
        }
        client.saveEx();
    }
}
