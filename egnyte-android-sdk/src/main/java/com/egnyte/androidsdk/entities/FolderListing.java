package com.egnyte.androidsdk.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents folder listing
 */
public final class FolderListing {

    /**
     * This folder
     */
    public final EgnyteFolder parentFolder;
    /**
     * Files in folder
     */
    public final List<EgnyteFile> files;
    /**
     * Folders in folder
     */
    public final List<EgnyteFolder> folders;
    /**
     * Listing metadata
     */
    public final Metadata metadata;

    public FolderListing(EgnyteFolder parentFolder, Metadata metadata, ArrayList<EgnyteFile> files, ArrayList<EgnyteFolder> folders) {
        this.parentFolder = parentFolder;
        this.metadata = metadata;
        this.files = Collections.unmodifiableList(files);
        this.folders = Collections.unmodifiableList(folders);
    }

    public static FolderListing parse(JSONObject jsonObject) throws JSONException {
        return new FolderListing(
                EgnyteFolder.parse(jsonObject),
                Metadata.parse(jsonObject),
                parseFiles(jsonObject),
                parseFolders(jsonObject)
        );
    }

    private static ArrayList<EgnyteFolder> parseFolders(JSONObject jsonObject) throws JSONException {
        ArrayList<EgnyteFolder> result = new ArrayList<>();
        JSONArray folders = jsonObject.optJSONArray("folders");
        if (folders != null) {
            for (int i = 0; i < folders.length(); ++i) {
                result.add(EgnyteFolder.parse(folders.getJSONObject(i)));
            }
        }
        return result;
    }

    private static ArrayList<EgnyteFile> parseFiles(JSONObject jsonObject) throws JSONException {
        ArrayList<EgnyteFile> result = new ArrayList<>();
        JSONArray files = jsonObject.optJSONArray("files");
        if (files != null) {
            for (int i = 0; i < files.length(); ++i) {
                result.add(EgnyteFile.parse(files.getJSONObject(i)));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderListing that = (FolderListing) o;

        if (parentFolder != null ? !parentFolder.equals(that.parentFolder) : that.parentFolder != null)
            return false;
        if (files != null ? !files.equals(that.files) : that.files != null) return false;
        if (folders != null ? !folders.equals(that.folders) : that.folders != null) return false;
        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parentFolder != null ? parentFolder.hashCode() : 0;
        result = 31 * result + (files != null ? files.hashCode() : 0);
        result = 31 * result + (folders != null ? folders.hashCode() : 0);
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FolderListing{" +
                "parentFolder=" + parentFolder +
                ", files=" + files +
                ", folders=" + folders +
                ", metadata=" + metadata +
                '}';
    }

    public static class Metadata {
        public final int count;
        public final int offset;
        public final int totalCount;
        public final boolean restrictMoveDelete;
        public final String publicLinks;
        public final boolean allowLinks;

        public Metadata(int count, int offset, int totalCount, boolean restrictMoveDelete, String publicLinks, boolean allowLinks) {
            this.count = count;
            this.offset = offset;
            this.totalCount = totalCount;
            this.restrictMoveDelete = restrictMoveDelete;
            this.publicLinks = publicLinks;
            this.allowLinks = allowLinks;
        }

        public static Metadata parse(JSONObject jsonObject) throws JSONException {
            return new Metadata(
                    jsonObject.getInt("count"),
                    jsonObject.getInt("offset"),
                    jsonObject.getInt("total_count"),
                    jsonObject.getBoolean("restrict_move_delete"),
                    jsonObject.getString("public_links"),
                    jsonObject.getBoolean("allow_links")
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Metadata metadata = (Metadata) o;

            if (count != metadata.count) return false;
            if (offset != metadata.offset) return false;
            if (totalCount != metadata.totalCount) return false;
            if (restrictMoveDelete != metadata.restrictMoveDelete) return false;
            if (allowLinks != metadata.allowLinks) return false;
            if (publicLinks != null ? !publicLinks.equals(metadata.publicLinks) : metadata.publicLinks != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = count;
            result = 31 * result + offset;
            result = 31 * result + totalCount;
            result = 31 * result + (restrictMoveDelete ? 1 : 0);
            result = 31 * result + (publicLinks != null ? publicLinks.hashCode() : 0);
            result = 31 * result + (allowLinks ? 1 : 0);
            return result;
        }
    }
}