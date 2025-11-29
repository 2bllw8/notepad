/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.io;

import android.content.ContentResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import exe.bbllw8.either.Try;

public final class EditorFileReaderTask implements Callable<Try<String>> {
    private final ContentResolver cr;
    private final EditorFile editorFile;
    private final int maxSize;

    public EditorFileReaderTask(ContentResolver cr,
                                EditorFile editorFile,
                                int maxSize) {
        this.cr = cr;
        this.editorFile = editorFile;
        this.maxSize = maxSize;
    }

    @Override
    public Try<String> call() {
        final long fileSize = editorFile.getSize();

        return Try.from(() -> {
            if (fileSize > maxSize) {
                throw new EditorFileTooLargeException(fileSize, maxSize);
            } else {
                final StringBuilder sb = new StringBuilder();
                try (final InputStream reader = cr.openInputStream(editorFile.getUri())) {
                    if (reader == null) {
                        throw new IOException("Failed top open " + editorFile.getUri());
                    }
                    final byte[] buffer = new byte[4096];
                    int read = reader.read(buffer, 0, 4096);
                    while (read > 0) {
                        sb.append(new String(buffer, 0, read));
                        read = reader.read(buffer, 0, 4096);
                    }
                    return sb.toString();
                }
            }
        });
    }
}
