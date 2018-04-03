package ru.geekbrains.dropbox.backend.services;

public interface AuthentificationService {

    boolean login(String login, String password);
    void logout();
}
