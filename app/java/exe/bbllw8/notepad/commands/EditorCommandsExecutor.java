/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

public interface EditorCommandsExecutor {

    void runFindCommand(EditorCommand.Find command);

    void runDeleteAllCommand(EditorCommand.DeleteAll command);

    void runDeleteFirstCommand(EditorCommand.DeleteFirst command);

    void runSubstituteAllCommand(EditorCommand.SubstituteAll command);

    void runSubstituteFirstCommand(EditorCommand.SubstituteFirst command);

    default boolean runCommand(EditorCommand command) {
        if (command instanceof EditorCommand.Find) {
            runFindCommand((EditorCommand.Find) command);
            return true;
        } else if (command instanceof EditorCommand.DeleteAll) {
            runDeleteAllCommand((EditorCommand.DeleteAll) command);
            return true;
        } else if (command instanceof EditorCommand.DeleteFirst) {
            runDeleteFirstCommand((EditorCommand.DeleteFirst) command);
            return true;
        } else if (command instanceof EditorCommand.SubstituteAll) {
            runSubstituteAllCommand((EditorCommand.SubstituteAll) command);
            return true;
        } else if (command instanceof EditorCommand.SubstituteFirst) {
            runSubstituteFirstCommand((EditorCommand.SubstituteFirst) command);
            return true;
        } else {
            return false;
        }
    }
}
