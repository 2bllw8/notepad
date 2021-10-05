/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.help;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Callable;

import exe.bbllw8.notepad.markdown.MarkdownFormatter;
import exe.bbllw8.notepad.R;

final class LoadHelpContentTextTask implements Callable<Optional<CharSequence>> {
    private static final String TAG = "LoadHelpTextTask";
    @NonNull
    private final Resources resources;
    private final MarkdownFormatter formatter;

    public LoadHelpContentTextTask(@NonNull Context context) {
        this.resources = context.getResources();
        this.formatter = new MarkdownFormatter(context.getColor(R.color.markdownCode),
                context.getColor(R.color.markdownQuote));
    }

    @Override
    public Optional<CharSequence> call() {
        final StringBuilder sb = new StringBuilder();
        try (InputStream reader = resources.openRawResource(R.raw.editor_help)) {
            final byte[] buffer = new byte[4096];
            int read = reader.read(buffer, 0, 4096);
            while (read > 0) {
                sb.append(new String(buffer, 0, read));
                read = reader.read(buffer, 0, 4096);
            }

            final CharSequence original = sb.toString();
            return Optional.of(formatter.format(original));
        } catch (IOException e) {
            Log.e(TAG, "Failed to read file", e);
            return Optional.empty();
        }
    }
}
