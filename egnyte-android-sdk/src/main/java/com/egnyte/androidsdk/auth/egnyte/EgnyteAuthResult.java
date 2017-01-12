package com.egnyte.androidsdk.auth.egnyte;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

/**
 * This class contains successfull authentication result
 */
public final class EgnyteAuthResult implements Parcelable {

    public static final Parcelable.Creator<EgnyteAuthResult> CREATOR = new Parcelable.Creator<EgnyteAuthResult>() {
        public EgnyteAuthResult createFromParcel(Parcel in) {
            return new EgnyteAuthResult(in);
        }

        public EgnyteAuthResult[] newArray(int size) {
            return new EgnyteAuthResult[size];
        }
    };
    private final String authToken;
    private final URL egnyteDomainURL;

    public EgnyteAuthResult(String code, URL egnyteDomainURL) {
        this.authToken = code;
        this.egnyteDomainURL = egnyteDomainURL;
    }

    public EgnyteAuthResult(Parcel in) {
        authToken = in.readString();
        egnyteDomainURL = UrlUtils.parseUrl(in.readString());
    }

    /**
     * Get obtained auth token
     * @return
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Get Egnyte domain URL associated with obtained token
     * @return
     */
    public URL getEgnyteDomainURL() {
        return egnyteDomainURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(authToken);
        out.writeString(egnyteDomainURL.toString());
    }
}
