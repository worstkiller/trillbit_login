package com.vikas.trillo.network;

import android.content.Context;
import android.util.Log;

import com.vikas.trillo.listeners.AudioUploadListener;
import com.vikas.trillo.model.AudioModel;
import com.vikas.trillo.model.UploadAudioModel;
import com.vikas.trillo.utils.SessionManager;
import com.vikas.trillo.utils.Utils;
import com.vikas.trillo.utils.WebConstants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by OFFICE on 5/18/2017.
 */

public class UploadAudioService {

    private ApiServiceNetwork apiServiceNetwork;
    private AudioUploadListener audioUploadListener;
    private Context context;
    private SessionManager sessionManager;

    public UploadAudioService(Context context, AudioUploadListener audioUploadListener) {
        this.audioUploadListener = audioUploadListener;
        this.context = context;
        sessionManager = new SessionManager(context);
        apiServiceNetwork = new ApiServiceNetwork();
    }

    public void uploadAudio(String path) {
        final UploadAudioModel uploadAudioModel = new UploadAudioModel();
        uploadAudioModel.setAudio_data(Utils.readBytesFromFile(path));
        try {
            apiServiceNetwork.getNetworkService(sessionManager.getData().get(SessionManager.TRILL_BIT_TOKEN), WebConstants.TRILL_BIT_API_END).uploadAudio(uploadAudioModel).enqueue(new Callback<AudioModel>() {
                @Override
                public void onResponse(Call<AudioModel> call, Response<AudioModel> response) {
                    if (response.code() == 201) {
                        if (response.body().getSuccess()) {
                            sessionManager.saveAudioFiles(response.body());
                            audioUploadListener.onUploadVideoSuccess();
                        } else {
                            audioUploadListener.onUploadVideoFailure();
                        }
                    } else {
                        audioUploadListener.onUploadVideoFailure();
                    }
                    Log.d("audio bytes ", response.code()+"");
                }

                @Override
                public void onFailure(Call<AudioModel> call, Throwable t) {
                    audioUploadListener.onUploadVideoFailure();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            audioUploadListener.onUploadVideoFailure();
        }
    }
}
