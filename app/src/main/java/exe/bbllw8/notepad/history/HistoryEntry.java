/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.history;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public final class HistoryEntry implements Parcelable {
    private static final byte ZERO = (byte) 0;
    private static final byte ONE = (byte) 1;

    @Nullable
    private final CharSequence before;
    @Nullable
    private final CharSequence after;
    private final int start;

    public HistoryEntry(@Nullable CharSequence before,
                        @Nullable CharSequence after,
                        int start) {
        this.before = before;
        this.after = after;
        this.start = start;
    }

    protected HistoryEntry(@NonNull Parcel in) {
        before = in.readByte() == ONE
                ? in.readString()
                : null;
        after = in.readByte() == ONE
                ? in.readString()
                : null;
        start = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (before == null) {
            dest.writeByte(ZERO);
        } else {
            dest.writeByte(ONE);
            dest.writeString(before.toString());
        }
        if (after == null) {
            dest.writeByte(ZERO);
        } else {
            dest.writeByte(ONE);
            dest.writeString(after.toString());
        }
        dest.writeInt(start);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryEntry> CREATOR = new Creator<>() {
        @NonNull
        @Override
        public HistoryEntry createFromParcel(@NonNull Parcel in) {
            return new HistoryEntry(in);
        }

        @NonNull
        @Override
        public HistoryEntry[] newArray(int size) {
            return new HistoryEntry[size];
        }
    };

    @NonNull
    public Optional<CharSequence> getBefore() {
        return Optional.ofNullable(before);
    }

    @NonNull
    public Optional<CharSequence> getAfter() {
        return Optional.ofNullable(after);
    }

    public int getStart() {
        return start;
    }
}
