package com.topcoder.client.contestMonitor.view.gui.menu;

final class Entry {

    private final String description;
    private final ViewField viewField;

    Entry(String description, ViewField viewField) {
        this.description = description;
        this.viewField = viewField;
    }

    String getDescription() {
        return description;
    }

    ViewField getViewField() {
        return viewField;
    }

}
