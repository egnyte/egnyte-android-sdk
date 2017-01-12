package com.egnyte.androidsdk.picker;

import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.EgnyteFolder;
import com.egnyte.androidsdk.entities.FolderListing;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class FolderListingViewModelTest {

    @Test
    public void getCount() throws Exception {
        assertEquals(0, folderListingViewModel(new ArrayList<EgnyteFile>(), new ArrayList<EgnyteFolder>()).getCount());
        assertEquals(5, folderListingViewModel(files(), folders()).getCount());
    }

    @Test
    public void getItem() throws Exception {
        FolderListingViewModel listing = folderListingViewModel(files(), folders());
        assertEquals("folder 0", ((EgnyteFolder) listing.getItem(0).item).name);
        assertEquals("folder 1", ((EgnyteFolder) listing.getItem(1).item).name);
        assertEquals("folder 2", ((EgnyteFolder) listing.getItem(2).item).name);
        assertEquals("file 3", ((EgnyteFile) listing.getItem(3).item).name);
        assertEquals("file 4", ((EgnyteFile) listing.getItem(4).item).name);
    }

    private ArrayList<EgnyteFolder> folders() {
        ArrayList<EgnyteFolder> folders = new ArrayList<>();
        folders.add(new EgnyteFolder("folder 0", 0, null, null, true));
        folders.add(new EgnyteFolder("folder 1", 0, null, null, true));
        folders.add(new EgnyteFolder("folder 2", 0, null, null, true));
        return folders;
    }

    private ArrayList<EgnyteFile> files() {
        ArrayList<EgnyteFile> files = new ArrayList<>();
        files.add(new EgnyteFile(null, 0, null, "file 3", false, false, null, null, 0L, null, 0));
        files.add(new EgnyteFile(null, 0, null, "file 4", false, false, null, null, 0L, null, 0));
        return files;
    }

    private FolderListingViewModel folderListingViewModel(ArrayList<EgnyteFile> files, ArrayList<EgnyteFolder> folders) {
        return new FolderListingViewModel(new FolderListing(null, null, files, folders));
    }

}