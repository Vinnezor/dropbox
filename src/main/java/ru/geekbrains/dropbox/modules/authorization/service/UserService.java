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

import javax.annotation.PostConstruct;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    @Getter
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();

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

    public void saveUser(User user ) {
        userDao.save(user);
    }

}
