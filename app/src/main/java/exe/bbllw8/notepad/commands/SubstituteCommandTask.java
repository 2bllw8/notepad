/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import exe.bbllw8.either.Try;

public final class SubstituteCommandTask implements Callable<Try<String>> {
    public static final int ALL = -1;

    private final String toFind;
    private final String replacement;
    private final String content;
    private final int count;
    private final int cursor;

    public SubstituteCommandTask(String toFind,
                                 String replacement,
                                 String content,
                                 int cursor,
                                 int count) {
        this.toFind = toFind;
        this.replacement = replacement;
        this.content = content;
        this.count = count;
        this.cursor = cursor;
    }

    @Override
    public Try<String> call() {
        // Pattern may throw an exception if the user input
        // is not a valid regular expression
        return Try.from(() -> {
            final Pattern pattern = Pattern.compile(toFind);
            if (count == ALL) {
                return pattern.matcher(content).replaceAll(replacement);
            } else {
                return content.substring(0, cursor) + substitute(pattern,
                        content.substring(cursor),
                        count);
            }
        });
    }

    private String substitute(Pattern pattern, String content, int count) {
        if (count == 0) {
            return content;
        } else {
            return substitute(pattern,
                    pattern.matcher(content).replaceFirst(replacement),
                    count - 1);
        }
    }
}
