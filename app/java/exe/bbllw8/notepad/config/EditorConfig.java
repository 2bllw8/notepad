/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.config;

import android.content.Context;
import android.content.SharedPreferences;

public final class EditorConfig {
    private static final String CONFIG_PREFERENCES = "config";

    private static final String KEY_SIZE = "text_size";
    private static final String KEY_STYLE = "text_style";
    private static final String KEY_AUTO_PAIR = "auto_pair";
    private static final String KEY_SHOW_COMMAND_BAR = "show_command_bar";
    private static final String KEY_WRAP_TEXT = "wrap_text";

    private final EditorConfigListener configListener;
    private final SharedPreferences preferences;

    // Runtime configs

    @Config.Eol
    private String eol;

    public EditorConfig(Context context,
                        EditorConfigListener listener) {
        this.configListener = listener;
        this.preferences = context.getSharedPreferences(CONFIG_PREFERENCES, Context.MODE_PRIVATE);
        this.eol = System.lineSeparator();
    }

    @Config.Size
    public int getTextSize() {
        return preferences.getInt(KEY_SIZE, Config.DEFAULT_SIZE);
    }

    public void setTextSize(@Config.Size int size) {
        preferences.edit()
                .putInt(KEY_SIZE, size)
                .apply();
        configListener.onTextSizeChanged(size);
    }

    @Config.Style
    public int getTextStyle() {
        return preferences.getInt(KEY_STYLE, Config.DEFAULT_STYLE);
    }

    public void setTextStyle(@Config.Style int style) {
        preferences.edit()
                .putInt(KEY_STYLE, style)
                .apply();
        configListener.onTextStyleChanged(style);
    }

    public boolean getAutoPairEnabled() {
        return preferences.getBoolean(KEY_AUTO_PAIR, Config.DEFAULT_AUTO_PAIR);
    }

    public void setAutoPairEnabled(boolean enabled) {
        preferences.edit()
                .putBoolean(KEY_AUTO_PAIR, enabled)
                .apply();
        configListener.onAutoPairEnabledChanged(enabled);
    }

    public boolean getShowCommandBar() {
        return preferences.getBoolean(KEY_SHOW_COMMAND_BAR, Config.DEFAULT_SHOW_COMMAND_BAR);
    }

    public void setShowCommandBar(boolean show) {
        preferences.edit()
                .putBoolean(KEY_SHOW_COMMAND_BAR, show)
                .apply();
        configListener.onShowCommandBarChanged(show);
    }

    @Config.Eol
    public String getEol() {
        return eol;
    }

    public void setEol(@Config.Eol String eol) {
        this.eol = eol;
        configListener.onEolChanged(eol);
    }

    public boolean getWrapText() {
        return preferences.getBoolean(KEY_WRAP_TEXT, Config.DEFAULT_WRAP_TEXT);
    }

    public void setWrapText(boolean wrap) {
        preferences.edit()
                .putBoolean(KEY_WRAP_TEXT, wrap)
                .apply();
        configListener.onWrapTextChanged(wrap);
    }

    public void increaseTextSize() {
        final int current = getTextSize();
        switch (current) {
            case Config.Size.SMALL:
                setTextSize(Config.Size.MEDIUM);
                break;
            case Config.Size.MEDIUM:
                setTextSize(Config.Size.LARGE);
                break;
            case Config.Size.LARGE:
                // Do nothing
                break;
        }
    }

    public void decreaseTextSize() {
        final int current = getTextSize();
        switch (current) {
            case Config.Size.SMALL:
                // Do nothing
                break;
            case Config.Size.MEDIUM:
                setTextSize(Config.Size.SMALL);
                break;
            case Config.Size.LARGE:
                setTextSize(Config.Size.MEDIUM);
                break;
        }
    }
}
