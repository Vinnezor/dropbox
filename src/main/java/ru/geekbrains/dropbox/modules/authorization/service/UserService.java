package ru.geekbrains.dropbox.modules.authorization.service;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.modules.authorization.dao.User;
import ru.geekbrains.dropbox.modules.authorization.dao.UserDao;
import ru.geekbrains.dropbox.modules.authorization.registration.UserRegistration;

import javax.annotation.PostConstruct;


@Service
public class UserService implements UserDetailsService {

    private UserDao userDao;
    private UserRegistration userRegistration;

    @Getter
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserService( UserDao userDao, UserRegistration userRegistration) {
        this.userDao = userDao;
        this.userRegistration = userRegistration;

        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void init() {
        //add test User
//        if(!userDao.findByUserName("user").isPresent()) {
//            List<UserRole> userRoles = new ArrayList<>();
//            userRoles.add(UserRole.USER);
//            userDao.save(
//                    User.builder().
//                            username("user").
//                            password(bCryptPasswordEncoder.encode("pass")).
//                            email("email").
//                            authorities(userRoles).
//                            accountNonExpired(true).
//                            accountNonLocked(true).
//                            credentialsNonExpired(true).
//                            enabled(true).
//                            build());
//        }
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userDao.findByUserName(username).orElse(null);
    }

    public boolean registrationUser(String userName, String userPassword, String repeatPassword, String userEmail) {
        userPassword = bCryptPasswordEncoder.encode(userPassword);
        repeatPassword = bCryptPasswordEncoder.encode(repeatPassword);
        System.out.println(userRegistration.validateUserName(userName) && !userRegistration.validateRegistration(userName)
                && userRegistration.validateEmail((userEmail)));
        System.out.println(userPassword.equals(repeatPassword)
                && userRegistration.validatePassword(userPassword));
        if(userRegistration.validateUserName(userName) && !userRegistration.validateRegistration(userName)
                && userRegistration.validateEmail((userEmail))) {
            if(userPassword.equals(repeatPassword)
                    && userRegistration.validatePassword(userPassword)) {
                userRegistration.setUserName(userName);
                userRegistration.setUserPassword(userPassword);
                userRegistration.setUserEmail(userEmail);
                userRegistration.createNewUser();
                return true;
            } else {
               return false;
            }
        } else {
            return false;
        }
    }

    public void saveUser(User user ) {
        userDao.save(user);
    }

}
