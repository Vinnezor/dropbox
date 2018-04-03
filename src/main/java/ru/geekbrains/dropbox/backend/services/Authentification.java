package ru.geekbrains.dropbox.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.backend.dao.Users;

@Service
public class Authentification  implements AuthentificationService{

    @Autowired
    Users users;

    private boolean authorized;

    @Override
    public boolean login(String login, String password) {
        if (authorized) return true;
        if (users.findUser(login, password)) {
            setAuthorized(true);
            return true;
        } else return false;
    }

    @Override
    public void logout() {
         setAuthorized(false);
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
