package com.egnyte.androidsdk.picker;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import com.egnyte.androidsdk.apiclient.egnyte.client.APIClient;
import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.EgnyteFolder;
import com.egnyte.androidsdk.entities.FolderListing;

import java.util.WeakHashMap;

/**
 * View for browsing through folders in Egnyte cloud
 */
public class EgnyteFolderPickerView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {

    private Callback callback;
    private final NavigateBackView navBackView;
    private final RecyclerView recyclerView;

    private ListingAdapter adapter;

    private FolderRepository currentFolderRepository;
    private WeakHashMap<String, FolderRepository> cache;
    private APIClient client;
    private LayoutInflater inflater;

    public EgnyteFolderPickerView(Context context) {
        this(context, null);
    }

    public EgnyteFolderPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EgnyteFolderPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public EgnyteFolderPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, getPickerStyleId(context));
        this.inflater = LayoutInflater.from(contextThemeWrapper);
        setOnRefreshListener(this);

        View content = inflater.inflate(R.layout.picker_view, this, false);
        navBackView = (NavigateBackView) content.findViewById(R.id.picker_back_view);
        navBackView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPath(navBackView.getPath());
            }
        });

        recyclerView = (RecyclerView) content.findViewById(R.id.picker_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(contextThemeWrapper, attrs, defStyleAttr, defStyleRes));
        addView(content);

        setColorSchemeColors(getColorSchemeColors(contextThemeWrapper));
        setProgressBackgroundColorSchemeColor(getProgressBackgroundColor(contextThemeWrapper));
    }

    private int getPickerStyleId(Context context) {
        TypedValue outValue = new TypedValue();
        boolean found = context.getTheme().resolveAttribute(R.attr.egnytePickerStyle, outValue, true);
        return found ? outValue.data : R.style.EgnytePickerStyle;
    }

    private int getColorSchemeColors(Context context) {
        TypedValue outValue = new TypedValue();
        boolean found = context.getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        return found ? outValue.data : Color.parseColor("#139591");
    }

    private int getProgressBackgroundColor(Context context) {
        TypedValue outValue = new TypedValue();
        boolean found = context.getTheme().resolveAttribute(R.attr.egnytePickerBackground, outValue, true);
        return found ? outValue.data : Color.parseColor("#FFFFFF");
    }

    /**
     * Initializes view.
     * @param client {@link APIClient}, cannot be null
     * @param callback {@link Callback} for handling interaction with this view, cannot be null
     * @throws IllegalArgumentException when {@link APIClient} or {@link Callback} is null
     */
    public void init(APIClient apiClient, Callback callback) {
        init(apiClient, callback, null);
    }

    /**
     * Initializes view.
     * @param client {@link APIClient}, cannot be null
     * @param callback {@link Callback} for handling interaction with this view, cannot be null
     * @param factory {@link} pass class extending {@link AbsViewHoldersFactory} to modify how content is displayed, might be null
     * @throws IllegalArgumentException when {@link APIClient} or {@link Callback} is null
     */
    public void init(APIClient client, Callback callback, AbsViewHoldersFactory factory) throws IllegalArgumentException {
        if (client == null) {
            throw new IllegalArgumentException("APIClient cannot be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        this.client = client;
        this.callback = callback;
        this.adapter = new ListingAdapter(factory == null ? new ViewHoldersFactoryImpl(inflater, this) : factory);
        recyclerView.setAdapter(adapter);
        if (cache == null) {
            cache = new WeakHashMap<>();
        }
        if (currentFolderRepository == null) {
            currentFolderRepository = new FolderRepository("");
            cache.put("", currentFolderRepository);
        }
        if (currentFolderRepository.getCurrentFolderListing() == null) {
            currentFolderRepository.load(this, client);
            setRefreshing(true);
            updateAdapterWithCurrentRepository();
        }
    }

    void onLoadFinished() {
        setRefreshing(false);
        updateAdapterWithCurrentRepository();
        callback.onLoadingFinished();
    }

    private void updateAdapterWithCurrentRepository() {
        String path = currentFolderRepository.getPath();
        if (path.isEmpty()) {
            navBackView.update(null, "");
        } else {
            int lastSeparatorIndex = path.lastIndexOf("/");
            String parentPath = null;
            String name;
            if (lastSeparatorIndex == -1) {
                parentPath = null;
                name = null;
            } else {
                parentPath = path.substring(0, lastSeparatorIndex);
                name = path.substring(lastSeparatorIndex + 1);
            }
            navBackView.update(name, parentPath);
        }

        FolderListingViewModel listingViewModel = null;
        if (currentFolderRepository.getCurrentFolderListing() != null) {
            listingViewModel = new FolderListingViewModel(currentFolderRepository.getCurrentFolderListing());
        }

        Exception error = currentFolderRepository.getError();
        PickerItem errorViewModel = null;
        if (error != null) {
            errorViewModel = new PickerItem.Error(error);
        }
        PickerItem emptyFolder = null;
        if (listingViewModel != null && listingViewModel.getCount() == 0) {
            emptyFolder = new PickerItem.EmptyFolder();
        }

        adapter.update(listingViewModel, errorViewModel, emptyFolder);
    }

    @Override
    public void onRefresh() {
        currentFolderRepository.load(this, client);
    }

    /**
     * Loads given path
     *
     * @param path Egnyte's folder path to load
     */
    public void openPath(String path) {
        if (path == null) {
            path = "";
        }
        currentFolderRepository.cancel();
        setRefreshing(false);
        currentFolderRepository = cache.get(path);
        if (currentFolderRepository == null) {
            currentFolderRepository = new FolderRepository(path);
            cache.put(path, currentFolderRepository);
        }
        currentFolderRepository.load(this, client);
        setRefreshing(true);
        updateAdapterWithCurrentRepository();
    }

    /**
     * Reloads current folder
     */
    public void reload() {
        currentFolderRepository.load(this, client);
        setRefreshing(true);
    }

    /**
     * Get current folder listing. Might be null if folder is not loaded yet
     *
     * @return Current folder listing, null if folder is not loaded yet
     */
    public FolderListing getCurrentFolderListing() {
        return currentFolderRepository.getCurrentFolderListing();
    }

    /**
     * Returns current path
     *
     * @return current path
     */
    public String getCurrentPath() {
        return currentFolderRepository.getPath();
    }

    /**
     * Navigates to parent folder
     */
    public void goUp() {
        openPath(navBackView.getPath());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof BaseSavedState) {
            super.onRestoreInstanceState(((BaseSavedState) state).getSuperState());

            if (state instanceof SavedState) {
                SavedState savedState = (SavedState) state;
                if (cache == null) {
                    cache = new WeakHashMap<>();
                }
                if (client != null && !NullSafeEquals.check(getCurrentPath(), savedState.path)) {
                    if (currentFolderRepository != null) {
                        currentFolderRepository.cancel();
                    }
                    currentFolderRepository = new FolderRepository(savedState.path);
                    cache.put(savedState.path, currentFolderRepository);
                    currentFolderRepository.load(this, client);
                    setRefreshing(true);
                    updateAdapterWithCurrentRepository();
                }
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    void onFileClicked(EgnyteFile file) {
        callback.onFileClicked(file);
    }

    void onFolderClicked(EgnyteFolder folder) {
        if (!callback.onFolderClicked(folder)) {
            openPath(folder.path);
        }
    }

    void onEmptyFolderClicked() {
        if (!callback.onEmptyFolderClicked()) {
            reload();
        }
    }

    void onErrorClicked(Exception error) {
        if (!callback.onErrorClicked(error)) {
            reload();
        }
    }

    /**
     * Returns {@link FilesystemItem} at given position
     *
     * @param position
     * @return
     */
    public FilesystemItem getItemAtPosition(int position) {
        return FolderListingViewModel.getItemAtPosition(position, getCurrentFolderListing());
    }

    public static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private final String path;

        protected SavedState(Parcelable superState, EgnyteFolderPickerView egnyteFolderPickerView) {
            super(superState);
            path = egnyteFolderPickerView.getCurrentPath();
        }

        public SavedState(Parcel source) {
            super(source);
            path = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(path);
        }
    }

    /**
     * {@link ViewHolder} for displaying items
     *
     * @param <T> class of displayed item
     */
    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder {

        private T boundItem;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(T item) {
            boundItem = item;
        }

        public final T getBoundItem() {
            return boundItem;
        }
    }

    /**
     * Implement this interface for handling interactions with {@link EgnyteFolderPickerView}
     */
    public static interface Callback {

        /**
         * Called when file is clicked
         *
         * @param file clicked file
         * @return true if click is handled, false if view should handle it
         */
        boolean onFileClicked(EgnyteFile file);

        /**
         * Called when folder is clicked
         *
         * @param folder clicked folder
         * @return true if click is handled, false if view should handle it
         */
        boolean onFolderClicked(EgnyteFolder folder);

        /**
         * Called when empty folder view is clicked
         *
         * @return true if click is handled, false if view should handle it
         */
        boolean onEmptyFolderClicked();

        /**
         * Called when error folder view is clicked
         *
         * @return true if click is handled, false if view should handle it
         */
        boolean onErrorClicked(Exception error);

        /**
         * Called when {@link EgnyteFolderPickerView} finished (successfully or not) loading folder
         */
        void onLoadingFinished();
    }

    /**
     * Base class implementing {@link Callback}.
     */
    public abstract static class BaseCallback implements Callback {

        @Override
        public boolean onFolderClicked(EgnyteFolder folder) {
            return false;
        }

        @Override
        public boolean onEmptyFolderClicked() {
            return false;
        }

        @Override
        public boolean onErrorClicked(Exception error) {
            return false;
        }

        @Override
        public void onLoadingFinished() {
        }
    }
}
