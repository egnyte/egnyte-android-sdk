package com.egnyte.androidsdk.sample.auth;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.egnyte.androidsdk.auth.egnyte.AuthFailedException;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuth;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthRequest;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthResponseHelper;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthResult;
import com.egnyte.androidsdk.sample.MainActivity;
import com.egnyte.androidsdk.sample.R;

public class LoginActivity extends Activity {

    private static final int REQUEST_CODE_AUTH = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EgnyteAuthRequest.Builder authRequestBuilder = new EgnyteAuthRequest.Builder(
                        "your client id", "your shared secret"
                );
                //if you know what Egnyte domain to connect to, use setEgnyteDomainURL
                //authRequestBuilder.setEgnyteDomainURL(domainUrl);
                EgnyteAuth.start(authRequestBuilder.build(), LoginActivity.this, REQUEST_CODE_AUTH);
            }
        });

        EgnyteAuthResult savedAuthResponse = EgnyteAuthResponseHelper.loadFromPrefs(this);
        if (savedAuthResponse != null) {
            startMainActivity(savedAuthResponse);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTH) {
            try {
                EgnyteAuthResult authResponse = EgnyteAuth.parseResult(resultCode, data);
                if (authResponse != null) {
                    EgnyteAuthResponseHelper.saveIntoPrefs(authResponse, this);
                    startMainActivity(authResponse);
                }
            } catch (AuthFailedException e) {
                Toast.makeText(this, "Auth failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startMainActivity(EgnyteAuthResult authResponse) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.KEY_AUTH_RESULT, authResponse);
        startActivity(intent);
        finish();
    }
}
