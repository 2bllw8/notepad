/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.help;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;
import java.util.concurrent.Callable;

import exe.bbllw8.either.Try;
import cc.chokoka.notepad.R;
import cc.chokoka.notepad.markdown.MarkdownFormatter;

final class LoadHelpContentTextTask implements Callable<Try<CharSequence>> {
    private final Resources resources;
    private final MarkdownFormatter formatter;

    public LoadHelpContentTextTask(Context context) {
        this.resources = context.getResources();
        this.formatter = new MarkdownFormatter(context.getColor(R.color.markdown_code),
                context.getColor(R.color.markdown_quote));
    }

    @Override
    public Try<CharSequence> call() {
        final StringBuilder sb = new StringBuilder();
        return Try.from(() -> {
            try (InputStream reader = resources.openRawResource(R.raw.help_manual)) {
                final byte[] buffer = new byte[4096];
                int read = reader.read(buffer, 0, 4096);
                while (read > 0) {
                    sb.append(new String(buffer, 0, read));
                    read = reader.read(buffer, 0, 4096);
                }

                final CharSequence original = sb.toString();
                return formatter.format(original);
            }
        });
    }
}
