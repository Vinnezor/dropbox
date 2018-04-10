package ru.geekbrains.dropbox.frontend.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;

import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import ru.geekbrains.dropbox.frontend.service.FileService;
import ru.geekbrains.dropbox.frontend.ui.client.LoginView;
import ru.geekbrains.dropbox.frontend.ui.client.MainView;



@SpringUI
@PushStateNavigation
public class MainUI extends UI {

    @Autowired
    @Qualifier("frontFileService")
    FileService frontFileService;

    @Autowired
    AuthenticationManager manager;

    private Navigator navigator;

    @Override
    protected void init(VaadinRequest request) {

        navigator = new Navigator(this, this);
        navigator.addView(MainView.NAME, new MainView(frontFileService));
        navigator.addView(LoginView.NAME, new LoginView(manager));

    }

}
