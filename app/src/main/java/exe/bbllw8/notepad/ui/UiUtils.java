/*
 * Copyright (c) 2025 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.ui;

import android.graphics.Insets;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.annotation.RequiresApi;

import java.util.function.BiConsumer;

public final class UiUtils {

    @RequiresApi(35)
    public static void applyInsets(View view,
                                   BiConsumer<ViewGroup.MarginLayoutParams, Insets> applyInsets) {
        view.setOnApplyWindowInsetsListener((v, windowInsets) -> {
            final Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
            final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                    v.getLayoutParams();
            applyInsets.accept(mlp, insets);
            v.setLayoutParams(mlp);
            return WindowInsets.CONSUMED;
        });
    }

    private UiUtils() {
    }
}
