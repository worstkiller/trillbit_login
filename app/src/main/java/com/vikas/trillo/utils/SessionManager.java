package com.vikas.trillo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.vikas.trillo.model.AudioModel;
import com.vikas.trillo.model.SessionModel;
import com.vikas.trillo.model.TrillLoginModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OFFICE on 5/17/2017.
 */

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String PREFERENCE_NAME = SessionManager.class.getSimpleName();
    public final static String TOKEN = "token";
    public final static String NAME = "userName";
    public final static String USER_ID = "userID";
    public final static String IS_lOGG_IN = "isLogin";
    public final static String TRILL_BIT_TOKEN = "trillBitToken";
    public final static String AUDIO_JSON = "uploadJson";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Map<String, String> getData() {
        //return user info
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put(TOKEN, sharedPreferences.getString(TOKEN, null));
        stringMap.put(NAME, sharedPreferences.getString(NAME, null));
        stringMap.put(TRILL_BIT_TOKEN, sharedPreferences.getString(TRILL_BIT_TOKEN, null));
        stringMap.put(USER_ID, sharedPreferences.getString(USER_ID, null));
        return stringMap;
    }

    public boolean isLoggedIn() {
        //return the stored value of session
        return sharedPreferences.getBoolean(IS_lOGG_IN, false);
    }

    public void saveLogin(SessionModel sessionModel) {
        //saving the user info
        editor.putString(TOKEN, sessionModel.getAuth());
        editor.putString(NAME, sessionModel.getName());
        editor.putString(USER_ID, sessionModel.getUser_id());
        editor.apply();
    }

    public void saveTrillLogin(TrillLoginModel loginModel) {
        editor.putString(TRILL_BIT_TOKEN, loginModel.getPayload().getAccessToken());
        editor.putString(NAME, loginModel.getPayload().getName());
        editor.putBoolean(IS_lOGG_IN, true);
        editor.apply();
    }

    public void saveAudioFiles(AudioModel audioModel) {
        Gson gson = new Gson();
        String storedString = sharedPreferences.getString(AUDIO_JSON, null);
        try {
            JsonArray jsonObject = new JsonParser().parse(storedString).getAsJsonArray();
            if (jsonObject==null) {
                //empty
                List<AudioModel> newList = new ArrayList<>();
                newList.add(audioModel);
                editor.putString(AUDIO_JSON, gson.toJson(newList)).apply();
                Log.d(SessionManager.class.getCanonicalName(), storedString + "");
            } else {
                //add here
                List<AudioModel> existingList = gson.fromJson(jsonObject, new TypeToken<List<AudioModel>>() {
                }.getType());
                existingList.add(audioModel);
                editor.putString(AUDIO_JSON, gson.toJson(existingList)).apply();
                Log.d(SessionManager.class.getCanonicalName(), storedString + " " + existingList.size());
            }
        } catch (NullPointerException | JsonSyntaxException e) {
            e.printStackTrace();
            List<AudioModel> newList = new ArrayList<>();
            newList.add(audioModel);
            editor.putString(AUDIO_JSON, gson.toJson(newList)).apply();
        }
    }


    public ArrayList<AudioModel> getAudioList() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AudioModel>>() {
        }.getType();
        String clinicInfo = sharedPreferences.getString(AUDIO_JSON, null);
        return gson.fromJson(clinicInfo, type);
    }
}
