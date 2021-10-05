/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.parse;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import exe.bbllw8.notepad.commands.EditorCommand;

public final class SetCommandParser implements CommandParser<EditorCommand.Set> {
    private static final Pattern SET_PATTERN = Pattern.compile("^set/\\w+/\\w+$");

    @Override
    public boolean matches(@NonNull String command) {
        return SET_PATTERN.matcher(command).find();
    }

    @NonNull
    @Override
    public EditorCommand.Set parse(@NonNull String command) {
        final int lastDivider = command.lastIndexOf('/');
        final String key = command.substring(4, lastDivider);
        final String value = command.substring(lastDivider + 1);
        return new EditorCommand.Set(key, value);
    }
}
