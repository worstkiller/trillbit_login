package com.vikas.trillo.network;

import com.vikas.trillo.listeners.TrillBitLoginListener;
import com.vikas.trillo.model.SessionModel;
import com.vikas.trillo.model.TrillLoginModel;
import com.vikas.trillo.utils.WebConstants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by OFFICE on 5/18/2017.
 */

public class WebLoginTrillBit {
    private ApiServiceNetwork apiServiceNetwork;
    private TrillBitLoginListener trillBitLoginListener;

    public WebLoginTrillBit(TrillBitLoginListener trillBitLoginListener) {
        apiServiceNetwork = new ApiServiceNetwork();
        this.trillBitLoginListener = trillBitLoginListener;
    }

    public void authenticateUser(SessionModel sessionModel) {
        try {
            apiServiceNetwork.getNetworkService(null, WebConstants.TRILL_BIT_API_END).authenticateUser(sessionModel).enqueue(new Callback<TrillLoginModel>() {
                @Override
                public void onResponse(Call<TrillLoginModel> call, Response<TrillLoginModel> response) {

                    if (response.code() == 201) {

                        trillBitLoginListener.onLoginSuccess(response.body());

                    } else {
                        trillBitLoginListener.onLoginFail();
                    }
                }

                @Override
                public void onFailure(Call<TrillLoginModel> call, Throwable t) {
                    trillBitLoginListener.onLoginFail();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            trillBitLoginListener.onLoginFail();
        }
    }
}
