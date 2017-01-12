package com.egnyte.androidsdk.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents search result of file.
 */
public class EgnyteSearchedFile {

    /**
     * Name of the file
     */
    public final String name;
    /**
     * Path of the file
     */
    public final String path;
    /**
     * Always false
     */
    public final boolean isFolder;
    /**
     * Size in bytes of the file
     */
    public final long size;
    /**
     * Snippet of file's content
     */
    public final String snippet;
    /**
     * Version identifier of the file.
     */
    public final String entryId;
    /**
     * Identifier of all file versions.
     */
    public final String groupId;
    /**
     * Date of last file modification
     */
    public final Date lastModified;
    /**
     * Full name of user who uploaded the file
     */
    public final String uploadedBy;
    /**
     * Username of user who upladed the file
     */
    public final String uploadedByUsername;
    /**
     * Number of file versions
     */
    public final int numVersions;

    public EgnyteSearchedFile(String name, String path, boolean isFolder, long size, String snippet, String entryId,
                              String groupId, Date lastModified, String uploadedBy, String uploadedByUsername,
                              int numVersions) {
        this.name = name;
        this.path = path;
        this.isFolder = isFolder;
        this.size = size;
        this.snippet = snippet;
        this.entryId = entryId;
        this.groupId = groupId;
        this.lastModified = lastModified;
        this.uploadedBy = uploadedBy;
        this.uploadedByUsername = uploadedByUsername;
        this.numVersions = numVersions;
    }

    public static EgnyteSearchedFile parse(JSONObject jsonObject, SimpleDateFormat dateFormat) throws JSONException, ParseException {
        return new EgnyteSearchedFile(
                jsonObject.getString("name"),
                jsonObject.getString("path"),
                jsonObject.getBoolean("is_folder"),
                jsonObject.getLong("size"),
                jsonObject.getString("snippet"),
                jsonObject.getString("entry_id"),
                jsonObject.getString("group_id"),
                dateFormat.parse(jsonObject.getString("last_modified")),
                jsonObject.getString("uploaded_by"),
                jsonObject.getString("uploaded_by_username"),
                jsonObject.getInt("num_versions")
        );
    }
}
