package com.egnyte.androidsdk.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Represents file stored in Egnyte cloud
 */
public final class EgnyteFile {

    private static final SimpleDateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    /**
     * SHA512 checksum of the file
     */
    public final String checksum;
    /**
     * Size in bytes of the file
     */
    public final long size;
    /**
     * Path of the file
     */
    public final String path;
    /**
     * Name of the file
     */
    public final String name;
    /**
     * Describes if file is locked for modificaiton.
     */
    public final boolean locked;
    /**
     * Alsways false
     */
    public final boolean isFolder;
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
    public final long lastModified;
    /**
     * Uploader of the file.
     */
    public final String uploadedBy;
    /**
     * Number of the flie versions.
     */
    public final int numVersions;

    public EgnyteFile(String checksum, long size, String path, String name, boolean locked, boolean isFolder,
                      String entryId, String groupId, long lastModified, String uploadedBy, int numVersions) {
        this.checksum = checksum;
        this.size = size;
        this.path = path;
        this.name = name;
        this.locked = locked;
        this.isFolder = isFolder;
        this.entryId = entryId;
        this.groupId = groupId;
        this.lastModified = lastModified;
        this.uploadedBy = uploadedBy;
        this.numVersions = numVersions;
    }

    public static EgnyteFile parse(JSONObject jsonObject) throws JSONException {
        long timestamp = 0L;
        try {
            timestamp = rfc1123.parse(jsonObject.getString("last_modified")).getTime();
        } catch (ParseException ignore) {
        }
        return new EgnyteFile(
                jsonObject.getString("checksum"),
                jsonObject.getLong("size"),
                jsonObject.getString("path"),
                jsonObject.getString("name"),
                jsonObject.getBoolean("locked"),
                jsonObject.getBoolean("is_folder"),
                jsonObject.getString("entry_id"),
                jsonObject.getString("group_id"),
                timestamp,
                jsonObject.getString("uploaded_by"),
                jsonObject.getInt("num_versions")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EgnyteFile that = (EgnyteFile) o;

        if (size != that.size) return false;
        if (locked != that.locked) return false;
        if (isFolder != that.isFolder) return false;
        if (lastModified != that.lastModified) return false;
        if (numVersions != that.numVersions) return false;
        if (checksum != null ? !checksum.equals(that.checksum) : that.checksum != null)
            return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (entryId != null ? !entryId.equals(that.entryId) : that.entryId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (uploadedBy != null ? !uploadedBy.equals(that.uploadedBy) : that.uploadedBy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = checksum != null ? checksum.hashCode() : 0;
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (locked ? 1 : 0);
        result = 31 * result + (isFolder ? 1 : 0);
        result = 31 * result + (entryId != null ? entryId.hashCode() : 0);
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        result = 31 * result + (uploadedBy != null ? uploadedBy.hashCode() : 0);
        result = 31 * result + numVersions;
        return result;
    }

    @Override
    public String toString() {
        return "EgnyteFile{" +
                "checksum='" + checksum + '\'' +
                ", size=" + size +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", locked=" + locked +
                ", isFolder=" + isFolder +
                ", entryId='" + entryId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", lastModified=" + lastModified +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", numVersions=" + numVersions +
                '}';
    }
}
