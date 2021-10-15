/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

import android.content.ContentResolver;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import exe.bbllw8.either.Either;
import exe.bbllw8.either.Left;
import exe.bbllw8.either.Right;

public final class EditorFileReaderTask implements Callable<Either<IOException, String>> {
    private static final String TAG = "EditorFileReaderTask";

    @NonNull
    private final ContentResolver cr;
    @NonNull
    private final EditorFile editorFile;
    private final int maxSize;

    public EditorFileReaderTask(@NonNull ContentResolver cr,
                                @NonNull EditorFile editorFile,
                                int maxSize) {
        this.cr = cr;
        this.editorFile = editorFile;
        this.maxSize = maxSize;
    }

    @NonNull
    @Override
    public Either<IOException, String> call() {
        final long fileSize = editorFile.getSize();

        if (fileSize > maxSize) {
            Log.e(TAG, "The file is too big: " + fileSize + '/' + maxSize);
            return new Left<>(new EditorFileTooLargeException(fileSize, maxSize));
        } else {
            final StringBuilder sb = new StringBuilder();
            try (final InputStream reader = cr.openInputStream(editorFile.getUri())) {
                final byte[] buffer = new byte[4096];
                int read = reader.read(buffer, 0, 4096);
                while (read > 0) {
                    sb.append(new String(buffer, 0, read));
                    read = reader.read(buffer, 0, 4096);
                }
                return new Right<>(sb.toString());
            } catch (IOException e) {
                Log.e(TAG, "Failed to read the file contents", e);
                return new Left<>(e);
            }
        }
    }
}
