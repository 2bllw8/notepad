/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import exe.bbllw8.notepad.config.Config;

public final class EditorFile implements Parcelable {
    private final Uri uri;
    private final String name;
    private final long size;
    @Config.Eol
    private final String eol;

    public EditorFile(Uri uri,
                      String name,
                      long size,
                      @Config.Eol String eol) {
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.eol = eol;
    }

    public Uri getUri() {
        return uri;
    }

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

    protected EditorFile(Parcel in) {
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(name);
        dest.writeLong(size);
        dest.writeString(eol);
    }

    public static final Creator<EditorFile> CREATOR = new Creator<>() {
        @Override
        public EditorFile createFromParcel(Parcel in) {
            return new EditorFile(in);
        }

        @Override
        public EditorFile[] newArray(int size) {
            return new EditorFile[size];
        }
    };
}
