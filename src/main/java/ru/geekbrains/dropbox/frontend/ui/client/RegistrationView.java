package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.frontend.ui.MainUI;
import ru.geekbrains.dropbox.modules.authorization.registration.RegistrationField;
import ru.geekbrains.dropbox.modules.authorization.service.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private List<HorizontalLayout> textFieldsHorizontalLayouts;
    private List<TextField> textFieldsList;

    @Autowired
    RegistrationView (UserService userService) {
        this.userService = userService;
        textFieldsHorizontalLayouts = new ArrayList<>();
        textFieldsList = new ArrayList<>();
        Label mainLabel = new Label("Регистрация");
        createLoginForm();
        addComponents(
                mainLabel,
                userNameLayout,
                passwordLayout,
                passwordRepeatLayout,
                emailLayout,
                btnsPanel
                );
        components.forEach(component -> setComponentAlignment(component, Alignment.MIDDLE_CENTER));

    }

    private void createLoginForm() {
        //создание формы все компоненты формы включая кнопки объявляются ниже
        userNameLayout = new HorizontalLayout();
        userNameField = new TextField("Имя пользователя");
        userNameLayout.addComponent(userNameField);
        textFieldsHorizontalLayouts.add(userNameLayout);
        textFieldsList.add(userNameField);

        passwordLayout = new HorizontalLayout();
        passwordField = new PasswordField("Пароль");
        passwordLayout.addComponent(passwordField);
        textFieldsHorizontalLayouts.add(passwordLayout);
        textFieldsList.add(passwordField);

        passwordRepeatLayout = new HorizontalLayout();
        passwordFieldRepeat = new PasswordField("Повторите пароль");
        passwordRepeatLayout.addComponent(passwordFieldRepeat);
        textFieldsHorizontalLayouts.add(passwordRepeatLayout);
        textFieldsList.add(passwordFieldRepeat);

        emailLayout = new HorizontalLayout();
        emailField = new TextField("Email");
        emailLayout.addComponent(emailField);
        textFieldsHorizontalLayouts.add(emailLayout);
        textFieldsList.add(emailField);

        btnsPanel = new HorizontalLayout();
        btnRegistration = new Button("Зарегистрироваться");
        createRegistrationHandler();
        btnReturn = new Button("Вернуться");
        createReturnHandler();
        btnsPanel.addComponents(btnRegistration, btnReturn);
    }

    private void createRegistrationHandler() {
        btnRegistration.addClickListener(clickEvent -> {
            if (!isTextFieldsEmpty() && isComparePasswords()) {
                addImagesOnLayout();
                List<String> errorList = userService.registrationUser(
                                                    userNameField.getValue(),
                                                    passwordField.getValue(),
                                                    emailField.getValue()
                );
                if(errorList.isEmpty()) {
                    clearTextField();
                    getUI().getNavigator().navigateTo(LoginView.NAME);
                }
                errorList.forEach(this::addDescriptionError);
            } else {
                addImagesOnLayout();
            }
        });
    }

    private void createReturnHandler() {
        btnReturn.addClickListener(clickEvent -> {
            clearTextField();
            clearStatesImages();
            getUI().getNavigator().navigateTo(LoginView.NAME);
        });
    }

    private void clearTextField() {
        textFieldsList.forEach(TextField::clear);
    }


    private void addDescriptionError(String errorDescription) {

        if(errorDescription.startsWith(RegistrationField.USER_NAME.getField())) {
            clearStateImage(userNameLayout);
            Notification.show(
                    errorDescription.substring(RegistrationField.USER_NAME.getField().length(), errorDescription.length()),
                    Notification.Type.WARNING_MESSAGE
            ).setDelayMsec(4000);
            userNameLayout.addComponent(addImage(error));
        }
        if (errorDescription.startsWith(RegistrationField.USER_EMAIL.getField())) {
            clearStateImage(emailLayout);
            Notification.show(
                    errorDescription.substring(RegistrationField.USER_EMAIL.getField().length(), errorDescription.length()),
                    Notification.Type.WARNING_MESSAGE
            ).setDelayMsec(4000);
            emailLayout.addComponent(addImage(error));
        }
    }

    private boolean isComparePasswords() {
        return passwordFieldRepeat.getValue().equals(passwordField.getValue());
    }

    private void addImagesOnLayout() {
        clearStatesImages();
        textFieldsList.stream().filter(TextField::isEmpty).forEach(textField -> {
           AbstractOrderedLayout parent = (AbstractOrderedLayout) textField.getParent();
           parent.addComponent(addImage(error));
        });
        textFieldsList.stream().filter(textField -> !textField.isEmpty()).forEach(textField -> {
            AbstractOrderedLayout parent = (AbstractOrderedLayout) textField.getParent();
            parent.addComponent(addImage(confirm));
        });
        if(!isComparePasswords()) {
            clearStateImage(passwordRepeatLayout);
            passwordRepeatLayout.addComponent(addImage(error));
        }

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

    private boolean isTextFieldsEmpty() {
        return textFieldsList.stream().anyMatch(TextField::isEmpty);
    }

    private void clearStatesImages() {
        //картинка всегда второй элемент
        int indexOfImage = 1;
        textFieldsHorizontalLayouts.stream().filter(layout -> layout.getComponentCount() > indexOfImage).
                forEach(layout -> layout.removeComponent(layout.getComponent(indexOfImage)));
    }

    private void clearStateImage(AbstractOrderedLayout layout) {
        int indexOfImage = 1;
        layout.removeComponent(layout.getComponent(indexOfImage));
    }
}
