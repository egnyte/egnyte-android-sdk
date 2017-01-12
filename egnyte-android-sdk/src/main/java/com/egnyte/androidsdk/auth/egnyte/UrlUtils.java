package com.egnyte.androidsdk.auth.egnyte;

import java.net.MalformedURLException;
import java.net.URL;

class UrlUtils {

    static URL parseUrl(String stringURL) {
        URL result = null;
        try {
            result = new URL(stringURL);
        } catch (MalformedURLException ignore) {
        }
        return result;
    }
}
