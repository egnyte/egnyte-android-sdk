package com.egnyte.androidsdk.apiclient.egnyte.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class URLConnectionFactoryImpl implements URLConnectionFactory {

    @Override
    public HttpURLConnection httpUrlConnection(URL url) throws IOException {
        return (HttpsURLConnection) url.openConnection();
    }
}
