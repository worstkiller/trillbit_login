package com.vikas.trillo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vikas.trillo.R;
import com.vikas.trillo.listeners.TrillBitLoginListener;
import com.vikas.trillo.model.SessionModel;
import com.vikas.trillo.model.TrillLoginModel;
import com.vikas.trillo.network.WebLoginTrillBit;
import com.vikas.trillo.utils.SessionManager;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by OFFICE on 5/17/2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, TrillBitLoginListener {
    private static final int RC_SIGN_IN = 101;
    private static final String TAG = LoginActivity.class.getCanonicalName();
    @BindView(R.id.cvFacebookLogin)
    CardView cvFacebookLogin;
    @BindView(R.id.cvGoogleLogin)
    CardView cvGoogleLogin;
    @BindView(R.id.tvTermsConditions)
    TextView tvTermsConditions;
    @BindView(R.id.pbLogin)
    ProgressBar pbLogin;
    @BindView(R.id.tvFacebookLogin)
    TextView tvFacebookLogin;
    @BindView(R.id.tvGoogleLogin)
    TextView tvGoogleLogin;
    private GoogleApiClient mGoogleApiClient;
    private SessionManager sessionManager;
    private CallbackManager mCallbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (isLoginAlready()) {
            openMainActivity();
        } else {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            setSpannableBottomText();
            intializeMemebers();
        }
    }

    private boolean isLoginAlready() {
        Log.d(TAG, " token = " + sessionManager.getData().get(SessionManager.USER_ID) + " " + sessionManager.getData().get(SessionManager.TOKEN));
        if (sessionManager.isLoggedIn()) {
            return true;
        } else return false;
    }

    private void setSpannableBottomText() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tvTermsConditions.getText());
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 31, 37, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 41, 52, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvTermsConditions.setText(spannableStringBuilder);
    }

    private void intializeMemebers() {
        //here initialize the class members
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestServerAuthCode(getString(R.string.web_client_id), false)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                // App code
                                SessionModel sessionModel = new SessionModel();
                                sessionModel.setName(user.optString("name"));
                                sessionModel.setLogin_type("facebook");
                                sessionModel.setUser_id(user.optString("id"));
                                sessionModel.setAuth(loginResult.getAccessToken().getToken());
                                saveSession(sessionModel);
                                Log.d(TAG, "facebook sign in called " + user.toString());
                            }
                        }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @OnClick({R.id.tvFacebookLogin, R.id.tvGoogleLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvFacebookLogin:
                launchFaceBookLogin();
                break;
            case R.id.tvGoogleLogin:
                launchGoogleLogin();
                break;
        }
    }

    private void launchFaceBookLogin() {
        //here launch the facebook login
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email "));
    }

    private void launchGoogleLogin() {
        //here launch the google login
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        makeToast(connectionResult.getErrorMessage());
    }

    private void makeToast(String errorMessage) {
        Snackbar.make(cvFacebookLogin, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                try {
                    GoogleSignInAccount account = result.getSignInAccount();
                    SessionModel sessionModel = new SessionModel();
                    sessionModel.setUser_id(account.getId());
                    sessionModel.setAuth(account.getIdToken());
                    sessionModel.setName(account.getDisplayName());
                    sessionModel.setLogin_type("google");
                    saveSession(sessionModel);
                    Log.d(TAG, account.getIdToken() + " $ " + account.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Google Sign In failed, update UI appropriately
                makeToast(getString(R.string.login_failed));
            }
            Log.d(TAG, "google sign in response " + result.isSuccess());
        }
        //passing back result to the faceook sdk
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void saveSession(SessionModel sessionModel) {
        sessionManager.saveLogin(sessionModel);
        lockButtons(true);
        requestTrillBitLogin(sessionModel);
    }

    @Override
    protected void onDestroy() {
        loginManager = null;
        mGoogleApiClient = null;
        mCallbackManager = null;
        super.onDestroy();
    }

    private void requestTrillBitLogin(SessionModel sessionModel) {
        //here login to trillbit api
        WebLoginTrillBit webLoginTrillBit = new WebLoginTrillBit(this);
        webLoginTrillBit.authenticateUser(sessionModel);
        Log.d(TAG, "trillbit sign in called ");
    }

    private void lockButtons(boolean b) {
        if (b) {
            pbLogin.setVisibility(View.VISIBLE);
            tvFacebookLogin.setEnabled(false);
            tvGoogleLogin.setEnabled(false);
        } else {
            pbLogin.setVisibility(View.GONE);
            tvFacebookLogin.setEnabled(true);
            tvGoogleLogin.setEnabled(true);
        }
    }

    @Override
    public void onLoginSuccess(TrillLoginModel loginModel) {
        //login success
        sessionManager.saveTrillLogin(loginModel);
        lockButtons(false);
        openMainActivity();
        Log.d(TAG, "trillbit sign in success ");
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFail() {
        lockButtons(false);
        makeToast(getString(R.string.error_msg));
        Log.d(TAG, "trillbit sign in failed ");
    }
}
