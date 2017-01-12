package com.egnyte.androidsdk.auth.egnyte;

import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.requests.JSONResponseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Used internally in authentication process
 */
public class ExchangeCodeRequest extends JSONResponseRequest<String> {

    public ExchangeCodeRequest(String clientId, String clientSecret, String code, EgnyteAuthRequest.Scope[] scope) {
        super("POST", "/puboauth/token", null, null, null, new ExchangeTokenBody(clientId, clientSecret, code, scope));
    }

    @Override
    protected String parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        return jsonObject.getString("access_token");
    }

    private static class ExchangeTokenBody implements RequestBody {

        private final String clientId;
        private final String clientSecret;
        private final String code;
        private final EgnyteAuthRequest.Scope[] scope;

        public ExchangeTokenBody(String clientId, String clientSecret, String code, EgnyteAuthRequest.Scope[] scope) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.code = code;
            this.scope = scope;
        }

        @Override
        public String getContentType() {
            return "application/x-www-form-urlencoded";
        }

        @Override
        public void writeContent(CancelledState cancelledState, OutputStream os) throws IOException {
            StringBuilder sb = new StringBuilder()
                    .append("client_id=").append(encode(clientId))
                    .append("&client_secret=").append(encode(clientSecret))
                    .append("&redirect_uri=").append(encode(EgnyteAuthActivity.REDIRECT_URL))
                    .append("&code=").append(encode(code))
                    .append("&grant_type=authorization_code");
            StringBuilder scopeBuilder = new StringBuilder();
            for (EgnyteAuthRequest.Scope scopePart : scope) {
                if (scopeBuilder.length() != 0) {
                    scopeBuilder.append(" ");
                }
                scopeBuilder.append(scopePart.toString());
            }
            if (scopeBuilder.length() != 0) {
                sb.append(scopeBuilder);
            }
            os.write(sb.toString().getBytes());
        }

        public static String encode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException ignore) {
                return null;
            }
        }
    }
}
