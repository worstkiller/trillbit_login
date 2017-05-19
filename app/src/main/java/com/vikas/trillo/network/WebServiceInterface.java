package com.vikas.trillo.network;

import com.google.gson.JsonObject;
import com.vikas.trillo.model.AudioModel;
import com.vikas.trillo.model.SessionModel;
import com.vikas.trillo.model.TrillLoginModel;
import com.vikas.trillo.model.UploadAudioModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by OFFICE on 5/18/2017.
 */

public interface WebServiceInterface {
    @POST("v1/login/")
    Call<TrillLoginModel> authenticateUser(@Body SessionModel sessionModel);

    @GET("spandey2405/4edb1139a173867bb3bab7c1e8553147/raw/2796583d8eea701b546b20e8cc6c3faae81c51d1/DemoProductInfo.md")
    Call<ResponseBody> getRawProductJson();

    @POST("v1/save_audio/")
    Call<AudioModel> uploadAudio(@Body UploadAudioModel uploadAudioModel);
}
