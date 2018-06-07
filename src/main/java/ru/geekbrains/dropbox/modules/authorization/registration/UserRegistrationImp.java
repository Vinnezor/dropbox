package ru.geekbrains.dropbox.modules.authorization.registration;


import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.modules.authorization.dao.User;
import ru.geekbrains.dropbox.modules.authorization.dao.UserRole;
import ru.geekbrains.dropbox.modules.authorization.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRegistrationImp implements UserRegistration {


    private UserService userService;
    @Setter
    private String userName;
    @Setter
    private String userPassword;
    @Setter
    private String userEmail;
    private List<UserRole> userRoles;

    @Autowired
    public UserRegistrationImp(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void createNewUser() {
        userService.saveUser(userBuild());

    }

    private User userBuild () {
        userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);
        return  User.builder().
                username(userName).
                password(userService.getBCryptPasswordEncoder().encode(userPassword)).
                email(userEmail).
                authorities(userRoles).
                accountNonExpired(true).
                accountNonLocked(true).
                credentialsNonExpired(true).
                enabled(true).
                build();
    }

    public boolean validateUserName(String userName) {
        return !userName.equals("");
    }

    public boolean validatePassword(String userPassword) {
        return !userPassword.equals("");
    }

    public boolean validateEmail(String userEmail) {
        return !userEmail.equals("");
    }

    @Override
    public boolean validateRegistration(String userName) {
        return userService.loadUserByUsername(userName) != null;
    }

}
