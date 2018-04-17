package com.example.admin.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity implements AuthManager.IAuthManager
{

    public static final int GOOGLE_SIGN_IN = 9001;
    public static final int TWITTER_SIGN_IN = 9002;
    private EditText etEmail;
    private EditText etPassword;
    private String email;
    private String password;
    AuthManager authManager;
    private GoogleSignInClient mGoogleSignInClient;
    private TwitterLoginButton mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        AuthManager.Initialize(this);
        authManager = AuthManager.getDefault();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("kjnkj", "lkjlkj"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        mLoginButton = findViewById(R.id.button_twitter_login);
        authManager.registerTwitterLoginButton(mLoginButton);
    }



    public void onSignIn(View view)
    {
        getCreditials();
        authManager.sgnIn(email, password);
    }

    public void onRegister(View view)
    {

        //getCreditials();
        //authManager.register(email, password);
    }
    private void getCreditials()
    {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    @Override
    public void onLoginSuccess(FirebaseUser user)
    {
        String message;
        if(user != null)
        {
            message = "Login successful";
        }
        else
        {
            message = "Login failed";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSignOut(boolean isSignedOut)
    {
    }

    @Override
    public void onLoginError(String error)
    {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN)
        {
            authManager.signIn(data, requestCode);
        }

        else if (requestCode == TWITTER_SIGN_IN)
        {
            mLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void signOut_Clicked(View view) {
        authManager.signOut();
    }

    public void googleSignIn_Clicked(View view)
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }
}
