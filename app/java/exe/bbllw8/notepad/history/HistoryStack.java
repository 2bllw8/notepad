/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.history;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Optional;

public final class HistoryStack implements Parcelable {
    private final HistoryEntry[] stack;
    private final int capacity;
    private int i;
    private int size;

    public HistoryStack(int capacity) {
        this.stack = new HistoryEntry[capacity];
        this.capacity = capacity;
        this.i = -1;
    }

    public void push(HistoryEntry entry) {
        // By allowing the size to grow beyond the capacity
        // without constraints, we can ensure that if the user
        // makes more than $(capacity + 1) edits and then
        // undoes at least $capacity edits, the stack will not
        // be considered empty
        size++;
        i = (i + 1) % capacity;
        stack[i] = entry;
    }

    public Optional<HistoryEntry> pop() {
        if (size == 0) {
            return Optional.empty();
        } else {
            if (i < 0) {
                i = capacity - 1;
            }
            final HistoryEntry current = stack[i--];
            if (current == null) {
                return Optional.empty();
            } else {
                size--;
                stack[i + 1] = null;
                return Optional.of(current);
            }
        }
    }

    public boolean isNotEmpty() {
        return size > 0;
    }

    HistoryStack(Parcel in) {
        stack = in.createTypedArray(HistoryEntry.CREATOR);
        capacity = in.readInt();
        i = in.readInt();
        size = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(stack, flags);
        dest.writeInt(capacity);
        dest.writeInt(i);
        dest.writeInt(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryStack> CREATOR = new Creator<>() {
        @Override
        public HistoryStack createFromParcel(Parcel in) {
            return new HistoryStack(in);
        }

        @Override
        public HistoryStack[] newArray(int size) {
            return new HistoryStack[size];
        }
    };
}
