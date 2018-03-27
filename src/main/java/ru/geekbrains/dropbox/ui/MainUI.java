package ru.geekbrains.dropbox.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import ru.geekbrains.dropbox.services.AuthentificationService;
import ru.geekbrains.dropbox.ui.client.LoginView;
import ru.geekbrains.dropbox.ui.client.MainFormDesign;


@SpringUI
public class MainUI extends UI {

    @Autowired
    AuthentificationService authentification;

    @Override
    protected void init(VaadinRequest request) {
        setContent(new LoginView(authentification));
    }
}
