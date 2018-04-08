package ru.geekbrains.dropbox.frontend.ui;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.geekbrains.dropbox.frontend.service.AuthentificationService;
import ru.geekbrains.dropbox.frontend.service.FileService;

import java.io.*;
import java.util.List;


@SpringUI
public class MainUI extends UI {

    @Autowired
    AuthentificationService authentification;

    @Autowired
    @Qualifier("frontFileService")
    FileService frontFileService;


    private Panel authPanel;
    private Panel workPanel;
    private Button btnAuth;
    private Grid<File> fileList;
    private Upload btnUpload;
    private Button btnDownload;
    private Button btnDelete;
    private TextField textLogin;
    private TextField textPass;
    private File focusFile;
    private FileDownloader fileDownloader;



    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout mainLayout = new VerticalLayout();

        fileList = new Grid<>();
        createFileList();

        authPanel = new Panel();
        createAuthPanel();

        workPanel = new Panel();
        createWorkPanel();

        mainLayout.addComponents(fileList, authPanel, workPanel);
        setContent(mainLayout);
    }

    private void createFileList () {
        fileList.addColumn(File::getName).setCaption("Имя файла");
        fileList.addColumn((file) -> (file.length() / 1024 / 1024)).setCaption("размер в кбайтах");
        fillFileList();
        fileList.addItemClickListener(itemClick -> focusFile = itemClick.getItem());
    }

    //заполняем грид данными
    private void fillFileList() {
        fileList.setItems(frontFileService.getFileList());
    }

    private void createAuthPanel () {
        VerticalLayout authMainLayout = new VerticalLayout();
        HorizontalLayout loginAndPassTextFields = new HorizontalLayout();
        textLogin = new TextField();
        textLogin.setPlaceholder("Логин");
        textPass = new TextField();
        textPass.setPlaceholder("Пароль");
        btnAuth = new Button("Авторизация");
        createAuthListener();
        loginAndPassTextFields.addComponents(textLogin, textPass);
        authMainLayout.addComponents(loginAndPassTextFields, btnAuth);
        authPanel.setContent(authMainLayout);
    }

    private void createWorkPanel () {
        HorizontalLayout workPanelLayout = new HorizontalLayout();
        btnUpload = new Upload();
        btnUpload.setButtonCaption("Добавить");
        createUploadReciever();
        btnDownload = new Button("Скачать");
        createBtnDownloadHandler();
        btnDelete = new Button("Удалить");
        createBtnDeleteHandler();
        workPanelLayout.addComponents(btnUpload, btnDownload, btnDelete);
        workPanel.setContent(workPanelLayout);
    }


    private void createAuthListener () {
        btnAuth.addClickListener(clickEvent -> {
            if(authentification.login(textLogin.getValue(), textPass.getValue())){
                authPanel.setVisible(false);
            }
        });
    }

    private void createUploadReciever () {
        btnUpload.setReceiver((String fileName, String mimeType) -> {
            //return OutputStream
                try {
                    OutputStream stream = frontFileService.getFileOutputStream(fileName);
                    fillFileList();
                    return stream;
                } catch (IOException e) {
                    e.printStackTrace();
                    Notification.show("Не удалось загрузить файл");
                }
                return null;
        });
    }

    private void createBtnDownloadHandler () {
        List<File> files = frontFileService.getFileList();
        for (int i = 0; i < files.size(); i++) {
            createResource(files.get(i).getName());
        }
        btnDownload.addClickListener(clickEvent -> {
            if(focusFile == null){
                Notification.show("Не выбран файл");

            } else createResource(focusFile.getName());
        });
    }

    private void createBtnDeleteHandler () {
        btnDelete.addClickListener(clickEvent -> {
           if(focusFile == null) {
               Notification.show("Не выбран файл");
           } else {
               if (frontFileService.deleteFile(focusFile.getName())) {
                    fillFileList();
               }
           }

        });
    }


    private void createResource(String fileName) {

        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    return frontFileService.getFileInputStream(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, fileName);
        if (fileDownloader == null){
            fileDownloader = new FileDownloader(resource);
            fileDownloader.extend(btnDownload);
        } else {
            fileDownloader.setFileDownloadResource(resource);
        }

    }
}
