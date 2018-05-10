package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.modules.authorization.registration.UserRegistration;

@Component
@UIScope
public class RegistrationView extends VerticalLayout implements View {

    @Autowired
    UserRegistration userRegistration;

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

    private Label errorLabel;


    RegistrationView () {
        errorLabel = new Label();
        createLoginForm();
        addComponents(
                errorLabel,
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
        btnRegistration = new Button("Зарегистрироваться");
        createRegistrationHandler();
        btnReturn = new Button("Вернуться");
        createReturnHandler();
        btnsPanel.addComponents(btnRegistration, btnReturn);


    }

    private void createRegistrationHandler() {
        btnRegistration.addClickListener(clickEvent -> {
            String userName = userNameField.getValue();
            String userPassword = passwordField.getValue();
            String userPasswordRepeat = passwordFieldRepeat.getValue();
            String userEmail = emailField.getValue();

            if(userRegistration.validateUserName(userName) && !userRegistration.validateRegistration(userName)
                    && userRegistration.validateEmail((userEmail))) {
                if(userPassword.equals(userPasswordRepeat)
                        && userRegistration.validatePassword(userPassword)) {
                    userRegistration.setUserName(userName);
                    userRegistration.setUserPassword(userPassword);
                    userRegistration.setUserEmail(userEmail);
                    userRegistration.createNewUser();
                    if(userRegistration.validateRegistration(userName)) {
                        getUI().getNavigator().navigateTo(LoginView.NAME);
                    }
                } else {
                    Notification.show("Пароли не совпадают").setDelayMsec(2000);
                }
            } else {
                Notification.show("Неправильное заполнение полей").setDelayMsec(2000);
            }
        });
    }

    private void createReturnHandler() {
        btnReturn.addClickListener(clickEvent -> getUI().getNavigator().navigateTo(LoginView.NAME));
    }


}
