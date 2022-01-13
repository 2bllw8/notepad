/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.task;

import android.util.Range;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FindCommandTask implements Callable<Optional<Range<Integer>>> {
    private final Pattern pattern;
    private final CharSequence content;
    private final int cursor;

    public FindCommandTask(String toFind,
                           CharSequence content,
                           int cursor) {
        this.pattern = Pattern.compile(toFind);
        this.content = content;
        this.cursor = cursor;
    }

    @Override
    public Optional<Range<Integer>> call() {
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find(cursor)) {
            return Optional.of(new Range<>(matcher.start(), matcher.end()));
        } else {
            // Try to search from beginning
            if (matcher.find(0)) {
                return Optional.of(new Range<>(matcher.start(), matcher.end()));
            } else {
                return Optional.empty();
            }
        }
    }
}
