/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.task;

import java.util.concurrent.Callable;

public final class DeleteAllCommandTask implements Callable<String> {
    private final String toDelete;
    private final String content;

    public DeleteAllCommandTask(String toDelete,
                                String content) {
        this.toDelete = toDelete;
        this.content = content;
    }

    @Override
    public String call() {
        return new SubstituteAllCommandTask(toDelete, "", content).call();
    }
}
