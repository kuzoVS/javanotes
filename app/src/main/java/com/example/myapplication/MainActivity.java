package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText txtUser;
    private EditText txtPassword;
    private CheckBox chkRemember;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "login_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = findViewById(R.id.txt_auth_user);
        txtPassword = findViewById(R.id.txt_auth_password);
        chkRemember = findViewById(R.id.chkRemember);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadSavedCredentials();
    }

    public void onRegisterClick(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void onSignInClick(View v) {
        Activity ctx = this;

        String username = txtUser.getText().toString();
        String password = txtPassword.getText().toString();

        if (chkRemember.isChecked()) {
            saveCredentials(username, password);
        } else {
            clearSavedCredentials();
        }

        String args = "?name=" + username + "&secret=" + password;

        ApiCall apiCall = new ApiCall(this, "PUT", "session" + args) {
            public void on_ready(String result) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String status = responseJson.optString("status");

                    if (status.equals("ok")) {
                        // Authentication successful
                        String token = responseJson.optString("token");
                        Intent intent = new Intent(ctx, ListActivity.class);
                        intent.putExtra("token", token);
                        System.out.println("Token: " + token);
                        startActivity(intent);
                    } else {
                        // Authentication error
                        System.out.println("Invalid credentials");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void on_fail() {
                // Handle authentication failure
                System.out.println("Authentication failed");
            }
        };
    }

    private void loadSavedCredentials() {
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        String password = sharedPreferences.getString(KEY_PASSWORD, "");
        boolean remember = sharedPreferences.getBoolean(KEY_REMEMBER, false);

        txtUser.setText(username);
        txtPassword.setText(password);
        chkRemember.setChecked(remember);
    }

    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER, true);
        editor.apply();
    }

    private void clearSavedCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_REMEMBER);
        editor.apply();
    }
}