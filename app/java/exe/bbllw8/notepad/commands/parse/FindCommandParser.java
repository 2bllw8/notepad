/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.parse;

import java.util.regex.Pattern;

import exe.bbllw8.notepad.commands.EditorCommand;

public final class FindCommandParser implements CommandParser<EditorCommand.Find> {
    private static final Pattern FIND_PATTERN = Pattern.compile("^/.+$");

    @Override
    public boolean matches(String command) {
        return FIND_PATTERN.matcher(command).find();
    }

    @Override
    public EditorCommand.Find parse(String command) {
        final String toFind = command.substring(1);
        return new EditorCommand.Find(toFind);
    }
}
