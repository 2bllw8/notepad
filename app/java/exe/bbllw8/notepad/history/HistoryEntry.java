/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.history;

import android.os.Parcel;
import android.os.Parcelable;

public final class HistoryEntry implements Parcelable {
    private final CharSequence before;
    private final CharSequence after;
    private final int start;

    public HistoryEntry(CharSequence before,
                        CharSequence after,
                        int start) {
        this.before = before;
        this.after = after;
        this.start = start;
    }

    protected HistoryEntry(Parcel in) {
        before = in.readString();
        after = in.readString();
        start = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(before.toString());
        dest.writeString(after.toString());
        dest.writeInt(start);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryEntry> CREATOR = new Creator<>() {
        @Override
        public HistoryEntry createFromParcel(Parcel in) {
            return new HistoryEntry(in);
        }

        @Override
        public HistoryEntry[] newArray(int size) {
            return new HistoryEntry[size];
        }
    };

    public CharSequence getBefore() {
        return before;
    }

    public CharSequence getAfter() {
        return after;
    }

    public int getStart() {
        return start;
    }
}
