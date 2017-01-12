package com.egnyte.androidsdk.auth.egnyte;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is helper class for storing {@link EgnyteAuthResult} in {@link SharedPreferences}
 */
public class EgnyteAuthResponseHelper {

    private static final String AUTH_TOKEN = "AUTH_TOKEN";
    private static final String EGNYTE_DOMAIN_URL = "EGNYTE_DOMAIN_URL";
    private static final String PREFS_NAME = "com.egnyte.androidsdk.auth.egnyte.AUTH_RESPONSE_PREFS";

    /**
     * Persists {@link EgnyteAuthResult}
     * @param authResult  {@link EgnyteAuthResult} to persist
     * @param context {@link Context} to obtain {@link SharedPreferences}
     */
    public static void saveIntoPrefs(EgnyteAuthResult authResult, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(AUTH_TOKEN, authResult.getAuthToken());
        editor.putString(EGNYTE_DOMAIN_URL, authResult.getEgnyteDomainURL().toString());
        editor.apply();
    }

    /**
     * Loads {@link EgnyteAuthResult}
     * @param context {@link Context} to obtain {@link SharedPreferences}
     * @return loaded {@link EgnyteAuthResult}
     */
    public static EgnyteAuthResult loadFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String authToken = prefs.getString(AUTH_TOKEN, null);
        String egnyteDomainUrl = prefs.getString(EGNYTE_DOMAIN_URL, null);
        if (authToken != null && egnyteDomainUrl != null) {
            try {
                return new EgnyteAuthResult(authToken, new URL(egnyteDomainUrl));
            } catch (MalformedURLException e) {
            }
        }
        return null;
    }

    /**
     * Clears saved {@link EgnyteAuthResult} and forgets session cookie allowing to log into new account
     * @param context {@link Context} to obtain {@link SharedPreferences}
     */
    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String egnyteDomainURL = prefs.getString(EGNYTE_DOMAIN_URL, null);
        EgnyteAuth.forgetSessionCookie(egnyteDomainURL);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(AUTH_TOKEN);
        editor.remove(EGNYTE_DOMAIN_URL);
        editor.apply();
    }
}
