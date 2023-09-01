package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private String token;
    private ArrayAdapter<NoteEntity> adapter;
    private List<NoteEntity> noteList;

    private void update() {
        String args = "?token=" + token;
        new ApiCall(this, "GET", "note" + args) {
            public void on_ready(String result) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    JSONArray notesArray = responseJson.getJSONArray("notes");
                    System.out.println("Notes: " + notesArray.toString());

                    noteList.clear();
                    for (int i = 0; i < notesArray.length(); i++) {
                        noteList.add(new NoteEntity(notesArray.getJSONObject(i)));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void on_fail() {
                // Обработка ошибки
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        update();
        Activity ctx = this;
        noteList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteList);
        ListView list = findViewById(R.id.lst_note);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NoteEntity note = noteList.get(i);
                Intent intent = new Intent(ctx, NoteActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("note_id", note.id);
                intent.putExtra("note_title", note.title);
                intent.putExtra("note_content", note.content);
                startActivityForResult(intent, 1);
            }
        });

        token = getIntent().getStringExtra("token");
        update();

    }

    public void onNew_click(View v) {
        String args = "?token=" + token + "&title=untitled&content=somecontent";
        Activity ctx = this;
        new ApiCall(this, "PUT", "note" + args) {
            public void on_ready(String result) {
                Toast.makeText(ctx, "Note created!", Toast.LENGTH_SHORT).show();
                update();
            }

            public void on_fail() {
                // Обработка ошибки
            }
        };
    }

    public void onSignOut_click(View v) {
        String args = "?token=" + token;
        new ApiCall(this, "DELETE", "session" + args) {
            public void on_ready(String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

            public void on_fail() {
                // Обработка ошибки
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            update();
        }
    }
}