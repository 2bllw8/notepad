/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.concurrent.Callable;

import exe.bbllw8.either.Failure;
import exe.bbllw8.either.Success;
import exe.bbllw8.either.Try;
import exe.bbllw8.notepad.config.Config;

public final class EditorFileLoaderTask implements Callable<Try<EditorFile>> {
    private static final String[] FILE_INFO_QUERY = {
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE
    };

    @NonNull
    private final ContentResolver cr;
    @NonNull
    private final Uri uri;
    @NonNull
    private final String eol;

    public EditorFileLoaderTask(@NonNull ContentResolver cr,
                                @NonNull Uri uri) {
        this(cr, uri, System.lineSeparator());
    }

    public EditorFileLoaderTask(@NonNull ContentResolver cr,
                                @NonNull Uri uri,
                                @NonNull String eol) {
        this.cr = cr;
        this.uri = uri;
        this.eol = eol;
    }

    @NonNull
    @Override
    public Try<EditorFile> call() {
        try (final Cursor infoCursor = cr.query(uri, FILE_INFO_QUERY, null, null, null)) {
            if (infoCursor.moveToFirst()) {
                final String name = infoCursor.getString(0);
                final long size = infoCursor.getLong(1);
                return new Success<>(new EditorFile(uri, name, size, eol));
            } else {
                return new Failure<>(new FileNotFoundException(uri.toString()));
            }
        }
    }
}
