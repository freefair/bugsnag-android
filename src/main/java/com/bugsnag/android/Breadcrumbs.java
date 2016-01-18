package com.bugsnag.android;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class Breadcrumbs implements JsonStream.Streamable {
    private static class Breadcrumb {
        private static final int MAX_MESSAGE_LENGTH = 140;
        final String timestamp;
        final String message;

        Breadcrumb(@NonNull String message) {
            this.timestamp = DateUtils.toISO8601(new Date());
            this.message = message.substring(0, Math.min(message.length(), MAX_MESSAGE_LENGTH));
        }
    }

    private static final int DEFAULT_MAX_SIZE = 20;
    private final List<Breadcrumb> store = new LinkedList<Breadcrumb>();
    private int maxSize = DEFAULT_MAX_SIZE;

    public void toStream(@NonNull JsonStream writer) throws IOException {
        writer.beginArray();

        for (Breadcrumb breadcrumb : store) {
            writer.beginArray();
            writer.value(breadcrumb.timestamp);
            writer.value(breadcrumb.message);
            writer.endArray();
        }

        writer.endArray();
    }

    void add(@NonNull String message) {
        if (store.size() >= maxSize) {
            store.remove(0);
        }

        store.add(new Breadcrumb(message));
    }

    void clear() {
        store.clear();
    }

    void setSize(int size) {
        if (size > store.size()) {
            this.maxSize = size;
        } else {
            store.subList(0, store.size() - size).clear();
        }
    }
}
