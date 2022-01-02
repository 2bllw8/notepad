/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

public final class EditorFileTooLargeException extends RuntimeException {
    private final long fileSize;
    private final long maxSize;

    public EditorFileTooLargeException(long fileSize, long maxSize) {
        super("File is larger than the max size supported by the configuration"
                + fileSize
                + " / "
                + maxSize);
        this.fileSize = fileSize;
        this.maxSize = maxSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getMaxSize() {
        return maxSize;
    }
}
