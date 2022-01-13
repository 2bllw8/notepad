/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands.parse;

import exe.bbllw8.notepad.commands.EditorCommand;

public interface CommandParser<T extends EditorCommand> {

    boolean matches(String command);

    T parse(String command);
}
