package com.example.homeexpenses;

public enum Person {
    Gunnthor("Gunnthor"),
    Iris("Iris");

    private final String displayName;

    Person(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
