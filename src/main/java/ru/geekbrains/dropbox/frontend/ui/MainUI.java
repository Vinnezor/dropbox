package ru.geekbrains.dropbox.frontend.ui;

import com.vaadin.navigator.Navigator;

import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.geekbrains.dropbox.frontend.ui.client.LoginView;
import ru.geekbrains.dropbox.frontend.ui.client.MainView;
import ru.geekbrains.dropbox.frontend.ui.client.RegistrationView;


@SpringUI
@PushStateNavigation
public class MainUI extends UI {


    private MainView mainView;
    private LoginView loginView;
    private RegistrationView registrationView;
    private Navigator navigator;

    @Autowired
    public MainUI(MainView mainView, LoginView loginView, RegistrationView registrationView) {
        this.mainView = mainView;
        this.loginView = loginView;
        this.registrationView = registrationView;
    }

    @Override
    protected void init(VaadinRequest request) {

        navigator = new Navigator(this, this);
        navigator.addView(MainView.NAME, mainView);
        navigator.addView(LoginView.NAME, loginView);
        navigator.addView(RegistrationView.NAME, registrationView);


    }

}
