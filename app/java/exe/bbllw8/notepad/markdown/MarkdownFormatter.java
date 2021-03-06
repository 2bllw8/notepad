/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.markdown;

import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import androidx.annotation.ColorInt;

public final class MarkdownFormatter {
    @ColorInt
    private final int codeColor;
    @ColorInt
    private final int quoteColor;

    public MarkdownFormatter(@ColorInt int codeColor,
                             @ColorInt int quoteColor) {
        this.codeColor = codeColor;
        this.quoteColor = quoteColor;
    }

    public CharSequence format(CharSequence text) {
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        final int n = text.length();
        int i = 0;
        boolean wasLastLine = true;

        while (i < n) {
            final char c = text.charAt(i++);
            if (wasLastLine) {
                final boolean softNewLine = sb.length() > 0
                        && sb.charAt(sb.length() - 1) == ' ';
                switch (c) {
                    case '#': {
                        if (softNewLine) {
                            sb.append('\n');
                        }

                        final int j = nextEol(text, n, i);
                        if (peek(text, n, i) == '#') {
                            if (peek(text, n, i + 1) == '#') {
                                if (i + 2 < n) {
                                    final CharSequence h3Text = text.subSequence(i + 2, j)
                                            .toString()
                                            .trim();
                                    sb.append(h3Text,
                                            new RelativeSizeSpan(1.25f),
                                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                }
                            } else {
                                if (i + 1 < n) {
                                    final CharSequence h2Text = text.subSequence(i + 1, j)
                                            .toString()
                                            .trim();
                                    sb.append(h2Text,
                                            new RelativeSizeSpan(1.5f),
                                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                }
                            }
                        } else {
                            if (i < n) {
                                final CharSequence h1Text = text.subSequence(i, j)
                                        .toString()
                                        .trim();
                                sb.append(h1Text,
                                        new RelativeSizeSpan(1.75f),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                        }
                        sb.append('\n');
                        i = j;
                        continue;
                    }
                    case '>': {
                        if (softNewLine) {
                            sb.append('\n');
                        }

                        final int j = nextEol(text, n, i);
                        final CharSequence quotedText = text.subSequence(i, j)
                                .toString()
                                .trim();
                        final int start = sb.length();
                        sb.append(quotedText);
                        final int end = sb.length();
                        if (Build.VERSION.SDK_INT >= 28) {
                            sb.setSpan(new TypefaceSpan(Typeface.SERIF),
                                    start,
                                    end,
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        sb.setSpan(new ForegroundColorSpan(quoteColor),
                                start,
                                end,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        sb.append('\n');
                        i = j;
                        continue;
                    }
                    case '-': {
                        if (softNewLine) {
                            sb.append('\n');
                        }
                        sb.append(" ",
                                new BulletSpan(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        wasLastLine = false;
                        continue;
                    }
                    case ' ': {
                        final int listBegin = nextMatch(text, '-', n, i);
                        if (listBegin < n) {
                            if (softNewLine) {
                                sb.append('\n');
                            }
                            for (int space = 0; space < (listBegin - i); space += 2) {
                                sb.append(' ');
                            }
                            sb.append(" ",
                                    new BulletSpan(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            i = listBegin - 1;
                            wasLastLine = false;
                            continue;
                        }
                    }
                }
            }

            if (c == '\n') {
                wasLastLine = true;
                if (peek(text, n, i) == '\n') {
                    i++;
                    sb.append('\n');
                } else {
                    sb.append(' ');
                }
            } else {
                wasLastLine = false;
                switch (c) {
                    case '*':
                    case '_': {
                        if (peek(text, n, i) == c) {
                            final int j = nextMatch(text, c, n, i + 1);
                            if (peek(text, n, j) == c) {
                                if (i + 1 < n) {
                                    sb.append(text.subSequence(i + 1, j - 1),
                                            new StyleSpan(Typeface.BOLD),
                                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                }
                                i = j + 1;
                            } else {
                                sb.append(c);
                            }
                        } else {
                            final int j = nextMatch(text, c, n, i);
                            if (i < n) {
                                sb.append(text.subSequence(i, j - 1),
                                        new StyleSpan(Typeface.ITALIC),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            i = j;
                        }
                    }
                    break;
                    case '`': {
                        final int j = nextMatch(text, '`', n, i);
                        final CharSequence codeText = text.subSequence(i, j - 1);
                        final int start = sb.length();
                        sb.append(codeText);
                        final int end = sb.length();
                        if (Build.VERSION.SDK_INT >= 28) {
                            sb.setSpan(new TypefaceSpan(Typeface.MONOSPACE),
                                    start,
                                    end,
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        sb.setSpan(new BackgroundColorSpan(codeColor),
                                start,
                                end,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        i = j;
                    }
                    break;
                    case '~': {
                        final int j = nextMatch(text, '~', n, i);
                        sb.append(text.subSequence(i, j - 1),
                                new StrikethroughSpan(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        i = j;
                    }
                    break;
                    default: {
                        sb.append(c);
                    }
                }
            }
        }
        return sb;
    }

    private static char peek(CharSequence s, int max, int index) {
        return index < max && index >= 0 ? s.charAt(index) : 0;
    }

    private static int nextEol(CharSequence s, int max, int from) {
        return nextMatch(s, '\n', max, from);
    }

    private static int nextMatch(CharSequence s,
                                 char c,
                                 int max,
                                 int from) {
        int i = from;
        while (i < max) {
            if (s.charAt(i++) == c) {
                return i;
            }
        }
        return i;
    }
}
