/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

import java.util.Optional;
import java.util.regex.Pattern;

import exe.bbllw8.either.Either;
import exe.bbllw8.either.Left;
import exe.bbllw8.either.Right;

public final class EditorCommandParser {
    private static final Pattern PATTERN_DELETE_ALL = Pattern.compile("^d/.+$");
    private static final Pattern PATTERN_DELETE_FIRST = Pattern.compile("^\\d+ d/.+$");
    private static final Pattern PATTERN_SUBSTITUTE_ALL = Pattern.compile("^s/.+/.+$");
    private static final Pattern PATTERN_SUBSTITUTE_FIRST = Pattern.compile("^\\d+ s/.+/.+$");
    private static final Pattern PATTERN_FIND = Pattern.compile("^/.+$");

    private EditorCommandParser() {
    }

    public static Optional<Either<FindCommand, SubstituteCommand>> parse(String command) {
        if (command.isEmpty()) {
            return Optional.empty();
        } else {
            if (PATTERN_DELETE_ALL.matcher(command).find()) {
                return Optional.of(new Right<>(deleteAll(command)));
            } else if (PATTERN_DELETE_FIRST.matcher(command).find()) {
                return Optional.of(new Right<>(deleteFirst(command)));
            } else if (PATTERN_SUBSTITUTE_ALL.matcher(command).find()) {
                return Optional.of(new Right<>(substituteAll(command)));
            } else if (PATTERN_SUBSTITUTE_FIRST.matcher(command).find()) {
                return Optional.of(new Right<>(substituteFirst(command)));
            } else if (PATTERN_FIND.matcher(command).find()) {
                // Find always last resort
                return Optional.of(new Left<>(find(command)));
            } else {
                return Optional.empty();
            }
        }
    }

    private static SubstituteCommand deleteAll(String command) {
        final String toDelete = command.substring(2);
        return new SubstituteCommand(SubstituteCommand.ALL, toDelete, "");
    }

    private static SubstituteCommand deleteFirst(String command) {
        final int countDivider = command.indexOf(' ');
        final String toDelete = command.substring(countDivider + 3);
        final int count = Integer.parseInt(command.substring(0, countDivider));
        return new SubstituteCommand(count, toDelete, "");
    }

    private static SubstituteCommand substituteAll(String command) {
        final int lastDivider = command.lastIndexOf('/');
        final String toFind = command.substring(2, lastDivider);
        final String substituteWith = command.substring(lastDivider + 1);
        return new SubstituteCommand(SubstituteCommand.ALL, toFind, substituteWith);
    }

    private static SubstituteCommand substituteFirst(String command) {
        final int countDivider = command.indexOf(' ');
        final int lastDivider = command.lastIndexOf('/');
        final String toFind = command.substring(countDivider + 3, lastDivider);
        final String substituteWith = command.substring(lastDivider + 1);
        final int count = Integer.parseInt(command.substring(0, countDivider));
        return new SubstituteCommand(count, toFind, substituteWith);
    }

    private static FindCommand find(String command) {
        final String toFind = command.substring(1);
        return new FindCommand(toFind);
    }
}
