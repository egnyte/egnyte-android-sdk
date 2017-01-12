package com.egnyte.androidsdk.picker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

class ListingAdapter extends RecyclerView.Adapter<EgnyteFolderPickerView.ViewHolder> {

    private FolderListingViewModel listingViewModel;
    private PickerItem error;
    private PickerItem emptyFolder;

    private AbsViewHoldersFactory viewHoldersFactory;

    public ListingAdapter(AbsViewHoldersFactory viewHoldersFactory) {
        this.viewHoldersFactory = viewHoldersFactory;
    }

    public void update(FolderListingViewModel listing, PickerItem error, PickerItem emptyFolder) {
        boolean changed = !NullSafeEquals.check(listingViewModel, listing)
                || !NullSafeEquals.check(this.error, error)
                || !NullSafeEquals.check(this.emptyFolder, emptyFolder);
        if (changed) {
            notifyItemRangeRemoved(0, getItemCount());
        }
        this.listingViewModel = listing;
        this.error = error;
        this.emptyFolder = emptyFolder;
        if (changed) {
            notifyItemRangeInserted(0, getItemCount());
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public EgnyteFolderPickerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHoldersFactory.onCreateViewHolder(parent, PickerItem.Type.values()[viewType]);
    }

    @Override
    public void onBindViewHolder(EgnyteFolderPickerView.ViewHolder holder, int position) {
        holder.bind(getItem(position).item);
    }

    private PickerItem getItem(int position) {
        if (error != null) {
            return error;
        } else if (emptyFolder != null) {
            return emptyFolder;
        } else {
            return listingViewModel.getItem(position);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += listingViewModel == null ? 0 : listingViewModel.getCount();
        count += emptyFolder == null ? 0 : 1;
        count += error == null ? 0 : 1;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (error != null) {
            return error.type.ordinal();
        } else if (emptyFolder != null) {
            return emptyFolder.type.ordinal();
        } else {
            return listingViewModel.getItem(position).type.ordinal();
        }
    }

}
