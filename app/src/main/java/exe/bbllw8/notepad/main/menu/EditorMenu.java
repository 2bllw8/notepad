/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.main.menu;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.IdRes;

import exe.bbllw8.notepad.R;
import exe.bbllw8.notepad.config.Config;

public final class EditorMenu {
    private final EditorMenuActions actions;

    private final MenuItem undoMenuItem;
    private final MenuItem saveMenuItem;

    private final MenuItem sizeSmallMenuItem;
    private final MenuItem sizeMediumMenuItem;
    private final MenuItem sizeLargeMenuItem;

    private final MenuItem styleMonoMenuItem;
    private final MenuItem styleSansMenuItem;
    private final MenuItem styleSerifMenuItem;

    private final MenuItem eolCrMenuItem;
    private final MenuItem eolCrLfMenuItem;
    private final MenuItem eolLfMenuItem;

    private final MenuItem autoPairMenuItem;
    private final MenuItem showCommandBarMenuItem;
    private final MenuItem wrapTextMenuItem;
    private final MenuItem showShellMenuItem;

    public EditorMenu(EditorMenuActions actions,
                      Menu menu,
                      MenuInflater inflater) {
        this.actions = actions;

        inflater.inflate(R.menu.editor_menu, menu);
        undoMenuItem = menu.findItem(R.id.action_undo);
        saveMenuItem = menu.findItem(R.id.menu_save);

        sizeSmallMenuItem = menu.findItem(R.id.menu_font_size_small);
        sizeMediumMenuItem = menu.findItem(R.id.menu_font_size_medium);
        sizeLargeMenuItem = menu.findItem(R.id.menu_font_size_large);

        styleMonoMenuItem = menu.findItem(R.id.menu_font_style_mono);
        styleSansMenuItem = menu.findItem(R.id.menu_font_style_sans);
        styleSerifMenuItem = menu.findItem(R.id.menu_font_style_serif);

        eolCrMenuItem = menu.findItem(R.id.menu_eol_cr);
        eolCrLfMenuItem = menu.findItem(R.id.menu_eol_crlf);
        eolLfMenuItem = menu.findItem(R.id.menu_eol_lf);

        autoPairMenuItem = menu.findItem(R.id.menu_option_auto_pair);
        showCommandBarMenuItem = menu.findItem(R.id.menu_option_command_bar_show);
        wrapTextMenuItem = menu.findItem(R.id.menu_option_wrap_text);
        showShellMenuItem = menu.findItem(R.id.menu_option_shell_show);
    }

    public boolean onItemSelected(@IdRes int id, boolean isChecked) {
        if (id == R.id.menu_save) {
            actions.saveContents();
            return true;
        } else if (id == R.id.menu_new) {
            actions.openNewWindow();
            return true;
        } else if (id == R.id.menu_open) {
            actions.openFile();
            return true;
        } else if (id == R.id.menu_help) {
            actions.openHelp();
            return true;
        } else if (id == android.R.id.home) {
            actions.closeEditor();
            return true;
        } else if (id == R.id.action_undo) {
            actions.undoLastAction();
            return true;
        } else if (id == R.id.menu_font_size_small) {
            actions.setFontSize(Config.Size.SMALL);
            return true;
        } else if (id == R.id.menu_font_size_medium) {
            actions.setFontSize(Config.Size.MEDIUM);
            return true;
        } else if (id == R.id.menu_font_size_large) {
            actions.setFontSize(Config.Size.LARGE);
            return true;
        } else if (id == R.id.menu_font_style_mono) {
            actions.setFontStyle(Config.Style.MONO);
            return true;
        } else if (id == R.id.menu_font_style_sans) {
            actions.setFontStyle(Config.Style.SANS);
            return true;
        } else if (id == R.id.menu_font_style_serif) {
            actions.setFontStyle(Config.Style.SERIF);
            return true;
        } else if (id == R.id.menu_eol_cr) {
            actions.setEol(Config.Eol.CR);
            return true;
        } else if (id == R.id.menu_eol_crlf) {
            actions.setEol(Config.Eol.CRLF);
            return true;
        } else if (id == R.id.menu_eol_lf) {
            actions.setEol(Config.Eol.LF);
            return true;
        } else if (id == R.id.menu_option_auto_pair) {
            actions.setAutoPairEnabled(!isChecked);
            return true;
        } else if (id == R.id.menu_option_command_bar_show) {
            actions.setCommandBarShown(!isChecked);
            return true;
        } else if (id == R.id.menu_option_wrap_text) {
            actions.setWrapText(!isChecked);
            return true;
        } else if (id == R.id.menu_option_shell_show) {
            actions.setShellShown(!isChecked);
            return true;
        } else {
            return false;
        }
    }

    public void onFontSizeChanged(@Config.Size int size) {
        switch (size) {
            case Config.Size.SMALL:
                sizeSmallMenuItem.setChecked(true);
                break;
            case Config.Size.MEDIUM:
                sizeMediumMenuItem.setChecked(true);
                break;
            case Config.Size.LARGE:
                sizeLargeMenuItem.setChecked(true);
                break;
        }
    }

    public void onFontStyleChanged(@Config.Style int style) {
        switch (style) {
            case Config.Style.MONO:
                styleMonoMenuItem.setChecked(true);
                break;
            case Config.Style.SANS:
                styleSansMenuItem.setChecked(true);
                break;
            case Config.Style.SERIF:
                styleSerifMenuItem.setChecked(true);
                break;
        }
    }

    public void onAutoPairChanged(boolean isEnabled) {
        autoPairMenuItem.setChecked(isEnabled);
    }

    public void onCommandBarVisibilityChanged(boolean isVisible) {
        showCommandBarMenuItem.setChecked(isVisible);
    }

    public void onShellVisibilityChanged(boolean isVisible) {
        showShellMenuItem.setChecked(isVisible);
    }

    public void onWrapTextChanged(boolean isWrapped) {
        wrapTextMenuItem.setChecked(isWrapped);
    }

    public void onEolChanged(@Config.Eol String eol) {
        switch (eol) {
            case Config.Eol.CR:
                eolCrMenuItem.setChecked(true);
                break;
            case Config.Eol.CRLF:
                eolCrLfMenuItem.setChecked(true);
                break;
            case Config.Eol.LF:
                eolLfMenuItem.setChecked(true);
                break;
        }
    }

    public void setUndoAllowed(boolean allowed) {
        undoMenuItem.setEnabled(allowed);
    }

    public void setSaveAllowed(boolean allowed) {
        saveMenuItem.setEnabled(allowed);
    }
}
