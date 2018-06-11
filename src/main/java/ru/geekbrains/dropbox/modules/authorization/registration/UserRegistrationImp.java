package ru.geekbrains.dropbox.modules.authorization.registration;


import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.modules.authorization.dao.User;
import ru.geekbrains.dropbox.modules.authorization.dao.UserDao;
import ru.geekbrains.dropbox.modules.authorization.dao.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class UserRegistrationImp implements UserRegistration {


    private UserDao userDao;

    private final String descriptionUserName = "Логин должен состоять только из латинских букв и цифр длинной от 6 до 16 символов";
    private final String descriptionUserEmail = "Неверный формат email";
    private final String descriptionUserExists = "Такой пользователь уже существует, введите другое имя пользователя";

    private static final String patternForUserName = "^[a-zA-Z0-9_-]{6,16}$";
    private static final String patternForEmail = "^[^@]+@[^@.]+\\.[^@]+$";

    @Setter
    private String userName;
    @Setter
    private String userPassword;
    @Setter
    private String userEmail;
    private List<UserRole> userRoles;

    @Autowired
    public UserRegistrationImp(UserDao userDao) {
        this.userDao = userDao;

    }

    @Override
    public List<String> createNewUser() {
        User newUser = userBuild();
        List<String> errorList = new ArrayList<>();
        if(!validate(newUser.getUsername(), patternForUserName)) errorList.add(RegistrationField.USER_NAME.getField() + descriptionUserName);
        if(!validate(newUser.getEmail(), patternForEmail)) errorList.add(RegistrationField.USER_EMAIL.getField() + descriptionUserEmail);
        if(errorList.isEmpty()){
            if(!isUserExist(userName)) userDao.save(newUser);
            else errorList.add(RegistrationField.USER_NAME.getField() + descriptionUserExists);
        }
        return errorList;
    }


    private User userBuild () {
        userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);
        return  User.builder().
                username(userName).
                password(userPassword).
                email(userEmail).
                authorities(userRoles).
                accountNonExpired(true).
                accountNonLocked(true).
                credentialsNonExpired(true).
                enabled(true).
                build();
    }

    private boolean validate (String userName, String regex) {
        return Pattern.compile(regex).matcher(userName).matches();
    }

    private boolean isUserExist(String userName) {
        return userDao.findByUserName(userName).isPresent();
    }



}
