/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.main;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

public final class TextEditorView extends EditText {
    private BiConsumer<Integer, Integer> onCursorChanged = (start, end) -> {
    };

    public TextEditorView(@NonNull Context context) {
        super(context);
        setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
    }

    public TextEditorView(@NonNull Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs);
        setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
    }

    public TextEditorView(@NonNull Context context,
                          @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (onCursorChanged != null) {
            onCursorChanged.accept(selStart, selEnd);
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    public void setOnCursorChanged(BiConsumer<Integer, Integer> onCursorChanged) {
        this.onCursorChanged = onCursorChanged;
    }
}
