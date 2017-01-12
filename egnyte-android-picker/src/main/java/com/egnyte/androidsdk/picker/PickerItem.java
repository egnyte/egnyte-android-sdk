package com.egnyte.androidsdk.picker;

import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.EgnyteFolder;

public abstract class PickerItem<T> {

    public static class File extends PickerItem<EgnyteFile> {

        public File(EgnyteFile item) {
            super(Type.FILE, item);
        }
    }

    public static class Folder extends PickerItem<EgnyteFolder> {

        public Folder(EgnyteFolder item) {
            super(Type.FOLDER, item);
        }
    }

    public static class Error extends PickerItem<Exception> {

        public Error(Exception item) {
            super(Type.ERROR, item);
        }
    }

    public static class EmptyFolder extends PickerItem<Void> {

        public EmptyFolder() {
            super(Type.EMPTY_FOLDER, null);
        }
    }

    public enum Type {
        FILE, FOLDER, EMPTY_FOLDER, ERROR
    }

    public final Type type;
    public final T item;

    public PickerItem(Type type, T item) {
        this.type = type;
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PickerItem<?> that = (PickerItem<?>) o;

        if (type != that.type) return false;
        if (item != null ? !item.equals(that.item) : that.item != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
