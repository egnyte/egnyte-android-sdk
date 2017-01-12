package com.egnyte.androidsdk.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents created link
 */
public class Link {

    /**
     * Identifier of a link
     */
    public final String id;
    /**
     * Link itself
     */
    public final String url;
    /**
     * Recipients of the link
     */
    public final ArrayList<String> recipients;

    public Link(String id, String url, ArrayList<String> recipients) {
        this.id = id;
        this.url = url;
        this.recipients = recipients;
    }

    public static Link parse(JSONObject json) throws JSONException {
        return new Link(
                json.getString("id"),
                json.getString("url"),
                parseRecipients(json.getJSONArray("recipients"))
        );
    }

    private static ArrayList<String> parseRecipients(JSONArray recipients) throws JSONException {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < recipients.length(); ++i) {
            result.add(recipients.getString(i));
        }
        return result;
    }
}
