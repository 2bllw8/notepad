/*
 * Copyright (c) 2021 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.notepad.commands;

public class EditorCommand {

    private EditorCommand() {
    }

    public final static class DeleteAll extends EditorCommand {
        private final String toDelete;

        public DeleteAll(String toDelete) {
            this.toDelete = toDelete;
        }

        public String getToDelete() {
            return toDelete;
        }
    }

    public final static class DeleteFirst extends EditorCommand {
        private final int count;
        private final String toDelete;

        public DeleteFirst(int count,
                           String toDelete) {
            this.count = count;
            this.toDelete = toDelete;
        }

        public int getCount() {
            return count;
        }

        public String getToDelete() {
            return toDelete;
        }
    }

    public final static class Find extends EditorCommand {
        private final String toFind;

        public Find(String toFind) {
            this.toFind = toFind;
        }

        public String getToFind() {
            return toFind;
        }
    }

    public final static class Set extends EditorCommand {
        private final String key;
        private final String value;

        public Set(String key,
                   String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public final static class SubstituteAll extends EditorCommand {
        private final String toFind;
        private final String replaceWith;

        public SubstituteAll(String toFind,
                             String replaceWith) {
            this.toFind = toFind;
            this.replaceWith = replaceWith;
        }

        public String getToFind() {
            return toFind;
        }

        public String getReplaceWith() {
            return replaceWith;
        }
    }

    public final static class SubstituteFirst extends EditorCommand {
        private final int count;
        private final String toFind;
        private final String replaceWith;

        public SubstituteFirst(int count,
                               String toFind,
                               String replaceWith) {
            this.count = count;
            this.toFind = toFind;
            this.replaceWith = replaceWith;
        }

        public int getCount() {
            return count;
        }

        public String getToFind() {
            return toFind;
        }

        public String getReplaceWith() {
            return replaceWith;
        }
    }
}
