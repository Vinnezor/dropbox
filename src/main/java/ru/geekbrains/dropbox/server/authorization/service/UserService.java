package ru.geekbrains.dropbox.server.authorization.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.server.authorization.dao.User;
import ru.geekbrains.dropbox.server.authorization.dao.UserDao;
import ru.geekbrains.dropbox.server.authorization.dao.UserRole;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    @PostConstruct
    public void init() {

        if(!userDao.findByUserName("user").isPresent()) {
            List<UserRole> userRoles = new ArrayList<>();
            userRoles.add(UserRole.USER);
            userDao.save(
                    User.builder().
                            username("user").
                            password(new BCryptPasswordEncoder().encode("pass")).
                            authorities(userRoles).
                            accountNonExpired(true).
                            accountNonLocked(true).
                            credentialsNonExpired(true).
                            enabled(true).
                            build());
        }
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userDao.findByUserName(username).orElse(null);
    }

}
