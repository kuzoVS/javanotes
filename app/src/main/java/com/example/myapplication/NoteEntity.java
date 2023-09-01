package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoteEntity {
    public int id;
    public String title;
    public String content;

    public NoteEntity(JSONObject obj) throws JSONException
    {
        id = obj.getInt("id");
        title = obj.getString("title");

    }

    public String toString() { return title; }
}
