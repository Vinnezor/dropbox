package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class LoginView extends VerticalLayout implements View {

    @Autowired
    private AuthenticationManager manager;

    public static final String NAME = "login";
    private TextField textLogin;
    private PasswordField textPass;
    private Button btnAuth;
    private HorizontalLayout loginAndPassTextFields;
    private FormLayout loginForm;


    public LoginView () {
        createAuthPanel();
        addComponents(loginAndPassTextFields, btnAuth);
        createAuthListener();
    }


    private void createAuthPanel () {

        loginForm = new FormLayout();
        loginAndPassTextFields = new HorizontalLayout();
        textLogin = new TextField("Username");
        loginForm.addComponent(textLogin);
        textLogin.setPlaceholder("Логин");
        textPass = new PasswordField("Password");
        loginForm.addComponent(textPass);
        textPass.setPlaceholder("Пароль");
        btnAuth = new Button("Авторизация");
        loginForm.addComponent(btnAuth);
        loginAndPassTextFields.addComponents(loginForm);

    }

    private void createAuthListener () {
        btnAuth.addClickListener((clickEvent -> {
            try {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(textLogin.getValue(), textPass.getValue());
                SecurityContextHolder.getContext().setAuthentication(manager.authenticate(auth));
                getUI().getNavigator().navigateTo(MainView.NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
