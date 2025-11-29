/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.main;

import android.graphics.Point;

import java.util.concurrent.Callable;

public final class GetCursorCoordinatesTask implements Callable<Point> {
    private final String text;
    private final int cursorPosition;

    public GetCursorCoordinatesTask(String text,
                                    int cursorPosition) {
        this.text = text;
        this.cursorPosition = cursorPosition;
    }

    @Override
    public Point call() {
        if (text.isEmpty()) {
            return new Point(1, 1);
        } else {
            int column = 1;
            int row = 1;
            int i = 0;
            while (i < cursorPosition) {
                if (text.charAt(i++) == '\n') {
                    column = 1;
                    row++;
                } else {
                    column++;
                }
            }

            return new Point(column, row);
        }
    }
}
