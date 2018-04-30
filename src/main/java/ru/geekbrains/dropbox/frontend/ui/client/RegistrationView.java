package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class RegistrationView extends VerticalLayout implements View {

    public static final String NAME = "registration";

    //layout
    private HorizontalLayout btnsPanel;

    //TextFields
    private TextField userNameField;
    private PasswordField passwordField;
    private PasswordField passwordFieldRepeat;
    private TextField emailField;

    //Button
    private Button btnRegistration;
    private Button btnReturn;


    RegistrationView () {
        createLoginForm();
        addComponents(
                userNameField,
                passwordField,
                passwordFieldRepeat,
                emailField,
                btnsPanel
                );
    }

    private void createLoginForm() {
        userNameField = new TextField("Имя пользователя");
        passwordField = new PasswordField("Пароль");
        passwordFieldRepeat = new PasswordField("Повторите пароль");
        emailField = new TextField("Email");

        btnsPanel = new HorizontalLayout();
        btnRegistration = new Button("Зарегестрироваться");
        btnReturn = new Button("Вернуться");

        btnsPanel.addComponents(btnRegistration, btnReturn);


    }


}
