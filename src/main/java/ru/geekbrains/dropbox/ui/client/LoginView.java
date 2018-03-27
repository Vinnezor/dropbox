package ru.geekbrains.dropbox.ui.client;

import com.vaadin.navigator.View;
import com.vaadin.ui.Label;
import ru.geekbrains.dropbox.services.AuthentificationService;

public class LoginView extends MainFormDesign implements View {

    private Label authResult;
    AuthentificationService authentification;

    public LoginView(AuthentificationService authentification) {
        this.authentification = authentification;
        authResult = new Label();
        auth.addComponent(authResult);
        btnAuthAddClickListener();
        bntLogOutAddClickListener();
    }

    private void btnAuthAddClickListener() {
        btnAuth.addClickListener(clickEvent -> {
            if(authentification.login(fieldLogin.getValue(), fieldPassword.getValue())) {
                authResult.setValue("Вы авторизовались");
                fieldLogin.setVisible(false);
                fieldPassword.setVisible(false);
                btnAuth.setVisible(false);
                btnLogOut.setVisible(true);
            } else {
                authResult.setValue("Неправильный логин или пароль, попробуйте еще раз");
            }
            fieldLogin.clear();
            fieldPassword.clear();
        });
    }

    private void bntLogOutAddClickListener() {
        btnLogOut.addClickListener(clickEvent -> {
            authentification.logout();
            fieldLogin.setVisible(true);
            fieldPassword.setVisible(true);
            btnAuth.setVisible(true);
            btnLogOut.setVisible(false);
            authResult.setValue("");
        });
    }
}
