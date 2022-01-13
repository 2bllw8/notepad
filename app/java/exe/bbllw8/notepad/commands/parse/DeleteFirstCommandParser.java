/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.parse;

import java.util.regex.Pattern;

import exe.bbllw8.notepad.commands.EditorCommand;

public final class DeleteFirstCommandParser implements CommandParser<EditorCommand.DeleteFirst> {
    private static final Pattern DELETE_FIRST_PATTERN = Pattern.compile("^\\d+ d/.+$");

    @Override
    public boolean matches(String command) {
        return DELETE_FIRST_PATTERN.matcher(command).find();
    }

    @Override
    public EditorCommand.DeleteFirst parse(String command) {
        final int countDivider = command.indexOf(' ');
        final int count = Integer.parseInt(command.substring(0, countDivider));
        final String toDelete = command.substring(countDivider + 3);
        return new EditorCommand.DeleteFirst(count, toDelete);
    }
}
