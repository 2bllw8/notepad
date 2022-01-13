/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.task;

import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public final class SubstituteFirstCommandTask implements Callable<String> {
    private final String toFind;
    private final String replacement;
    private final String content;
    private final int count;
    private final int cursor;

    public SubstituteFirstCommandTask(String toFind,
                                      String replacement,
                                      String content,
                                      int count,
                                      int cursor) {
        this.toFind = toFind;
        this.replacement = replacement;
        this.content = content;
        this.count = count;
        this.cursor = cursor;
    }

    @Override
    public String call() {
        return content.substring(0, cursor) + substitute(Pattern.compile(toFind),
                content.substring(cursor),
                count);
    }

    private String substitute(Pattern pattern,
                              String content,
                              int count) {
        if (count == 0) {
            return content;
        } else {
            return substitute(pattern,
                    pattern.matcher(content).replaceFirst(replacement),
                    count - 1);
        }
    }
}
