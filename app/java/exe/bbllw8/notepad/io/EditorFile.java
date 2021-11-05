/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import exe.bbllw8.notepad.config.Config;

public final class EditorFile implements Parcelable {
    @NonNull
    private final Uri uri;
    @NonNull
    private final String name;
    private final long size;
    @Config.Eol
    private final String eol;

    public EditorFile(@NonNull Uri uri,
                      @NonNull String name,
                      long size,
                      @Config.Eol String eol) {
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.eol = eol;
    }

    @NonNull
    public Uri getUri() {
        return uri;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    @Config.Eol
    public String getEol() {
        return eol;
    }

    // Parcelable

    protected EditorFile(@NonNull Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        name = in.readString();
        size = in.readLong();
        eol = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(name);
        dest.writeLong(size);
        dest.writeString(eol);
    }

    public static final Creator<EditorFile> CREATOR = new Creator<>() {
        @NonNull
        @Override
        public EditorFile createFromParcel(@NonNull Parcel in) {
            return new EditorFile(in);
        }

        @NonNull
        @Override
        public EditorFile[] newArray(int size) {
            return new EditorFile[size];
        }
    };
}
