package com.egnyte.androidsdk.picker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.EgnyteFolder;

import java.util.Locale;

/**
 * Basic implementation of {@link AbsViewHoldersFactory}
 */
public class ViewHoldersFactoryImpl extends AbsViewHoldersFactory {

    private LayoutInflater inflater;
    private EgnyteFolderPickerView pickerView;

    public ViewHoldersFactoryImpl(LayoutInflater inflater, EgnyteFolderPickerView pickerView) {
        this.inflater = inflater;
        this.pickerView = pickerView;
    }

    @Override
    protected EgnyteFolderPickerView.ViewHolder newFileViewHolder(ViewGroup parent) {
        return new FileViewHolder(inflater, parent, pickerView);
    }

    @Override
    protected EgnyteFolderPickerView.ViewHolder newFolderViewHolder(ViewGroup parent) {
        return new FolderViewHolder(inflater, parent, pickerView);
    }

    @Override
    protected EgnyteFolderPickerView.ViewHolder newEmptyFolderViewHolder(ViewGroup parent) {
        return new EmptyFolderView(inflater, parent, pickerView);
    }

    @Override
    protected EgnyteFolderPickerView.ViewHolder newErrorViewHolder(ViewGroup parent) {
        return new ErrorViewHolder(inflater, parent, pickerView);
    }

    public static class FileViewHolder extends EgnyteFolderPickerView.ViewHolder<EgnyteFile> {

        public final TextView name;
        public final TextView extensionView;
        public final ImageView icon;

        public FileViewHolder(LayoutInflater inflater, ViewGroup parent, EgnyteFolderPickerView pickerView) {
            this(inflater.inflate(R.layout.file_view, parent, false), pickerView);
        }

        private FileViewHolder(View view, final EgnyteFolderPickerView pickerView) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerView.onFileClicked(getBoundItem());
                }
            });
            name = (TextView) view.findViewById(R.id.folder_content_row_name);
            icon = (ImageView) view.findViewById(R.id.folder_content_icon);
            extensionView = (TextView) view.findViewById(R.id.folder_content_icon_extension);
        }

        @Override
        public void bind(EgnyteFile item) {
            super.bind(item);
            String filename = item.name;
            name.setText(filename);
            icon.setImageResource(R.drawable.unknown);
            int dotIndex = filename.lastIndexOf(".");
            String extension = "?";
            if (dotIndex != -1) {
                extension = filename.substring(dotIndex + 1, Math.min(dotIndex + 4, filename.length()));
            }
            extensionView.setText(extension.toUpperCase(Locale.US));
        }
    }

    public static class FolderViewHolder extends EgnyteFolderPickerView.ViewHolder<EgnyteFolder> {

        public final TextView name;

        public FolderViewHolder(LayoutInflater inflater, ViewGroup parent, EgnyteFolderPickerView pickerView) {
            this(inflater.inflate(R.layout.folder_view, parent, false), pickerView);
        }

        private FolderViewHolder(View view, final EgnyteFolderPickerView pickerView) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerView.onFolderClicked(getBoundItem());
                }
            });
            name = (TextView) view.findViewById(R.id.folder_content_row_name);
        }

        @Override
        public void bind(EgnyteFolder item) {
            super.bind(item);
            name.setText(item.name);
        }
    }

    public static class EmptyFolderView extends EgnyteFolderPickerView.ViewHolder {

        public EmptyFolderView(LayoutInflater inflater, ViewGroup parent, EgnyteFolderPickerView pickerView) {
            this(inflater.inflate(R.layout.empty_folder_view, parent, false), pickerView);
        }

        private EmptyFolderView(View view, final EgnyteFolderPickerView pickerView) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerView.onEmptyFolderClicked();
                }
            });
        }
    }

    public static class ErrorViewHolder extends EgnyteFolderPickerView.ViewHolder<Exception> {

        public ErrorViewHolder(LayoutInflater inflater, ViewGroup parent, EgnyteFolderPickerView pickerView) {
            this(inflater.inflate(R.layout.error_view, parent, false), pickerView);
        }

        ErrorViewHolder(View view, final EgnyteFolderPickerView pickerView) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerView.onErrorClicked(getBoundItem());
                }
            });
        }

        @Override
        public void bind(Exception item) {
            super.bind(item);
        }
    }
}
