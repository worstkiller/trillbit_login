package com.vikas.trillo.listeners;

import com.vikas.trillo.model.TrillLoginModel;

/**
 * Created by OFFICE on 5/18/2017.
 */

public interface TrillBitLoginListener {
    public void onLoginSuccess(TrillLoginModel loginModel);
    public void onLoginFail();
}
