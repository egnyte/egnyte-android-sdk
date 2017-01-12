package com.egnyte.androidsdk.auth.egnyte;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.egnyte.androidsdk.R;
import com.egnyte.androidsdk.apiclient.egnyte.Callback;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.AuthClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * {@link Activity} with a {@link WebView} that controlls authentication process
 */
public final class EgnyteAuthActivity extends Activity {

    static final String REDIRECT_URL = "https://egnyte.com";

    private CancelledState cancelledState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.egnyte_auth_webview);

        WebView webView = (WebView) findViewById(R.id.egnyte_auth_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        EgnyteAuthRequest authRequest = getIntent().getParcelableExtra(EgnyteAuth.KEY_AUTH_REQUEST);
        webView.setWebViewClient(new EgnyteAuthWebViewClient(this, authRequest));

        webView.loadUrl(makeUrl(authRequest));
        findViewById(R.id.egnyte_auth_progress).setVisibility(View.VISIBLE);
    }

    static String makeUrl(EgnyteAuthRequest egnyteAuthRequest) {
        Uri.Builder uriBuilder = null;
        if (egnyteAuthRequest.getEgnyteDomainURL() != null) {
            uriBuilder = Uri.parse(egnyteAuthRequest.getEgnyteDomainURL().toString()).buildUpon();
            uriBuilder.appendPath("puboauth").appendPath("token");
        } else {
            switch (egnyteAuthRequest.getRegion()) {
                case US:
                    uriBuilder = Uri.parse("https://us-partner-integrations.egnyte.com").buildUpon();
                case EU:
                    uriBuilder = Uri.parse("https://partner-integrations.egnyte.com").buildUpon();
            }
            uriBuilder.appendPath("services").appendPath("oauth").appendPath("code");
        }
        uriBuilder.appendQueryParameter("client_id", egnyteAuthRequest.getKey());
        uriBuilder.appendQueryParameter("state", egnyteAuthRequest.getState());
        StringBuilder scopeBuilder = new StringBuilder();
        for (EgnyteAuthRequest.Scope scopePart : egnyteAuthRequest.getScope()) {
            if (scopeBuilder.length() != 0) {
                scopeBuilder.append(" ");
            }
            scopeBuilder.append(scopePart.toString());
        }
        if (scopeBuilder.length() != 0) {
            uriBuilder.appendQueryParameter("scope", scopeBuilder.toString());
        }
        uriBuilder.appendQueryParameter("mobile", "1");
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("redirect_uri", REDIRECT_URL);
        return uriBuilder.build().toString();
    }

    static class EgnyteAuthWebViewClient extends WebViewClient {

        private final EgnyteAuthActivity activity;
        private final EgnyteAuthRequest authRequest;
        private URL previousBaseUrl;

        public EgnyteAuthWebViewClient(EgnyteAuthActivity activity, EgnyteAuthRequest authRequest) {
            this.activity = activity;
            this.authRequest = authRequest;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            URL newBaseUrl = UrlUtils.parseUrl(getBaseUrl(Uri.parse(url)));
            if (newBaseUrl == null || !newBaseUrl.equals(previousBaseUrl)) {
                activity.findViewById(R.id.egnyte_auth_progress).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            activity.findViewById(R.id.egnyte_auth_progress).setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Uri uri = Uri.parse(url);
            if (getBaseUrl(uri).equals(REDIRECT_URL)) {
                return parseResult(webView, uri);
            } else {
                previousBaseUrl = UrlUtils.parseUrl(getBaseUrl(uri));
                return super.shouldOverrideUrlLoading(webView, url);
            }
        }

        private String getBaseUrl(Uri uri) {
            return new Uri.Builder()
                    .scheme(uri.getScheme())
                    .authority(uri.getAuthority())
                    .build()
                    .toString();
        }

        private boolean parseResult(WebView webView, Uri uri) {
            String error = uri.getQueryParameter("error");
            String code = uri.getQueryParameter("code");
            if (error == null && code != null) {
                if (!authRequest.getState().equals(uri.getQueryParameter("state"))) {
                    activity.setResult(EgnyteAuth.RESULT_INVALID_STATE);
                    activity.finish();
                } else {
                    activity.exchangeToken(
                            getEgnyteDomainURL(),
                            authRequest.getKey(),
                            authRequest.getSharedSecret(),
                            code, authRequest.getScope()
                    );
                }
            } else if (error != null) {
                activity.setResult(EgnyteAuth.RESULT_ERROR_ACCESS_DENIED);
                activity.finish();
            } else {
                activity.finish();
            }
            return true;
        }

        private URL getEgnyteDomainURL() {
            return previousBaseUrl == null ? authRequest.getEgnyteDomainURL() : previousBaseUrl;
        }
    }

    void exchangeToken(final URL baseURL, String clientId, String clientSecret, String code, EgnyteAuthRequest.Scope[] scope) {
        cancelledState = new CancelledState();
        new AuthClient(baseURL).execute(new ExchangeCodeRequest(clientId, clientSecret, code, scope), cancelledState, new Callback<String>() {

            WeakReference<Activity> activityWeakReference = new WeakReference<Activity>(EgnyteAuthActivity.this);

            @Override
            public void onSuccess(String token) {
                Activity activity = activityWeakReference.get();
                if (activity != null) {
                    activity.setResult(Activity.RESULT_OK, new Intent().putExtra(
                            EgnyteAuth.KEY_AUTH_RESPONSE, new EgnyteAuthResult(token, baseURL)
                    ));
                    activity.finish();
                }
            }

            @Override
            public void onError(IOException error) {
                Activity activity = activityWeakReference.get();
                if (activity != null) {
                    activity.setResult(
                            EgnyteAuth.RESULT_ERROR_TOKEN_EXCHANGE_FAILED,
                            new Intent().putExtra(EgnyteAuth.KEY_EXCEPTION, error)
                    );
                    activity.finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cancelledState != null) {
            cancelledState.setCancelled();
            cancelledState = null;
        }
    }

}
