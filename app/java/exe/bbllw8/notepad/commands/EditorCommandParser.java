/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import exe.bbllw8.notepad.commands.parse.CommandParser;
import exe.bbllw8.notepad.commands.parse.DeleteAllCommandParser;
import exe.bbllw8.notepad.commands.parse.DeleteFirstCommandParser;
import exe.bbllw8.notepad.commands.parse.FindCommandParser;
import exe.bbllw8.notepad.commands.parse.SetCommandParser;
import exe.bbllw8.notepad.commands.parse.SubstituteAllCommandParser;
import exe.bbllw8.notepad.commands.parse.SubstituteFirstCommandParser;

public final class EditorCommandParser {
    private static final CommandParser<?>[] COMMAND_PARSERS;

    static {
        COMMAND_PARSERS = new CommandParser<?>[]{
                new DeleteAllCommandParser(),
                new DeleteFirstCommandParser(),
                new SetCommandParser(),
                new SubstituteAllCommandParser(),
                new SubstituteFirstCommandParser(),
                // Find always last resort
                new FindCommandParser(),
        };
    }

    public Optional<EditorCommand> parse(String command) {
        if (!command.isEmpty()) {
            for (final CommandParser<?> parser : COMMAND_PARSERS) {
                if (parser.matches(command)) {
                    try {
                        return Optional.of(parser.parse(command));
                    } catch (PatternSyntaxException e) {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.empty();
    }
}
