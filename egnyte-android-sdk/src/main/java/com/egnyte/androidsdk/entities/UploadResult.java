package com.egnyte.androidsdk.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadResult {

    /**
     * SHA512 hash of uploaded file
     */
    public final String checksum;
    /**
     * EntryId identifying version of uploaded file
     */
    public final String entryId;
    /**
     * GroupId identifying uploaded file
     */
    public final String groupId;

    public UploadResult(String checksum, String entryId, String groupId) {
        this.checksum = checksum;
        this.entryId = entryId;
        this.groupId = groupId;
    }

    public static UploadResult parse(JSONObject jsonObject) throws JSONException {
        return new UploadResult(
                jsonObject.getString("checksum"),
                jsonObject.getString("entry_id"),
                jsonObject.getString("group_id")
        );
    }
}
