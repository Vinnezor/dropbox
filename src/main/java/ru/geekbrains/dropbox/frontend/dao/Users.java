package ru.geekbrains.dropbox.frontend.dao;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class Users {

    private final Map<String, String> users = new HashMap<>();


    Users () {
        fillWithData();
    }

    private void fillWithData() {
        for (int i = 0; i < 10; i++) {
            users.put("user" + i, "pass" + i);
        }
    }

    public boolean findUser(String login, String password) {
        if(users.containsKey(login)) {
            String psw = users.get(login);
            return psw.equals(password);
        } else {
            return false;
        }

    }
}
