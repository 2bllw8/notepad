/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.io;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import exe.bbllw8.either.Try;

public final class EditorFileLoaderTask implements Callable<Try<EditorFile>> {
    private static final String[] FILE_INFO_QUERY = {
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE
    };

    private final ContentResolver cr;
    private final Uri uri;
    private final String eol;

    public EditorFileLoaderTask(ContentResolver cr,
                                Uri uri) {
        this(cr, uri, System.lineSeparator());
    }

    public EditorFileLoaderTask(ContentResolver cr,
                                Uri uri,
                                String eol) {
        this.cr = cr;
        this.uri = uri;
        this.eol = eol;
    }

    @Override
    public Try<EditorFile> call() {
        return Try.from(() -> {
            try (final Cursor infoCursor = cr.query(uri, FILE_INFO_QUERY, null, null, null)) {
                if (infoCursor != null && infoCursor.moveToFirst()) {
                    final String name = infoCursor.getString(0);
                    final long size = infoCursor.getLong(1);
                    return new EditorFile(uri, name, size, eol);
                } else {
                    throw new FileNotFoundException(uri.toString());
                }
            }
        });
    }
}
