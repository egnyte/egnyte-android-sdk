package com.egnyte.androidsdk.picker;

import android.view.ViewGroup;

import com.egnyte.androidsdk.apiclient.egnyte.client.APIClient;

/**
 * Abstract class for creating {@link com.egnyte.androidsdk.picker.EgnyteFolderPickerView.ViewHolder} that are displayed in {@link EgnyteFolderPickerView}.
 * Extend this class to display custom views.
 * {@see {@link ViewHoldersFactoryImpl} and {@link EgnyteFolderPickerView#init(APIClient, AbsViewHoldersFactory, EgnyteFolderPickerView.Callback)}}
 */
public abstract class AbsViewHoldersFactory {

    public EgnyteFolderPickerView.ViewHolder onCreateViewHolder(ViewGroup parent, PickerItem.Type viewType) {
        switch (viewType) {
            case FILE:
                return newFileViewHolder(parent);
            case FOLDER:
                return newFolderViewHolder(parent);
            case EMPTY_FOLDER:
                return newEmptyFolderViewHolder(parent);
            case ERROR:
                return newErrorViewHolder(parent);
            default:
                throw new IllegalArgumentException("Unexpected view type");
        }
    }

    protected abstract EgnyteFolderPickerView.ViewHolder newFileViewHolder(ViewGroup parent);

    protected abstract EgnyteFolderPickerView.ViewHolder newFolderViewHolder(ViewGroup parent);

    protected abstract EgnyteFolderPickerView.ViewHolder newEmptyFolderViewHolder(ViewGroup parent);

    protected abstract EgnyteFolderPickerView.ViewHolder newErrorViewHolder(ViewGroup parent);

}
