package ru.geekbrains.dropbox.modules.authorization.registration;

public enum RegistrationField {
    USER_NAME("username"), USER_PASSWORD("password"), USER_EMAIL("email");

    private final String field;

    RegistrationField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

}
