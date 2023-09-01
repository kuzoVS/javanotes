package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    Activity ctx = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onCancelClick(View v) {
        finish();
    }

    public void onOkClick(View v) {
        EditText txtUser = findViewById(R.id.txt_reg_user);
        EditText txtPassword = findViewById(R.id.txt_reg_pass);

        String name = txtUser.getText().toString();
        String secret = txtPassword.getText().toString();

        String args = "?name=" + name + "&secret=" + secret;

        ApiCall apiCall = new ApiCall(this, "PUT", "account" + args) {
            public void on_ready(String result) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String status = responseJson.optString("status");

                    if (status.equals("ok")) {
                        Toast.makeText(ctx, "Your account created", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ctx, "This USERNAME \nALREADY TAKEN", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }


            public void on_fail() {
                // Обработка ошибки регистрации
            }
        };
    }
}
