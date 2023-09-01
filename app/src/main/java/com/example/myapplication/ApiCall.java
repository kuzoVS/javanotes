package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCall {

    private Activity context;

    public ApiCall(Activity context, String method, String endpoint) {
        this.context = context;
        Thread t = new Thread(() -> {
            try {
                URL url = new URL("http://kuzo66.pythonanywhere.com/" + endpoint);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod(method);

                InputStream input = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                con.disconnect();

                final String result = response.toString();
                context.runOnUiThread(() -> {
                    on_ready(result);
                });
            } catch (IOException ex) {
                Log.e("ApiCall", ex.toString());
                context.runOnUiThread(() -> {
                    on_fail();
                });
            }
        });
        t.start();
    }

    public void on_ready(String result) {
        try {
            JSONObject responseJson = new JSONObject(result);
            String status = responseJson.getString("status");
            System.out.println("Response: " + responseJson);
            System.out.println("Status: " + status);
            if (status.equals("ok")) {
                String token = responseJson.getString("token");
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("token", token);
                context.startActivity(intent);
            } else if (status.equals("error")) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        on_fail();
                    }
                });
            } else {
                // Неизвестный статус ответа сервера
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        on_fail();
                    }
                });
            }
        } catch (JSONException e) {
            Log.e("ApiCall", "JSON парсинг не удался: " + e.getMessage());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    on_fail();
                }
            });
        }
    }


    public void on_fail() {
        // Обработка ошибки
    }
}
