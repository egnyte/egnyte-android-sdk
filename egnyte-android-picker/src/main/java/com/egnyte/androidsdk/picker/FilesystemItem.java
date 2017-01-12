package com.egnyte.androidsdk.picker;

import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.EgnyteFolder;
import com.egnyte.androidsdk.entities.EgnyteSearchedFile;
import com.egnyte.androidsdk.entities.EgnyteSearchedFolder;

public final class FilesystemItem {

    private final EgnyteFile file;
    private final EgnyteFolder folder;
    private final EgnyteSearchedFile searchedFile;
    private final EgnyteSearchedFolder searchedFolder;

    public FilesystemItem(EgnyteFile file) {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        this.file = file;
        this.folder = null;
        this.searchedFile = null;
        this.searchedFolder = null;
    }

    public FilesystemItem(EgnyteFolder folder) {
        if (folder == null) {
            throw new IllegalArgumentException();
        }
        this.folder = folder;
        this.file = null;
        this.searchedFile = null;
        this.searchedFolder = null;
    }

    public FilesystemItem(EgnyteSearchedFile searchedFile) {
        if (searchedFile == null) {
            throw new IllegalArgumentException();
        }
        this.searchedFile = searchedFile;
        this.file = null;
        this.folder = null;
        this.searchedFolder = null;
    }
    public FilesystemItem(EgnyteSearchedFolder searchedFolder) {
        if (searchedFolder == null) {
            throw new IllegalArgumentException();
        }
        this.searchedFolder = searchedFolder;
        this.file = null;
        this.folder = null;
        this.searchedFile = null;
    }

    public boolean isFolder() {
        if (file != null) {
            return false;
        } else if (folder != null) {
            return true;
        } else if (searchedFile != null) {
            return  false;
        } else if (searchedFolder != null) {
            return true;
        }
        throw new IllegalStateException();
    }

    public boolean isFile() {
        return !isFolder();
    }

    public String getName() {
        if (file != null) {
            return file.name;
        } else if (folder != null) {
            return folder.name;
        } else if (searchedFile != null) {
            return searchedFile.name;
        } else if (searchedFolder != null) {
            return searchedFile.name;
        }
        throw new IllegalStateException();
    }

    public String getPath() {
        if (file != null) {
            return file.path;
        } else if (folder != null) {
            return folder.path;
        } else if (searchedFile != null) {
            return searchedFile.path;
        } else if (searchedFolder != null) {
            return searchedFolder.path;
        }
        throw new IllegalStateException();
    }

    public EgnyteFile getFile() {
        return file;
    }

    public EgnyteFolder getFolder() {
        return folder;
    }

    public EgnyteSearchedFile getSearchedFile() {
        return searchedFile;
    }

    public EgnyteSearchedFolder getSearchedFolder() {
        return searchedFolder;
    }
}
