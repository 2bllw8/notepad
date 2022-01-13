/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.task;

import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public final class SubstituteAllCommandTask implements Callable<String> {
    private final String toFind;
    private final String replacement;
    private final String content;

    public SubstituteAllCommandTask(String toFind,
                                    String replacement,
                                    String content) {
        this.toFind = toFind;
        this.replacement = replacement;
        this.content = content;
    }

    @Override
    public String call() {
        return Pattern.compile(toFind)
                .matcher(content)
                .replaceAll(replacement);
    }
}
