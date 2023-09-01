package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class NoteActivity extends AppCompatActivity {
    int id;
    String token;
    EditText txt_title;
    EditText txt_content;
    Activity ctx = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1note);

        txt_title = findViewById(R.id.txt_title);
        txt_content = findViewById(R.id.txt_content);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        id = intent.getIntExtra("note_id", -1);
        txt_title.setText(intent.getStringExtra("note_title"));

        String args = "?token=" + token + "&id=" + String.valueOf(id);
        new ApiCall(this, "GET", "note" + args) {
            public void on_ready(String result) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String status = responseJson.optString("status");
                    String content = responseJson.optString("content");
                    if (status.equals("ok")) {
                        txt_content.setText(content);
                    } else {
                        Toast.makeText(ctx, "Note not found!", Toast.LENGTH_SHORT).show();
                        System.out.println("Error note not found");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void on_fail() {
            }
        };
    }

    public void onClose_click(View v) {
        finish();
    }

    public void onDelete_click(View v) {
        String args = "?token=" + token + "&id=" + String.valueOf(id);
        new ApiCall(this, "DELETE", "note" + args) {
            public void on_ready(String result) {
                Toast.makeText(ctx, "Note deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }

            public void on_fail() {

            }

        };
    }

    public void onSave_click(View v) {
        String title = URLEncoder.encode(txt_title.getText().toString());
        String content = URLEncoder.encode(txt_content.getText().toString());

        String args = "?token=" + token + "&id=" + String.valueOf(id)
                + "&title=" + title + "&content=" + content;
        new ApiCall(this, "POST", "note" + args) {
            public void on_ready(String result) {
                setResult(Activity.RESULT_OK);
                finish();
                Toast.makeText(ctx, "Note saved!", Toast.LENGTH_SHORT).show();
            }

            public void on_fail() {
            }
        };
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(Activity.RESULT_OK, data);  // Set the result code before finishing
        super.finish();
    }
}
