/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.main.menu;

import exe.bbllw8.notepad.config.Config;

public interface EditorMenuActions {

    void saveContents();

    void openNewWindow();

    void openFile();

    void openHelp();

    void closeEditor();

    void undoLastAction();

    void setFontSize(@Config.Size int size);

    void setFontStyle(@Config.Style int style);

    void setAutoPairEnabled(boolean enabled);

    void setCommandBarShown(boolean shown);

    void setShellShown(boolean shown);

    void setEol(@Config.Eol String eol);

    void setWrapText(boolean wrap);
}
