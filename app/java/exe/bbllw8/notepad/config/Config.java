/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.config;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Config {

    private Config() {
    }

    @IntDef(value = {
            Size.SMALL,
            Size.MEDIUM,
            Size.LARGE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Size {
        int SMALL = 0;
        int MEDIUM = 1;
        int LARGE = 2;
    }

    @IntDef(value = {
            Style.MONO,
            Style.SANS,
            Style.SERIF,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Style {
        int MONO = 0;
        int SANS = 1;
        int SERIF = 2;
    }

    @StringDef(value = {
            Eol.CR,
            Eol.LF,
            Eol.CRLF,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Eol {
        String CR = "\r";
        String CRLF = "\r\n";
        String LF = "\n";
    }

    public static final int DEFAULT_SIZE = Size.MEDIUM;
    public static final int DEFAULT_STYLE = Style.MONO;
    public static final boolean DEFAULT_AUTO_PAIR = true;
    public static final boolean DEFAULT_SHOW_COMMAND_BAR = false;
}
