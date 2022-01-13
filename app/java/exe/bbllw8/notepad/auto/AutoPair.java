/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.auto;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AutoPair implements TextWatcher {
    private static final Map<Character, String> PAIR_MAP = new HashMap<>();

    private static final byte STATUS_ENABLED = 1;
    private static final byte STATUS_TRACKING = 1 << 1;
    private static final byte STATUS_ENABLED_AND_TRACKING = STATUS_ENABLED | STATUS_TRACKING;

    static {
        PAIR_MAP.put('\'', "'");
        PAIR_MAP.put('"', "\"");
        PAIR_MAP.put('`', "`");
        PAIR_MAP.put('(', ")");
        PAIR_MAP.put('[', "]");
        PAIR_MAP.put('{', "}");
    }

    private final Supplier<Editable> editableTextSupplier;
    private byte status;

    public AutoPair(Supplier<Editable> editableTextSupplier) {
        this.editableTextSupplier = editableTextSupplier;
        this.status = STATUS_TRACKING;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (status == STATUS_ENABLED_AND_TRACKING) {
            if (count == 1) {
                final int i = start + 1;
                final char typed = s.subSequence(start, i).charAt(0);
                final String closePair = PAIR_MAP.get(typed);

                if (closePair != null) {
                    final Editable editable = editableTextSupplier.get();
                    status &= ~STATUS_TRACKING;
                    editable.insert(i, closePair);
                    status |= STATUS_TRACKING;

                    Selection.setSelection(editable, i);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            status |= STATUS_ENABLED;
        } else {
            status &= ~STATUS_ENABLED;
        }
    }
}
