package com.egnyte.androidsdk.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents searched folder
 */
public class EgnyteSearchedFolder {

    /**
     * Name of the folder
     */
    public final String name;
    /**
     * Path of the folder
     */
    public final String path;
    /**
     * Always true
     */
    public final boolean isFolder;

    public EgnyteSearchedFolder(String name, String path, boolean isFolder) {
        this.name = name;
        this.path = path;
        this.isFolder = isFolder;
    }

    public static EgnyteSearchedFolder parse(JSONObject jsonObject) throws JSONException {
        return new EgnyteSearchedFolder(
                jsonObject.getString("name"),
                jsonObject.getString("path"),
                jsonObject.getBoolean("is_folder")
        );
    }
}
