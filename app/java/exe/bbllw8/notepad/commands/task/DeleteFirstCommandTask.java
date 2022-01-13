/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.task;

import java.util.concurrent.Callable;

public final class DeleteFirstCommandTask implements Callable<String> {
    private final String toDelete;
    private final String content;
    private final int count;
    private final int cursor;

    public DeleteFirstCommandTask(String toDelete,
                                  String content,
                                  int count,
                                  int cursor) {
        this.toDelete = toDelete;
        this.content = content;
        this.count = count;
        this.cursor = cursor;
    }

    @Override
    public String call() {
        return new SubstituteFirstCommandTask(toDelete, "", content, count, cursor).call();
    }
}
