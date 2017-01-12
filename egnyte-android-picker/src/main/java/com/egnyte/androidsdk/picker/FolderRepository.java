package com.egnyte.androidsdk.picker;

import android.util.Log;

import com.egnyte.androidsdk.apiclient.egnyte.Callback;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.APIClient;
import com.egnyte.androidsdk.entities.FolderListing;
import com.egnyte.androidsdk.requests.GetFolderListingRequest;

import java.io.IOException;
import java.lang.ref.WeakReference;

class FolderRepository {

    private final String path;

    private FolderListing folderListing;
    private CancelledState cancelledState;
    private boolean isLoading;
    private Exception error;

    public FolderRepository(String path) {
        this.path = path;
    }

    public void load(EgnyteFolderPickerView pickerView, APIClient asyncApiClient) {
        final WeakReference<EgnyteFolderPickerView> pickerRef = new WeakReference<>(pickerView);
        if (!isLoading) {
            isLoading = true;
            cancelledState = new CancelledState();
            asyncApiClient.enqueueAsync(new GetFolderListingRequest(path), new Callback<FolderListing>() {
                @Override
                public void onSuccess(FolderListing result) {
                    isLoading = false;
                    folderListing = result;
                    cancelledState = null;
                    error = null;
                    EgnyteFolderPickerView listener = pickerRef.get();
                    if (listener != null) {
                        listener.onLoadFinished();
                    }
                }

                @Override
                public void onError(IOException resultError) {
                    Log.e("ERROR", resultError.toString());
                    isLoading = false;
                    folderListing = null;
                    cancelledState = null;
                    error = resultError;
                    EgnyteFolderPickerView listener = pickerRef.get();
                    if (listener != null) {
                        listener.onLoadFinished();
                    }
                }
            }, cancelledState);
        }
    }

    public void cancel() {
        if (isLoading && cancelledState != null) {
            isLoading = false;
            cancelledState.setCancelled();
            cancelledState = null;
        }
    }

    public FolderListing getCurrentFolderListing() {
        return folderListing;
    }

    public Exception getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
