package ru.geekbrains.dropbox.modules.authorization.registration;

public interface UserRegistration {

    void createNewUser();
    void setUserEmail(String userEmail);
    void setUserName(String userName );
    void setUserPassword(String userPassword);
    boolean validateUserName(String userName);
    boolean validatePassword(String userPassword);
    boolean validateEmail(String userEmail);
    boolean validateRegistration(String userName);
}
