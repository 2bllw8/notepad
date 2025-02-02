/*
 * Copyright (c) 2022 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

public record SubstituteCommand(int count, String toFind, String substituteWith) {
    public static final int ALL = -1;

}
