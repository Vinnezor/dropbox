package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@UIScope
public class LoginView extends VerticalLayout implements View {

    @Autowired
    private AuthenticationManager manager;

    public static final String NAME = "login";
    private static final String cloudImage = "/image/cloud1.png";
    private static String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

    //layout
    private HorizontalLayout loginAndPassTextFields;
    private HorizontalLayout btnsPanel;

    private Alignment center = Alignment.MIDDLE_CENTER;

    //TextFields
    private TextField textLogin;
    private PasswordField textPass;

    //Buttons
    private Button btnAuth;
    private Button btnRegistration;



    public LoginView () {
        FileResource fileResource = new FileResource(new File(basePath + cloudImage));
        Image image = new Image("", fileResource);
        image.setHeight("150");
        image.setWidth("250");
        createAuthPanel();
        addComponents(image, loginAndPassTextFields, btnsPanel);
        setComponentAlignment(loginAndPassTextFields, center);
        setComponentAlignment(btnsPanel, center);
        setComponentAlignment(image, center);

    }


    private void createAuthPanel () {
        loginAndPassTextFields = new HorizontalLayout();

        textLogin = new TextField("Username");
        textLogin.setPlaceholder("Логин");

        textPass = new PasswordField("Password");
        textPass.setPlaceholder("Пароль");
        loginAndPassTextFields.addComponents(textLogin, textPass);

        btnsPanel = new HorizontalLayout();

        btnAuth = new Button("Авторизация");
        createAuthListener();

        btnRegistration = new Button("Регистрация");
        createRegistrationListener();

        btnsPanel.addComponents(btnAuth, btnRegistration);

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

    private void createRegistrationListener() {
        btnRegistration.addClickListener(clickEvent -> getUI().getNavigator().navigateTo(RegistrationView.NAME));
    }
}
