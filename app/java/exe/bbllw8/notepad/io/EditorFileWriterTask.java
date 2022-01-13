/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

import android.content.ContentResolver;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.Callable;

import exe.bbllw8.either.Try;

public final class EditorFileWriterTask implements Callable<Try<Integer>> {
    private static final String TAG = "EditorFileWriterTask";

    private final ContentResolver cr;
    private final EditorFile editorFile;
    private final String content;

    public EditorFileWriterTask(ContentResolver cr,
                                EditorFile editorFile,
                                String content) {
        this.cr = cr;
        this.editorFile = editorFile;
        this.content = content;
    }

    @Override
    public Try<Integer> call() {
        return Try.from(() -> {
            try (final OutputStream outputStream = cr.openOutputStream(editorFile.getUri())) {
                final String text = content.replaceAll("\n", editorFile.getEol());

                int written = 0;
                try (final InputStream inputStream = new ByteArrayInputStream(text.getBytes())) {
                    final byte[] buffer = new byte[4096];
                    int read = inputStream.read(buffer, 0, 4096);
                    while (read > 0) {
                        outputStream.write(buffer, 0, read);
                        written += read;
                        read = inputStream.read(buffer, 0, 4096);
                    }
                }

                return written;
            } catch (IOException e) {
                Log.e(TAG, "Failed to write file content", e);
                throw new UncheckedIOException(e);
            }
        });
    }
}
