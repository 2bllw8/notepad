/*
 * Copyright (c) 2022 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

public final class SubstituteCommand {
    public static final int ALL = -1;

    private final int count;
    private final String toFind;
    private final String substituteWith;

    public SubstituteCommand(int count,
                             String toFind,
                             String substituteWith) {
        this.count = count;
        this.toFind = toFind;
        this.substituteWith = substituteWith;
    }

    public int getCount() {
        return count;
    }

    public String getToFind() {
        return toFind;
    }

    public String getSubstituteWith() {
        return substituteWith;
    }
}
