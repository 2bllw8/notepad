/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.parse;

import java.util.regex.Pattern;

import exe.bbllw8.notepad.commands.EditorCommand;

public final class SubstituteFirstCommandParser implements
        CommandParser<EditorCommand.SubstituteFirst> {
    private static final Pattern SUBSTITUTE_FIRST_PATTERN = Pattern.compile("^\\d+ s/.+/.+$");

    @Override
    public boolean matches(String command) {
        return SUBSTITUTE_FIRST_PATTERN.matcher(command).find();
    }

    @Override
    public EditorCommand.SubstituteFirst parse(String command) {
        final int countDivider = command.indexOf(' ');
        final int count = Integer.parseInt(command.substring(0, countDivider));
        final int lastDivider = command.lastIndexOf('/');
        final String toFind = command.substring(countDivider + 3, lastDivider);
        final String replaceWith = command.substring(lastDivider + 1);
        return new EditorCommand.SubstituteFirst(count, toFind, replaceWith);
    }
}
