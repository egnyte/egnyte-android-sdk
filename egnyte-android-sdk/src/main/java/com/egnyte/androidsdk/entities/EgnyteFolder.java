package com.egnyte.androidsdk.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents Egnyte folder
 */
public final class EgnyteFolder {

    /**
     * Name of the folder
     */
    public final String name;
    /**
     * Timestamp of last modification
     */
    public final long lastModified;
    /**
     * Path of the folder
     */
    public final String path;
    /**
     * Identifier of the folder
     */
    public final String folderId;
    /**
     * Always true
     */
    public final boolean isFolder;

    public EgnyteFolder(String name, long lastModified, String path, String folderId, boolean isFolder) {
        this.name = name;
        this.lastModified = lastModified;
        this.path = path;
        this.folderId = folderId;
        this.isFolder = isFolder;
    }

    public static EgnyteFolder parse(JSONObject jsonObject) throws JSONException {
        return new EgnyteFolder(
                jsonObject.getString("name"),
                jsonObject.getLong("lastModified"),
                jsonObject.getString("path"),
                jsonObject.getString("folder_id"),
                jsonObject.getBoolean("is_folder")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EgnyteFolder that = (EgnyteFolder) o;

        if (lastModified != that.lastModified) return false;
        if (isFolder != that.isFolder) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (folderId != null ? !folderId.equals(that.folderId) : that.folderId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (folderId != null ? folderId.hashCode() : 0);
        result = 31 * result + (isFolder ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EgnyteFolder{" +
                "name='" + name + '\'' +
                ", lastModified=" + lastModified +
                ", path='" + path + '\'' +
                ", folderId='" + folderId + '\'' +
                ", isFolder=" + isFolder +
                '}';
    }
}
