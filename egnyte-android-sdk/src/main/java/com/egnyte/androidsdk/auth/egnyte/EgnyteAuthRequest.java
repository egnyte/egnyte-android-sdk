package com.egnyte.androidsdk.auth.egnyte;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

/**
 * This class represents data that's necessary to start authorization process.
 * Use {@link EgnyteAuthRequest.Builder} for creating instances.
 */
public final class EgnyteAuthRequest implements Parcelable {

    public static final Creator<EgnyteAuthRequest> CREATOR = new Creator<EgnyteAuthRequest>() {
        public EgnyteAuthRequest createFromParcel(Parcel in) {
            return new EgnyteAuthRequest(in);
        }

        public EgnyteAuthRequest[] newArray(int size) {
            return new EgnyteAuthRequest[size];
        }
    };

    private final String key;
    private final String sharedSecret;
    private final URL egnyteDomainURL;
    private final Region region;
    private final Scope[] scope;
    private final String state;

    EgnyteAuthRequest(String key, String sharedSecret, URL egnyteDomainURL, Region region, Scope[] scope, String state) {
        this.key = key;
        this.sharedSecret = sharedSecret;
        this.egnyteDomainURL = egnyteDomainURL;
        this.region = region;
        this.scope = scope;
        this.state = state;
    }

    EgnyteAuthRequest(Parcel in) {
        key = in.readString();
        sharedSecret = in.readString();
        egnyteDomainURL = unwrapURL(in);
        region = unwrapRegion(in);
        scope = unwrapScope(in);
        state = in.readString();
    }

    String getKey() {
        return key;
    }

    String getSharedSecret() {
        return sharedSecret;
    }

    URL getEgnyteDomainURL() {
        return egnyteDomainURL;
    }

    Region getRegion() {
        return region;
    }

    Scope[] getScope() {
        return scope;
    }

    String getState() {
        return state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(key);
        out.writeString(sharedSecret);
        out.writeString(egnyteDomainURL == null ? null : egnyteDomainURL.toString());
        wrapRegion(out);
        wrapScope(out);
        out.writeString(state);
    }

    private URL unwrapURL(Parcel in) {
        String fromParcel = in.readString();
        if (fromParcel == null) {
            return null;
        }
        return UrlUtils.parseUrl(fromParcel);
    }

    private void wrapRegion(Parcel out) {
        out.writeInt(region == null ? -1 : region.ordinal());
    }

    private Region unwrapRegion(Parcel in) {
        int regionInt = in.readInt();
        return regionInt == -1 ? null : Region.values()[regionInt];
    }

    private void wrapScope(Parcel out) {
        int scopeArraySize = scope.length;
        out.writeInt(scopeArraySize);
        int intArray[] = new int[scope.length];
        for (int i = 0; i < scopeArraySize; ++i) {
            intArray[i] = scope[i].ordinal();
        }
        out.writeInt(scopeArraySize);
    }

    private Scope[] unwrapScope(Parcel in) {
        int scopeArraySize = in.readInt();
        int[] scopeIntArray = new int[scopeArraySize];
        in.readIntArray(scopeIntArray);
        Scope[] result = new Scope[scopeArraySize];
        for (int i = 0; i < scopeArraySize; ++i) {
            result[i] = Scope.values()[scopeIntArray[i]];
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EgnyteAuthRequest that = (EgnyteAuthRequest) o;

        if (!key.equals(that.key)) return false;
        if (!sharedSecret.equals(that.sharedSecret)) return false;
        if (egnyteDomainURL != null ? !egnyteDomainURL.equals(that.egnyteDomainURL) : that.egnyteDomainURL != null)
            return false;
        if (region != that.region) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(scope, that.scope)) return false;
        if (!state.equals(that.state)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + sharedSecret.hashCode();
        result = 31 * result + (egnyteDomainURL != null ? egnyteDomainURL.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(scope);
        result = 31 * result + state.hashCode();
        return result;
    }

    /**
     * {@see {@link Builder#setScope(Scope[])}}
     */
    public enum Scope {
        FILESYSTEM, USER, GROUP, AUDIT, LINK, PERMISSION, BOOKMARK;

        @Override
        public String toString() {
            return "Egnyte." + this.name().toLowerCase(Locale.US);
        }
    }

    public enum Region {
        US, EU
    }


    /**
     * Builder for {@link EgnyteAuthRequest}
     */
    public static final class Builder {

        private String key;
        private String sharedSecret;
        private URL egnyteDomainURL;
        private Region region;
        private Scope[] scope;
        private String state;

        /**
         *
         * @param key The API key that was provided to on application registration
         * @param sharedSecret Shared secret that was provided on application registration.
         *                     If your application key was requested prior to January 2015, please register for a new
         *                     key to get one with a client secret.
         * @throws IllegalArgumentException If key or sharedSecret are empty or null
         */
        public Builder(String key, String sharedSecret) throws IllegalArgumentException {
            throwIfEmpty(key, "Key cannot be empty");
            throwIfEmpty(sharedSecret, "Shared secret cannot be empty");
            this.key = key;
            this.sharedSecret = sharedSecret;
        }

        private static void throwIfEmpty(String value, String errorMessage) {
            if (TextUtils.isEmpty(value)) {
                throw new IllegalArgumentException(errorMessage);
            }
        }

        /**
         * Use this for optimization. Note that it has no effect when {@link #setEgnyteDomainURL(URL)} was called
         * {@link Region.US by default}
         * @param region {@link Region.US} or {@link Region.EU}
         * @return
         */
        public Builder setRegion(Region region) {
            this.region = region;
            return this;
        }

        /**
         * Sets Egnyte domain URL to connect to
         * @param egnyteDomainURL full domain URL to connect to, i.e https://your-domain-name.egnyte.com
         * @return The same builder
         * @throws IllegalArgumentException if domain URL is not valid
         */
        public Builder setEgnyteDomainURL(URL egnyteDomainURL) throws IllegalArgumentException {
            URL normalized = normalizeUrl(egnyteDomainURL);
            validateUrl(normalized);
            this.egnyteDomainURL = normalized;
            return this;
        }

        private static URL normalizeUrl(URL egnyteDomainURL) {
            String normalizedUrlString = egnyteDomainURL.toString().replaceAll("/$", "");
            return UrlUtils.parseUrl(normalizedUrlString);
        }

        private static void validateUrl(URL egnyteDomainURL) {
            boolean isValid = egnyteDomainURL != null
                    && "https".equals(egnyteDomainURL.getProtocol())
                    && !TextUtils.isEmpty(egnyteDomainURL.getHost())
                    && TextUtils.isEmpty(egnyteDomainURL.getQuery())
                    && TextUtils.isEmpty(egnyteDomainURL.getPath());
            if (!isValid) {
                throw new IllegalArgumentException("Illegal base URL");
            }
        }

        /**
         * Scope restricts a token to a subset of APIs. By default, any OAuth token you create will be permitted to access all available Egnyte APIs. You should restrict a given token to a subset of APIs.
         * @param scope array of {@link Scope}
         * @return The same builder
         */
        public Builder setScope(Scope[] scope) {
            this.scope = scope;
            return this;
        }

        /**
         * As described in the OAuth 2.0 spec, this optional parameter is an opaque value used by the client to maintain state between the request and callback. The authorization server includes this value when redirecting the user-agent back to the client. The parameter can be used for preventing cross-site request forgery and passing the Egnyte domain as part of the response from the authorization server.
         * @param state state represented as {@link String}
         * @return The same builder
         */
        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        /**
         * Return new {@link EgnyteAuthRequest} object, setting default values where needed
         * @return new {@link EgnyteAuthRequest}
         */
        public EgnyteAuthRequest build() {
            return new EgnyteAuthRequest(
                    key,
                    sharedSecret,
                    egnyteDomainURL,
                    region == null ? Region.US : region,
                    scope == null ? new Scope[]{} : scope,
                    state == null ? generateRandomString() : state
            );
        }

        private String generateRandomString() {
            SecureRandom secureRandom = new SecureRandom();
            BigInteger bigInteger = new BigInteger(160, secureRandom);
            return bigInteger.toString(Character.MAX_RADIX);
        }
    }
}
