package ru.geekbrains.dropbox.services;

public interface AuthentificationService {

    boolean login(String login, String password);
    void logout();
}
