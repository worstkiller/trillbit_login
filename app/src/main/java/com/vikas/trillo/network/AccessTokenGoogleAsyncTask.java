package com.vikas.trillo.network;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.JsonObject;
import com.vikas.trillo.utils.WebConstants;

import java.io.IOException;

import static com.vikas.trillo.utils.WebConstants.BUNDLE_CODE_AUTH;

/**
 * Created by OFFICE on 5/20/2017.
 */

public class AccessTokenGoogleAsyncTask extends AsyncTaskLoader<String> {

    private String accessToken = null;
    private String authCode = null;

    public AccessTokenGoogleAsyncTask(Context context, Bundle bundle) {
        super(context);
        authCode = bundle.getString(BUNDLE_CODE_AUTH);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (accessToken == null || takeContentChanged()) {
            forceLoad();
        } else {
            deliverResult(accessToken);
        }
    }

    @Override
    public String loadInBackground() {
        ApiServiceNetwork apiServiceNetwork = new ApiServiceNetwork();
        String accessToken = null;
        try {
            JsonObject jsonObject = apiServiceNetwork.getNetworkService(null, WebConstants.GOOGLE_API_END).getAccessToken(authCode,
                    WebConstants.CLIENT_ID,
                    WebConstants.CLIENT_SECRET, WebConstants.REDIRECT_CALL_URL, WebConstants.GRANT_TYPE).execute().body();
            accessToken = jsonObject.get("access_token").getAsString();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    @Override
    public void deliverResult(String data) {
        accessToken = data;
        super.deliverResult(data);
    }
}
