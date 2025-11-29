/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.config;

public interface EditorConfigListener {

    void onTextSizeChanged(@Config.Size int newSize);

    void onTextStyleChanged(@Config.Style int newStyle);

    void onAutoPairEnabledChanged(boolean enabled);

    void onShowCommandBarChanged(boolean show);

    void onEolChanged(@Config.Eol String newEol);

    void onWrapTextChanged(boolean wrap);
}
