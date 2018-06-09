package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.frontend.ui.MainUI;
import ru.geekbrains.dropbox.modules.authorization.service.UserService;

import javax.validation.constraints.Null;
import java.io.File;

@Component
@UIScope
public class RegistrationView extends VerticalLayout implements View {



    public static final String NAME = "registration";
    private final String greenTick = "/image/tick.gif";
    private final String redCross = "/image/redcross.png";
    private final String wightImage = "20";
    private final String heightImage = "20";

    private final String error = "error";
    private final String confirm = "confirm";

    private UserService userService;

    //layout
    private HorizontalLayout btnsPanel;
    private HorizontalLayout userNameLayout;
    private HorizontalLayout passwordLayout;
    private HorizontalLayout passwordRepeatLayout;
    private HorizontalLayout emailLayout;

    //TextFields
    private TextField userNameField;
    private PasswordField passwordField;
    private PasswordField passwordFieldRepeat;
    private TextField emailField;

    //Button
    private Button btnRegistration;
    private Button btnReturn;

    private Label errorLabel;

    @Autowired
    RegistrationView (UserService userService) {
        this.userService = userService;
        createLoginForm();
        addComponents(
                userNameLayout,
                passwordLayout,
                passwordRepeatLayout,
                emailLayout,
                btnsPanel
                );
        components.forEach(component -> setComponentAlignment(component, Alignment.MIDDLE_CENTER));

    }

    private void createLoginForm() {
        userNameLayout = new HorizontalLayout();
        userNameField = new TextField("Имя пользователя");
        userNameLayout.addComponent(userNameField);

        passwordLayout = new HorizontalLayout();
        passwordField = new PasswordField("Пароль");
        passwordLayout.addComponent(passwordField);

        passwordRepeatLayout = new HorizontalLayout();
        passwordFieldRepeat = new PasswordField("Повторите пароль");
        passwordRepeatLayout.addComponent(passwordFieldRepeat);

        emailLayout = new HorizontalLayout();
        emailField = new TextField("Email");
        emailLayout.addComponent(emailField);

        btnsPanel = new HorizontalLayout();
        btnRegistration = new Button("Зарегистрироваться");
        createRegistrationHandler();
        btnReturn = new Button("Вернуться");
        createReturnHandler();
        btnsPanel.addComponents(btnRegistration, btnReturn);

    }

    private void createRegistrationHandler() {
        btnRegistration.addClickListener(clickEvent -> {
            clearStatesImages();
            if (checkFieldForEmpty()) {
                if(userService.registrationUser(
                        userNameField.getValue(),
                        passwordField.getValue(),
                        passwordFieldRepeat.getValue(),
                        emailField.getValue())) {
                    clearTextField();
                    getUI().getNavigator().navigateTo(LoginView.NAME);
                }
            }

        });
    }

    private void createReturnHandler() {
        btnReturn.addClickListener(clickEvent -> {
            clearTextField();
            getUI().getNavigator().navigateTo(LoginView.NAME);
        });
    }

    private void clearTextField() {
        userNameField.clear();
        passwordField.clear();
        passwordFieldRepeat.clear();
        emailField.clear();
    }


    private Image addImage(String state) {
        String imagePath = null;
        if(state.equals(confirm)) imagePath = greenTick;
        else if (state.equals(error)) imagePath = redCross;
        FileResource fileResource = new FileResource(new File(MainUI.basePath + imagePath));
        Image image = new Image("", fileResource);
        image.setWidth(wightImage);
        image.setHeight(heightImage);
        return image;
    }

    private boolean checkFieldForEmpty() {
        boolean check = true;
        if(userNameField.isEmpty()){
            userNameLayout.addComponent(addImage(error));
            check = false;
        }
        else userNameLayout.addComponent(addImage(confirm));
        if(passwordField.isEmpty()) {
            passwordLayout.addComponent(addImage(error));
            check = false;
        } else passwordLayout.addComponent(addImage(confirm));
        if(passwordFieldRepeat.isEmpty() || !passwordFieldRepeat.getValue().equals(passwordField.getValue())) {
            passwordRepeatLayout.addComponent(addImage(error));
            check = false;
        } else passwordRepeatLayout.addComponent(addImage(confirm));
        if(emailField.isEmpty()){
            emailLayout.addComponent(addImage(error));
            check = false;
        }
        else emailLayout.addComponent(addImage(confirm));
        return check;
    }

    private void clearStatesImages() {
        int indexOfImage = 1;

        if(userNameLayout.getComponentCount() > indexOfImage) {
            userNameLayout.removeComponent(userNameLayout.getComponent(indexOfImage));
        }
        if(passwordLayout.getComponentCount() > indexOfImage) {
            passwordLayout.removeComponent(passwordLayout.getComponent(indexOfImage));
        }
        if(passwordRepeatLayout.getComponentCount() > indexOfImage) {
            passwordRepeatLayout.removeComponent(passwordRepeatLayout.getComponent(indexOfImage));
        }
        if(emailLayout.getComponentCount() > indexOfImage) {
            emailLayout.removeComponent(emailLayout.getComponent(indexOfImage));
        }
    }


}
