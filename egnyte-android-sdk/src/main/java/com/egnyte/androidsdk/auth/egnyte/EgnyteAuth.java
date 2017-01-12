package com.egnyte.androidsdk.auth.egnyte;

import android.app.Activity;
import android.content.Intent;
import android.webkit.CookieManager;

/**
 * This is a helper class for obtaining {@link EgnyteAuthResult}
 */
public final class EgnyteAuth {

    static final int RESULT_ERROR_ACCESS_DENIED = 13517;
    static final int RESULT_INVALID_STATE = 13518;
    static final int RESULT_ERROR_TOKEN_EXCHANGE_FAILED = 13519;

    static final String KEY_AUTH_REQUEST = "auth_request";
    static final String KEY_AUTH_RESPONSE = "auth_response";
    static final String KEY_EXCEPTION = "exception";

    /**
     * Starts authentication process by starting {@link EgnyteAuthActivity} from given {@link Activity}
     *
     * @param egnyteAuthRequest contains necessary data to perform authentication
     * @param activity          used for starting {@link EgnyteAuthActivity}
     */
    public static void start(EgnyteAuthRequest egnyteAuthRequest, Activity activity, int requestCode) {
        Intent intent = new Intent(activity, EgnyteAuthActivity.class);
        intent.putExtra(KEY_AUTH_REQUEST, egnyteAuthRequest);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Unwraps {@link EgnyteAuthResult}. Should be called from {@link Activity#onActivityResult(int, int, Intent)}
     *
     * @param resultCode  as in {@link Activity#onActivityResult(int, int, Intent)}
     * @param data        as in {@link Activity#onActivityResult(int, int, Intent)}
     * @return {@link EgnyteAuthActivity} if arguments are result of calling {@link #start(EgnyteAuthRequest, Activity)}
     * and flow ends succesfully. Null otherwise.
     * @throws AuthFailedException if something went wrong during authentication process
     */
    public static EgnyteAuthResult parseResult(int resultCode, Intent data) throws AuthFailedException {
        if (resultCode == Activity.RESULT_OK) {
            return data.getParcelableExtra(KEY_AUTH_RESPONSE);
        } else if (resultCode == RESULT_ERROR_ACCESS_DENIED) {
            throw new AuthFailedException(AuthFailedException.Type.USER_DENIED_ACCESS);
        } else if (resultCode == RESULT_INVALID_STATE) {
            throw new AuthFailedException(AuthFailedException.Type.INVALID_STATE);
        } else if (resultCode == RESULT_ERROR_TOKEN_EXCHANGE_FAILED) {
            throw new ExchangeTokenFailedException((Exception) data.getSerializableExtra(KEY_EXCEPTION));
        } else {
            return null;
        }
    }

    /**
     * Clears session cookie for a given Egnyte domain URL. Should be called on logout if your application supports
     * logging into several users on the same domain.
     * @param egnyteDomainURL Egnyte domain URL
     */
    public static void forgetSessionCookie(String egnyteDomainURL) {
        if (egnyteDomainURL != null) {
            CookieManager.getInstance().setCookie(egnyteDomainURL, "JSESSIONID=\"\";expires=Thu, 01 Jan 1970 00:00:00 GMT");
        }
    }
}
