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
import ru.geekbrains.dropbox.frontend.ui.MainUI;

import java.io.File;

@Component
@UIScope
public class LoginView extends VerticalLayout implements View {


    private AuthenticationManager manager;

    public static final String NAME = "login";
    private static final String cloudImage = "/image/cloud1.png";
    private static final String failedAuthMessage = "Неверный логин или пароль";

    //layout
    private HorizontalLayout loginAndPassTextFields;
    private HorizontalLayout btnsPanel;

    private Alignment alignment = Alignment.MIDDLE_CENTER;

    //TextFields
    private TextField textLogin;
    private PasswordField textPass;

    //Buttons
    private Button btnAuth;
    private Button btnRegistration;


    @Autowired
    public LoginView (AuthenticationManager manager) {
        this.manager = manager;
        FileResource fileResource = new FileResource(new File(MainUI.basePath + cloudImage));
        Image image = new Image("", fileResource);
        image.setHeight("150");
        image.setWidth("250");
        createAuthPanel();
        addComponents(image, loginAndPassTextFields, btnsPanel);
        components.forEach(component -> setComponentAlignment(component, alignment));
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
                clearTextFields();
                SecurityContextHolder.getContext().setAuthentication(manager.authenticate(auth));
                getUI().getNavigator().navigateTo(MainView.NAME);
            } catch (Exception e) {
                Notification.show(failedAuthMessage).setDelayMsec(3000);
                e.printStackTrace();
            }

        }));
    }

    private void clearTextFields() {
        textLogin.clear();
        textPass.clear();
    }

    private void createRegistrationListener() {
        btnRegistration.addClickListener(clickEvent -> getUI().getNavigator().navigateTo(RegistrationView.NAME));
    }
}
