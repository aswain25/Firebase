package com.example.admin.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView tvEmail;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvEmail = findViewById(R.id.tvEamil);
        authManager = AuthManager.getDefault();
        //tvEmail.setText(authManager.getUser.getEmail());
    }
}
