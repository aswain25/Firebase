package com.example.admin.firebase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class AuthManager
{
    private FirebaseAuth mAuth;
    Activity activity;
    IAuthManager listener;
    FirebaseUser user;

    public static AuthManager instance;



    private AuthManager(Activity activity) {
        this.mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        this.listener = (IAuthManager)activity;
    }

    public static AuthManager getDefault()
    {
        return instance;
    }

    public static void Initialize(Activity activity)
    {
        instance = new AuthManager(activity);
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d("TAUTH", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAUTH", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAUTH", "signInWithCredential:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void registerTwitterLoginButton(TwitterLoginButton button)
    {
        button.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("TAUTH", "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w("TAUTH", "twitterLogin:failure", exception);
                //updateUI(null);
            }
        });
    }

    public void register(String email, String password)
    {
        user = null;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AUTHTAG", "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            listener.onLoginSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTHTAG", "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            listener.onLoginError(task.getException().toString());
                        }

                        // ...
                    }
                });
    }

    public void sgnIn(String email, String password)
    {
        user = null;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthTag", "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            listener.onLoginSuccess(user);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTHTAG", "signInWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            listener.onLoginSuccess(user);
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public void signIn(Intent data, int requestCode)
    {
        if (requestCode == MainActivity.GOOGLE_SIGN_IN)
        {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GAUTH", "Google sign in failed", e);
                // ...
            }
        }
        else if (requestCode == MainActivity.TWITTER_SIGN_IN)
        {

        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GAUTH", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            user = mAuth.getCurrentUser();
                            listener.onLoginSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            listener.onLoginSuccess(user);
                        }

                        // ...
                    }
                });
    }

    public void signOut()
    {

        mAuth.signOut();
        if (user != null)
            listener.onSignOut(false);
        else
            listener.onSignOut(true);
    }
    public interface IAuthManager
    {
        void onLoginSuccess(FirebaseUser user);
        void onSignOut(boolean isSignedOut);
        void onLoginError(String error);
    }
}
