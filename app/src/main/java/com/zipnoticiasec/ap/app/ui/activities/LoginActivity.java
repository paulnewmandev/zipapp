package com.zipnoticiasec.ap.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.UserModel;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

import static com.zipnoticiasec.ap.app.models.UserModel.SOCIAL_FACEBOOK;
import static com.zipnoticiasec.ap.app.models.UserModel.SOCIAL_GOOGLE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.POST_USER;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;

public class LoginActivity extends AppCompatActivity {

    private int GOOGLE_SIG_IN = 1005;

    private ImageView loginFacebook, loginGmail;
    private LoginButton btnFacebook;

    CallbackManager callbackManager = CallbackManager.Factory.create();
    //Google
    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount account = null;

    private UserModel mUser;
    private int mTypeSocial;
    private ProgressBar mProgressBar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_SIG_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViews(){
        mUser = new UserModel();
        mProgressBar = findViewById(R.id.progressBar);
        loginFacebook = findViewById(R.id.login_facebook);
        loginGmail = findViewById(R.id.login_gmail);
        btnFacebook = findViewById(R.id.login_button);
        if(!Prefs.with(this).getString(SessionUtils.PREFS.user_data.name(), "").isEmpty()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            initActivity();
        }
    }

    private void initActivity(){

        loginFacebook.setOnClickListener(v -> btnFacebook.performClick());

        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    Log.d("Respuesta Facebook", object.toString());
                    try {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mUser.nombres = object.getString("first_name") + " "+ object.getString("last_name");
                        JSONObject pictureData = object.getJSONObject("picture");
                        JSONObject pictureInfo = pictureData.getJSONObject("data");
                        mUser.imagen = pictureInfo.getString("url");
                        mUser.email = object.getString("email");
                        mUser.telefono = "";
                        mTypeSocial = SOCIAL_FACEBOOK;
                        logIn();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, first_name, last_name, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                // App code
            }
        });

        btnFacebook.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        loginGmail.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIG_IN);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            mProgressBar.setVisibility(View.VISIBLE);
            mTypeSocial = SOCIAL_GOOGLE;
            mUser.nombres = account.getGivenName()+ " " + account.getFamilyName();
            if(account.getPhotoUrl() != null) mUser.imagen = "https://lh3.googleusercontent.com"+account.getPhotoUrl().getPath();
            mUser.email = account.getEmail();
            mUser.telefono = "";
            logIn();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void logIn(){
        String mUrl = ApiUrl+POST_USER;
        RequestParams mParams = new RequestParams();
        mParams.put(UserModel.FIELDS.nombres.name(), mUser.nombres);
        mParams.put(UserModel.FIELDS.email.name(), mUser.email);
        mParams.put(UserModel.FIELDS.telefono.name(), mUser.telefono);
        mParams.put(UserModel.FIELDS.imagen.name(), mUser.imagen);
        mParams.put(UserModel.FIELDS.social.name(), mTypeSocial);
        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mProgressBar.setVisibility(View.GONE);
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        Prefs.with(mContext).save(SessionUtils.PREFS.user_data.name(), mJson.getString(DATA));
                        startActivity(new Intent(mContext, IntroActivity.class));
                        finish();
                    }else{
                        showToast(mContext, mJson.getString(MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(mContext, getString(R.string.error_json));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mProgressBar.setVisibility(View.GONE);
                showToast(mContext, getString(R.string.error_server));
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

}
