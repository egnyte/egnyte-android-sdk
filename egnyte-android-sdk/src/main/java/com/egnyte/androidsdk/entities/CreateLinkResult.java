package com.egnyte.androidsdk.entities;

import com.egnyte.androidsdk.requests.CreateLinkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class represents result of {@link CreateLinkRequest}
 */
public class CreateLinkResult {

    /**
     * Date format of {@link #expiryDate}
     */
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Created links
     */
    public final ArrayList<Link> links;
    /**
     * Path to linked resource
     */
    public final String path;
    /**
     * Link type
     */
    public final CreateLinkRequest.Type type;
    /**
     * Link accessibility
     */
    public final CreateLinkRequest.Accessibility accessibility;
    /**
     * Whether link creator will be notified whenever link is accessed
     */
    public final boolean notify;
    /**
     * Whether link will always link to current version. Might be true only for file links
     */
    public final boolean linkToCurrent;
    /**
     * Whether link was sent via mail
     */
    public final boolean sendMail;
    /**
     * Whether creator of link gets copy of sent mail. Might be true only if {@link sendMail} is true
     */
    public final boolean copyMe;
    /**
     * Creation date. See {@link dateFormat}
     */
    public final String creationDate;
    /**
     * Expiration date of link, might be null
     */
    public final String expiryDate;
    /**
     * After how many accesses link will expire, might be null
     */
    public final Integer expiryClicks;
    /**
     * Password for accessing link. Might be null. Set only if accessibility was set to {@link com.egnyte.androidsdk.requests.CreateLinkRequest.Accessibility#PASSWORD}
     */
    public final String password;

    public CreateLinkResult(ArrayList<Link> links, String path, CreateLinkRequest.Type type, CreateLinkRequest.Accessibility accessibility, boolean notify, boolean linkToCurrent, boolean sendMail, boolean copyMe, String creationDate, String expiryDate, Integer expiryClicks, String password) {
        this.links = links;
        this.path = path;
        this.type = type;
        this.accessibility = accessibility;
        this.notify = notify;
        this.linkToCurrent = linkToCurrent;
        this.sendMail = sendMail;
        this.copyMe = copyMe;
        this.creationDate = creationDate;
        this.expiryDate = expiryDate;
        this.expiryClicks = expiryClicks;
        this.password = password;
    }

    public static CreateLinkResult parse(JSONObject jsonObject) throws JSONException {
        return new CreateLinkResult(
                parseLinks(jsonObject.getJSONArray("links")),
                jsonObject.getString("path"),
                CreateLinkRequest.Type.valueOf(jsonObject.getString("type").toUpperCase(Locale.US)),
                parseAccessibility(jsonObject.optString("accessibility", null)),
                jsonObject.getBoolean("notify"),
                jsonObject.getBoolean("link_to_current"),
                jsonObject.getBoolean("send_mail"),
                jsonObject.getBoolean("copy_me"),
                jsonObject.getString("creation_date"),
                jsonObject.optString("expiry_date", null),
                jsonObject.has("expiry_clicks") ? jsonObject.getInt("expiry_clicks") : null,
                jsonObject.optString("password", null)
        );
    }

    private static CreateLinkRequest.Accessibility parseAccessibility(String accessibility) {
        if (accessibility != null) {
            return CreateLinkRequest.Accessibility.valueOf(accessibility.toUpperCase(Locale.US));
        } else {
            return null;
        }
    }

    private static ArrayList<Link> parseLinks(JSONArray links) throws JSONException {
        ArrayList<Link> result = new ArrayList<>();
        for (int i = 0; i < links.length(); ++i) {
            result.add(Link.parse(links.getJSONObject(i)));
        }
        return result;
    }
}
