/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.help;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import exe.bbllw8.notepad.R;
import exe.bbllw8.notepad.task.TaskExecutor;

public final class EditorHelpActivity extends Activity {

    private final TaskExecutor taskExecutor = new TaskExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        final TextView contentView = findViewById(R.id.helpContent);
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
}
