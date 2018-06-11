package ru.geekbrains.dropbox.modules.authorization.registration;

import java.util.List;

public interface UserRegistration {

    List<String> createNewUser();
    void setUserEmail(String userEmail);
    void setUserName(String userName );
    void setUserPassword(String userPassword);
}
