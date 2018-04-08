package ru.geekbrains.dropbox.frontend.service;

public interface AuthentificationService {

    boolean login(String login, String password);
    void logout();
}
