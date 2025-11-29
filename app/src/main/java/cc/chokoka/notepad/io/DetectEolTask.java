/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.io;

import java.util.concurrent.Callable;

import cc.chokoka.notepad.config.Config;

public final class DetectEolTask implements Callable<String> {
    private final String content;

    public DetectEolTask(String content) {
        this.content = content;
    }

    @Override
    public String call() {
        int i = 0;
        final int n = content.length();
        while (i < n) {
            final char c = content.charAt(i++);
            if (c == '\r') {
                return i < n && content.charAt(i) == '\n'
                        ? Config.Eol.CRLF
                        : Config.Eol.CR;
            } else if (c == '\n') {
                return Config.Eol.LF;
            }
        }
        // Assume default
        return System.lineSeparator();
    }
}
