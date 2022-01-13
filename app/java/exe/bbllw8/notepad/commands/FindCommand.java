/*
 * Copyright (c) 2022 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

public final class FindCommand {
    private final String toFind;

    public FindCommand(String toFind) {
        this.toFind = toFind;
    }

    public String getToFind() {
        return toFind;
    }
}
