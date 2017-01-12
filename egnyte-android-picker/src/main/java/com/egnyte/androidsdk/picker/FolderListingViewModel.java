package com.egnyte.androidsdk.picker;

import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.EgnyteFolder;
import com.egnyte.androidsdk.entities.FolderListing;

import java.util.ArrayList;

class FolderListingViewModel {

    private final ArrayList<PickerItem> content;

    public FolderListingViewModel(FolderListing folderListing) {
        ArrayList<PickerItem> content = new ArrayList<>();
        for (EgnyteFolder folder : folderListing.folders) {
            content.add(new PickerItem.Folder(folder));
        }
        for (EgnyteFile file : folderListing.files) {
            content.add(new PickerItem.File(file));
        }
        this.content = content;
    }

    public int getCount() {
        return content.size();
    }

    public PickerItem getItem(int position) {
        return content.get(position);
    }

    static FilesystemItem getItemAtPosition(int position, FolderListing folderListing) {
        if (folderListing == null) {
            return null;
        }
        if (position < folderListing.folders.size()) {
            EgnyteFolder egnyteFolder = folderListing.folders.get(position);
            return new FilesystemItem(egnyteFolder);
        } else {
            EgnyteFile egnyteFile = folderListing.files.get(position - folderListing.folders.size());
            return new FilesystemItem(egnyteFile);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderListingViewModel that = (FolderListingViewModel) o;

        if (!content.equals(that.content)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}
