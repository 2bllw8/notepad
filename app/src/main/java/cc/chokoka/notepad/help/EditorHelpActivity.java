/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package cc.chokoka.notepad.help;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cc.chokoka.notepad.R;
import cc.chokoka.notepad.task.TaskExecutor;
import cc.chokoka.notepad.ui.UiUtils;

public final class EditorHelpActivity extends Activity {

    private final TaskExecutor taskExecutor = new TaskExecutor();

    private ViewGroup rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        rootView = findViewById(R.id.helpRoot);
        TextView contentView = findViewById(R.id.helpContent);

        enableEdgeToEdge();

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        taskExecutor.runTask(new LoadHelpContentTextTask(this),
                tryResult -> tryResult.forEach(
                        contentView::setText,
                        err -> contentView.setText(R.string.help_error)));
    }

    @Override
    protected void onDestroy() {
        taskExecutor.terminate();
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }

    private void enableEdgeToEdge() {
        if (Build.VERSION.SDK_INT < 35) {
            return;
        }

        UiUtils.applyInsets(rootView, (mlp, insets) -> {
            mlp.topMargin = insets.top;
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
        });
    }

}
