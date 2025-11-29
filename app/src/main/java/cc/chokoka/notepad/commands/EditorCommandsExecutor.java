/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.commands;

public interface EditorCommandsExecutor {

    void runFindCommand(FindCommandTask findTask);

    void runSubstituteCommand(SubstituteCommandTask substituteTask);

    default boolean runEditorCommand(String command, String content, int cursor) {
        return EditorCommandParser.parse(command).map(either -> {
            either.forEach(
                    find -> runFindCommand(new FindCommandTask(find.toFind(), content, cursor)),
                    substitution -> runSubstituteCommand(new SubstituteCommandTask(
                            substitution.toFind(),
                            substitution.substituteWith(),
                            content,
                            cursor,
                            substitution.count())));
            return true;
        }).orElse(false);
    }
}
