package com.example.smartdispenser;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIClient {
    private static final String API_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";
    private static final String API_PASSWORD = "DTmauqMRyrUreRZSlDci:ZGCRAmvbycsqXRVEoXzE";
    private static final String userId = "1";

    private OkHttpClient client;

    public APIClient() {
        client = new OkHttpClient();
    }

    public String getResponse(String query) {
        // 构建请求体
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"model\":\"lite\",");
        jsonBuilder.append("\"messages\": [");
        jsonBuilder.append("{");
        jsonBuilder.append("\"role\": \"user\",");
        jsonBuilder.append("\"content\": \"");
        jsonBuilder.append(query);
        jsonBuilder.append("\"");
        jsonBuilder.append("}");
        jsonBuilder.append("]");
        jsonBuilder.append("}");

        String json = jsonBuilder.toString();

        // 创建请求体
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_PASSWORD)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 处理响应
                String responseBody = response.body().string();
                return extractContent(responseBody);
            } else {
                return "Request failed: " + response.code();
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    // 响应体转Json格式
    private String extractContent(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices.size() > 0) {
                JsonObject choice = choices.get(0).getAsJsonObject();
                JsonObject message = choice.getAsJsonObject("message");
                return message.get("content").getAsString();
            }
            return "Error: No content found";
        } catch (Exception e) {
            return "Error: Parsing JSON " + e.getMessage();
        }
    }
}
