package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.body.JSONBody;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.CreateLinkResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * This class represents <a href="https://developers.egnyte.com/docs/Egnyte_Link_API_Documentation#Create-a-Link">Create a Link</a> request
 */
public class CreateLinkRequest extends JSONResponseRequest<CreateLinkResult> {

    public enum Type {
        FILE, FOLDER, UPLOAD;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.US);
        }
    }

    public enum Accessibility {
        ANYONE, PASSWORD, DOMAIN, RECIPIENTS;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.US);
        }
    }

    public enum Protection {
        PREVIEW, NONE
    }

    /**
     * Use {@link CreateFolderLinkRequestBuilder}, {@link CreateFileLinkRequestBuilder} or {@link CreateUploadLinkRequestBuilder}
     */
    public CreateLinkRequest(String cloudPath, Type type, Accessibility accessibility, Boolean sendEmail, List<String> recipients, String message, Boolean copyMe, Boolean notify, Boolean linkToCurrent, String expiryDate, Integer expiryClicks, Protection protection, Boolean addFileName, Boolean folderPerReceipent) {
        super("POST", "/pubapi/v1/links", null, null, null, new JSONBody(createRequestJson(cloudPath, type, accessibility, sendEmail, recipients, message, copyMe, notify, linkToCurrent, expiryDate, expiryClicks, protection, addFileName, folderPerReceipent)));
    }

    private static JSONObject createRequestJson(String cloudPath, Type type, Accessibility accessibility, Boolean sendEmail, List<String> recipients, String message, Boolean copyMe, Boolean notify, Boolean linkToCurrent, String expiryDate, Integer expiryClicks, Protection protection, Boolean addFileName, Boolean folderPerReceipent) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", cloudPath);
            jsonObject.put("type", type);
            jsonObject.putOpt("accessibility", accessibility);
            jsonObject.putOpt("notify", notify);
            jsonObject.putOpt("send_email", sendEmail);
            jsonObject.putOpt("recipients", recipients == null ? null : new JSONArray(recipients));
            jsonObject.putOpt("message", message);
            jsonObject.putOpt("copy_me", copyMe);
            jsonObject.putOpt("link_to_current", linkToCurrent);
            jsonObject.putOpt("expiry_date", expiryDate);
            jsonObject.putOpt("expiry_clicks", expiryClicks);
            jsonObject.putOpt("protection", protection);
            jsonObject.putOpt("add_file_name", addFileName);
            jsonObject.putOpt("folder_per_recipient", folderPerReceipent);
        } catch (JSONException ignore) {
        }
        return jsonObject;
    }

    @Override
    protected CreateLinkResult parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        return CreateLinkResult.parse(jsonObject);
    }
}
