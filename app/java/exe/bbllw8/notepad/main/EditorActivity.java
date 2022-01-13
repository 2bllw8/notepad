/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

import exe.bbllw8.notepad.R;
import exe.bbllw8.notepad.auto.AutoPair;
import exe.bbllw8.notepad.commands.EditorCommand;
import exe.bbllw8.notepad.commands.EditorCommandParser;
import exe.bbllw8.notepad.commands.EditorCommandsExecutor;
import exe.bbllw8.notepad.commands.task.DeleteAllCommandTask;
import exe.bbllw8.notepad.commands.task.DeleteFirstCommandTask;
import exe.bbllw8.notepad.commands.task.FindCommandTask;
import exe.bbllw8.notepad.commands.task.SubstituteAllCommandTask;
import exe.bbllw8.notepad.commands.task.SubstituteFirstCommandTask;
import exe.bbllw8.notepad.config.Config;
import exe.bbllw8.notepad.config.EditorConfig;
import exe.bbllw8.notepad.config.EditorConfigListener;
import exe.bbllw8.notepad.help.EditorHelpActivity;
import exe.bbllw8.notepad.history.EditorHistory;
import exe.bbllw8.notepad.io.DetectEolTask;
import exe.bbllw8.notepad.io.EditorFile;
import exe.bbllw8.notepad.io.EditorFileLoaderTask;
import exe.bbllw8.notepad.io.EditorFileReaderTask;
import exe.bbllw8.notepad.io.EditorFileTooLargeException;
import exe.bbllw8.notepad.io.EditorFileWriterTask;
import exe.bbllw8.notepad.main.menu.EditorMenu;
import exe.bbllw8.notepad.main.menu.EditorMenuActions;
import exe.bbllw8.notepad.shell.EditorShell;
import exe.bbllw8.notepad.shell.OpenFileActivity;
import exe.bbllw8.notepad.task.TaskExecutor;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class EditorActivity extends Activity implements
        EditorConfigListener,
        EditorCommandsExecutor,
        EditorMenuActions,
        TextWatcher {
    private static final String KEY_EDITOR_FILE = "file";
    private static final String KEY_HISTORY_STATE = "history";
    private static final String TYPE_PLAIN_TEXT = "text/plain";
    private static final int REQUEST_CREATE_FILE_AND_QUIT = 10;
    private static final int REQUEST_CREATE_FILE = 11;
    private static final float ONE_MB = 1048576f;

    private boolean dirty = false;
    private boolean alwaysAllowSave = false;

    private ActionBar actionBar;
    private View loadView;
    private TextView summaryView;
    private TextEditorView textEditorView;
    private ViewGroup commandBar;
    private EditText commandField;

    private EditorConfig editorConfig;
    private EditorHistory editorHistory;
    private AutoPair autoPair;

    private final TaskExecutor taskExecutor = new TaskExecutor();
    private final EditorCommandParser editorCommandParser = new EditorCommandParser();
    private Optional<EditorFile> editorFile = Optional.empty();
    private Optional<EditorMenu> editorMenu = Optional.empty();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view
        setContentView(R.layout.activity_main);
        actionBar = getActionBar();
        loadView = findViewById(R.id.editorProgress);
        summaryView = findViewById(R.id.editorSummary);
        textEditorView = findViewById(R.id.editorContent);
        commandBar = findViewById(R.id.editorCommandBar);
        commandField = findViewById(R.id.editorCommandField);
        final ImageView commandRunButton = findViewById(R.id.editorCommandRun);

        setCursorCoordinatesSummary(1, 1);
        textEditorView.setOnCursorChanged(this::setCursorCoordinatesSummary);
        commandField.setOnKeyListener((v, code, ev) -> {
            if (code == KeyEvent.KEYCODE_ENTER) {
                runCurrentCommand();
                return true;
            } else {
                return false;
            }
        });

        commandRunButton.setOnClickListener(v -> runCurrentCommand());

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_close);
            actionBar.setHomeActionContentDescription(R.string.action_quit);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize features
        editorHistory = new EditorHistory(textEditorView::getEditableText,
                getResources().getInteger(R.integer.config_history_buffer_size));
        autoPair = new AutoPair(textEditorView::getEditableText);
        editorConfig = new EditorConfig(this, this);
        loadConfig();

        // Load content
        final Intent intent = getIntent();
        if (savedInstanceState == null) {
            if (Intent.ACTION_PROCESS_TEXT.equals(intent.getAction())) {
                // Save snippet feature
                alwaysAllowSave = true;
                setContentInView(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT));
            } else {
                final Uri inputUri = intent.getData();
                if (inputUri == null) {
                    // Empty content
                    registerTextListeners();
                } else {
                    // Load a file
                    loadFile(inputUri);
                }
            }
        } else {
            // Restoring instance, only register the listeners
            registerTextListeners();
        }
    }

    @Override
    protected void onDestroy() {
        taskExecutor.terminate();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        editorFile.ifPresent(x -> outState.putParcelable(KEY_EDITOR_FILE, x));

        // Late initialization
        if (editorHistory != null) {
            outState.putParcelable(KEY_HISTORY_STATE, editorHistory.saveInstance());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        editorFile = Optional.ofNullable(savedInstanceState.getParcelable(KEY_EDITOR_FILE));
        editorFile.ifPresent(x -> setDocumentTitle(x.getName()));

        final Parcelable historyState = savedInstanceState.getParcelable(KEY_HISTORY_STATE);
        if (historyState != null && editorHistory != null) {
            editorHistory.restoreInstance(historyState);
            if (editorHistory.canUndo()) {
                setDirty();
            } else {
                setNotDirty();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        if (menuInflater == null) {
            return super.onCreateOptionsMenu(menu);
        } else {
            editorMenu = Optional.of(new EditorMenu(this, menu, menuInflater))
                    .map(x -> {
                        // Load settings
                        x.onFontSizeChanged(editorConfig.getTextSize());
                        x.onFontStyleChanged(editorConfig.getTextStyle());
                        x.onAutoPairChanged(editorConfig.getAutoPairEnabled());
                        x.onCommandBarVisibilityChanged(editorConfig.getShowCommandBar());
                        x.onShellVisibilityChanged(EditorShell.isEnabled(this));
                        x.onEolChanged(editorConfig.getEol());
                        // If always dirty (snippet / new blank file) always allow to save
                        x.setSaveAllowed(alwaysAllowSave);
                        return x;
                    });
            return true;
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        final int id = item.getItemId();
        final boolean isChecked = item.isChecked();
        return editorMenu.map(x -> x.onItemSelected(id, isChecked))
                .orElseGet(() -> super.onMenuItemSelected(featureId, item));
    }

    @Override
    public void onBackPressed() {
        if (dirty) {
            showQuitMessage();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CREATE_FILE:
                loadNewSaveFile(data.getData(), false);
                break;
            case REQUEST_CREATE_FILE_AND_QUIT:
                loadNewSaveFile(data.getData(), true);
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_Q:
                    onBackPressed();
                    return true;
                case KeyEvent.KEYCODE_PLUS:
                    editorConfig.increaseTextSize();
                    return true;
                case KeyEvent.KEYCODE_EQUALS:
                    if (event.isShiftPressed()) {
                        // US keyboard '+' is 'Shift ='
                        editorConfig.increaseTextSize();
                    }
                    return true;
                case KeyEvent.KEYCODE_MINUS:
                    editorConfig.decreaseTextSize();
                    return true;
                default:
                    return super.onKeyUp(keyCode, event);
            }
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        final CharacterStyle[] toRemove = s.getSpans(0, s.length(),
                MetricAffectingSpan.class);
        for (final CharacterStyle span : toRemove) {
            s.removeSpan(span);
        }
        setDirty();
    }

    /* Menu actions */

    @Override
    public void saveContents() {
        saveContents(false);
    }

    @Override
    public void openNewWindow() {
        startActivity(new Intent(this, EditorActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void openFile() {
        startActivity(new Intent(this, OpenFileActivity.class));
    }

    @Override
    public void openHelp() {
        startActivity(new Intent(this, EditorHelpActivity.class));
    }

    @Override
    public void closeEditor() {
        onBackPressed();
    }

    @Override
    public void undoLastAction() {
        editorHistory.undo();
        if (!editorHistory.canUndo()) {
            setNotDirty();
        }
    }

    @Override
    public void setFontSize(@Config.Size int size) {
        editorConfig.setTextSize(size);
    }

    @Override
    public void setFontStyle(@Config.Style int style) {
        editorConfig.setTextStyle(style);
    }

    @Override
    public void setEol(@Config.Eol String eol) {
        editorConfig.setEol(eol);
    }

    @Override
    public void setAutoPairEnabled(boolean enabled) {
        editorConfig.setAutoPairEnabled(enabled);
    }

    @Override
    public void setCommandBarShown(boolean shown) {
        editorConfig.setShowCommandBar(shown);
    }

    @Override
    public void setShellShown(boolean shown) {
        EditorShell.setEnabled(this, shown);
        editorMenu.ifPresent(x -> x.onShellVisibilityChanged(shown));
    }

    /* File loading */

    private void loadFile(Uri uri) {
        summaryView.setText(R.string.summary_loading);
        loadView.setVisibility(View.VISIBLE);

        taskExecutor.runTask(new EditorFileLoaderTask(getContentResolver(), uri),
                tryResult -> {
                    if (tryResult.isSuccess()) {
                        tryResult.forEach(this::readFile);
                    } else {
                        showOpenErrorMessage();
                    }
                });
    }

    private void readFile(EditorFile editorFile) {
        final int maxSize = getResources().getInteger(R.integer.config_max_file_size);
        taskExecutor.runTask(new EditorFileReaderTask(getContentResolver(), editorFile, maxSize),
                tryResult -> {
                    if (tryResult.isSuccess()) {
                        tryResult.forEach(content -> detectEolAndSetContent(editorFile, content));
                    } else {
                        tryResult.failed().forEach(t -> showReadErrorMessage(editorFile, t));
                    }
                });
    }

    private void detectEolAndSetContent(EditorFile editorFile, String content) {
        taskExecutor.runTask(new DetectEolTask(content),
                eol -> {
                    final EditorFile ef = new EditorFile(editorFile.getUri(),
                            editorFile.getName(),
                            editorFile.getSize(),
                            eol);
                    setContent(ef, content.replaceAll(eol, "\n"));
                });
    }

    private void loadNewSaveFile(Uri uri, boolean quitWhenSaved) {
        taskExecutor.runTask(new EditorFileLoaderTask(getContentResolver(), uri,
                        editorConfig.getEol()),
                tryResult -> {
                    if (tryResult.isSuccess()) {
                        tryResult.forEach(editorFile -> saveNewFile(editorFile, quitWhenSaved));
                    } else {
                        tryResult.failed().forEach(t -> showOpenErrorMessage());
                    }
                });
    }

    private void openFileSaver(boolean quitWhenSaved) {
        String title = textEditorView.getText().toString();
        if (title.length() > 20) {
            title = title.substring(0, 20);
        }
        title = title.replace('\n', ' ') + ".txt";

        final Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType(TYPE_PLAIN_TEXT)
                .putExtra(Intent.EXTRA_TITLE, title);
        startActivityForResult(intent, quitWhenSaved
                ? REQUEST_CREATE_FILE_AND_QUIT
                : REQUEST_CREATE_FILE);
    }

    /* Content operations */

    private void setContent(EditorFile editorFile, String content) {
        this.editorFile = Optional.of(editorFile);
        editorConfig.setEol(editorFile.getEol());
        loadView.setVisibility(View.GONE);
        textEditorView.setVisibility(View.VISIBLE);

        loadConfig();
        setDocumentTitle(editorFile.getName());
        setContentInView(content);
    }

    private void setContentInView(String content) {
        if (Build.VERSION.SDK_INT >= 28) {
            setContentInViewAsync(content);
        } else {
            textEditorView.setText(content);
            registerTextListeners();
        }
    }

    @RequiresApi(28)
    private void setContentInViewAsync(String content) {
        final PrecomputedText.Params params = textEditorView.getTextMetricsParams();
        final Reference<TextEditorView> editorViewRef = new WeakReference<>(textEditorView);

        taskExecutor.submit(() -> {
            final TextEditorView ev = editorViewRef.get();
            if (ev != null) {
                final PrecomputedText preCompText = PrecomputedText.create(content, params);
                ev.post(() -> {
                    final TextEditorView ev2 = editorViewRef.get();
                    if (ev2 != null) {
                        ev2.setText(preCompText);
                        // Set listener after the contents
                        registerTextListeners();
                    }
                });
            }
        });
    }

    private void saveContents(boolean quitWhenSaved) {
        if (dirty || alwaysAllowSave) {
            if (editorFile.isPresent()) {
                writeContents(editorFile.get(), quitWhenSaved);
            } else {
                openFileSaver(quitWhenSaved);
            }
        }
    }

    private void saveNewFile(EditorFile editorFile,
                             boolean quitWhenSaved) {
        this.editorFile = Optional.of(editorFile);
        if (!quitWhenSaved) {
            // We don't need save to be forcefully enabled anymore
            alwaysAllowSave = false;
            editorMenu.ifPresent(x -> x.setSaveAllowed(false));

            editorConfig.setEol(editorFile.getEol());
        }
        setDocumentTitle(editorFile.getName());
        writeContents(editorFile, quitWhenSaved);
    }

    private void writeContents(EditorFile editorFile,
                               boolean quitWhenSaved) {
        final WeakReference<AlertDialog> savingDialogRef = new WeakReference<>(
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(R.string.action_save)
                        .setMessage(getString(R.string.save_in_progress, editorFile.getName()))
                        .show());

        final String contents = textEditorView.getText().toString();
        taskExecutor.runTask(new EditorFileWriterTask(getContentResolver(), editorFile, contents),
                tryResult -> {
                    if (tryResult.isSuccess()) {
                        // Change only the variable, still allow undo
                        dirty = false;
                        final AlertDialog savingDialog = savingDialogRef.get();
                        if (savingDialog != null && savingDialog.isShowing()) {
                            savingDialog.dismiss();
                        }
                        showSavedMessage(quitWhenSaved);
                    } else {
                        showWriteErrorMessage(editorFile);
                    }
                });

    }

    /* UI */

    private void registerTextListeners() {
        textEditorView.post(() -> {
            textEditorView.addTextChangedListener(this);
            textEditorView.addTextChangedListener(editorHistory);
            textEditorView.addTextChangedListener(autoPair);
        });
    }

    private void setDocumentTitle(String title) {
        actionBar.setTitle(title);
        setTaskDescription(new ActivityManager.TaskDescription(title));
    }

    private void setCursorCoordinatesSummary(int cursorStart, int cursorEnd) {
        final int numChars = cursorEnd - cursorStart;
        final String summaryTemplate = numChars == 0
                ? getString(R.string.summary_info)
                : getResources().getQuantityString(R.plurals.summary_selection_info, numChars);
        final String content = textEditorView.getText().toString();
        taskExecutor.runTask(new GetCursorCoordinatesTask(content, cursorStart),
                point -> {
                    final String summary = String.format(summaryTemplate,
                            point.y,
                            point.x,
                            numChars);
                    summaryView.post(() -> summaryView.setText(summary));
                });
    }

    /* Config */

    private void loadConfig() {
        onTextSizeChanged(editorConfig.getTextSize());
        onTextStyleChanged(editorConfig.getTextStyle());
        onAutoPairEnabledChanged(editorConfig.getAutoPairEnabled());
        onShowCommandBarChanged(editorConfig.getShowCommandBar());
    }

    @Override
    public void onTextSizeChanged(@Config.Size int newSize) {
        final int newTextSizeRes;
        switch (newSize) {
            case Config.Size.SMALL:
                newTextSizeRes = R.dimen.font_size_small;
                break;
            case Config.Size.LARGE:
                newTextSizeRes = R.dimen.font_size_large;
                break;
            case Config.Size.MEDIUM:
            default:
                newTextSizeRes = R.dimen.font_size_medium;
                break;
        }
        textEditorView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(newTextSizeRes));
        editorMenu.ifPresent(x -> x.onFontSizeChanged(newSize));
    }

    @Override
    public void onTextStyleChanged(@Config.Style int newStyle) {
        final Typeface newTypeface;
        switch (newStyle) {
            case Config.Style.SANS:
                newTypeface = Typeface.SANS_SERIF;
                break;
            case Config.Style.SERIF:
                newTypeface = Typeface.SERIF;
                break;
            case Config.Style.MONO:
            default:
                newTypeface = Typeface.MONOSPACE;
                break;
        }
        textEditorView.setTypeface(newTypeface);
        editorMenu.ifPresent(x -> x.onFontStyleChanged(newStyle));
    }

    @Override
    public void onAutoPairEnabledChanged(boolean enabled) {
        autoPair.setEnabled(enabled);
        editorMenu.ifPresent(x -> x.onAutoPairChanged(enabled));
    }

    @Override
    public void onShowCommandBarChanged(boolean show) {
        commandBar.setVisibility(show ? View.VISIBLE : View.GONE);
        editorMenu.ifPresent(x -> x.onCommandBarVisibilityChanged(show));
    }

    @Override
    public void onEolChanged(@Config.Eol String newEol) {
        editorMenu.ifPresent(x -> x.onEolChanged(newEol));

        if (editorFile.map(x -> x.getEol().equals(newEol)).orElse(false)) {
            // Nothing to do
            return;
        }

        editorFile = editorFile.map(ef -> new EditorFile(ef.getUri(),
                ef.getName(),
                ef.getSize(),
                newEol));

        // The text in the view didn't change, but if we
        // save the file, the output will be different
        setDirty();
    }

    /* Commands */

    private void runCurrentCommand() {
        editorCommandParser.parse(commandField.getText().toString())
                .ifPresent(x -> {
                    if (!runCommand(x)) {
                        showTmpMessage(R.string.command_unknown);
                    }
                });
    }

    @Override
    public void runFindCommand(EditorCommand.Find command) {
        final String content = textEditorView.getText().toString();
        final int selectionEnd = textEditorView.getSelectionEnd();
        final int cursor = selectionEnd == -1
                ? textEditorView.getSelectionStart()
                : selectionEnd;
        taskExecutor.runTask(new FindCommandTask(command.getToFind(), content, cursor),
                range -> {
                    textEditorView.requestFocus();
                    textEditorView.setSelection(range.getLower(), range.getUpper());
                },
                () -> showTmpMessage(R.string.command_find_none));
    }

    @Override
    public void runDeleteAllCommand(EditorCommand.DeleteAll command) {
        final String content = textEditorView.getText().toString();
        taskExecutor.runTask(new DeleteAllCommandTask(command.getToDelete(), content),
                textEditorView::setText);
    }

    @Override
    public void runDeleteFirstCommand(EditorCommand.DeleteFirst command) {
        final String content = textEditorView.getText().toString();
        final int cursor = textEditorView.getSelectionStart();
        taskExecutor.runTask(new DeleteFirstCommandTask(command.getToDelete(),
                        content, command.getCount(), cursor),
                textEditorView::setText);
    }

    @Override
    public void runSubstituteAllCommand(EditorCommand.SubstituteAll command) {
        final String content = textEditorView.getText().toString();
        taskExecutor.runTask(new SubstituteAllCommandTask(command.getToFind(),
                        command.getReplaceWith(), content),
                textEditorView::setText);
    }

    @Override
    public void runSubstituteFirstCommand(EditorCommand.SubstituteFirst command) {
        final String content = textEditorView.getText().toString();
        final int cursor = textEditorView.getSelectionStart();
        taskExecutor.runTask(new SubstituteFirstCommandTask(command.getToFind(),
                        command.getReplaceWith(), content, command.getCount(), cursor),
                textEditorView::setText);
    }

    /* Dirty */

    private void setNotDirty() {
        if (dirty) {
            dirty = false;
            editorMenu.ifPresent(x -> {
                x.setUndoAllowed(false);
                x.setSaveAllowed(alwaysAllowSave);
            });
        }
    }

    private void setDirty() {
        if (!dirty) {
            dirty = true;
            editorMenu.ifPresent(x -> {
                x.setUndoAllowed(true);
                x.setSaveAllowed(true);
            });
        }
    }

    /* Dialogs */

    private void showSavedMessage(boolean finishOnShown) {
        showTmpMessage(R.string.save_success);
        if (finishOnShown) {
            finish();
        }
    }

    private void showQuitMessage() {
        final String fileName = editorFile.map(x -> '"' + x.getName() + '"')
                .orElseGet(() -> getString(R.string.title_generic));

        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle(fileName)
                .setMessage(getString(R.string.save_quit_ask, fileName))
                .setPositiveButton(R.string.action_save_and_quit,
                        (d, which) -> {
                            d.dismiss();
                            saveContents(true);
                        })
                .setNegativeButton(R.string.action_quit,
                        (d, which) -> {
                            d.dismiss();
                            finish();
                        })
                .setNeutralButton(android.R.string.cancel,
                        (d, which) -> d.dismiss())
                .show();
    }

    private void showOpenErrorMessage() {
        showFatalErrorMessage(getString(R.string.error_open));
    }

    private void showReadErrorMessage(EditorFile editorFile,
                                      Throwable throwable) {
        if (throwable instanceof EditorFileTooLargeException) {
            final EditorFileTooLargeException ftl = (EditorFileTooLargeException) throwable;
            showFatalErrorMessage(getString(R.string.error_read_size,
                    editorFile.getName(),
                    ftl.getFileSize() / ONE_MB,
                    ftl.getMaxSize() / ONE_MB));
        } else {
            showFatalErrorMessage(getString(R.string.error_read_generic, editorFile.getName()));
        }
    }

    private void showWriteErrorMessage(EditorFile editorFile) {
        showFatalErrorMessage(getString(R.string.save_failed, editorFile.getName()));
    }

    private void showFatalErrorMessage(CharSequence message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setOnDismissListener(d -> finish())
                .show();
    }

    private void showTmpMessage(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
