package ru.geekbrains.dropbox.frontend.ui.client;



import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.frontend.service.FileService;


import java.io.*;
import java.util.List;

@Component
@UIScope
public class MainView extends VerticalLayout implements View {



    public static final String NAME = "";
    private HorizontalLayout workPanelLayout;

    private Grid<File> fileList;
    private Upload btnUpload;
    private Button btnDownload;
    private Button btnDelete;
    private Button btnLogout;

    private FileService frontFileService;
    private File focusFile;
    private FileDownloader fileDownloader;

    public MainView(FileService frontFileService) {
        this.frontFileService = frontFileService;
        fileList = new Grid<>();
        workPanelLayout = new HorizontalLayout();
        createWorkPanel();
        createFileList();
        createLogoutHandler();
        addComponents(fileList, workPanelLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        createUploadReciever();
        createBtnDownloadHandler();
        createBtnDeleteHandler();
        fillFileList();
    }

    private void createFileList () {
        fileList.addColumn(File::getName).setCaption("Имя файла");
        fileList.addColumn((file) -> (file.length() / 1024)).setCaption("размер в кбайтах");
        fileList.addItemClickListener(itemClick -> focusFile = itemClick.getItem());
    }

    //заполняем грид данными
    private void fillFileList() {
        fileList.setItems(frontFileService.getFileList());
    }



    private void createWorkPanel () {
        btnUpload = new Upload();
        btnUpload.setButtonCaption("Добавить");

        btnDownload = new Button("Скачать");

        btnDelete = new Button("Удалить");

        btnLogout = new Button("Выйти");

        workPanelLayout.addComponents(btnUpload, btnDownload, btnDelete, btnLogout);
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
            } else {
                createResource(focusFile.getName());
            }
        });
    }

    private void createBtnDeleteHandler () {
        btnDelete.addClickListener(clickEvent -> {
            if(focusFile == null) {
                Notification.show("Не выбран файл");
            } else {
                if (frontFileService.deleteFile(focusFile.getName())) {
                    fillFileList();
                    focusFile = null;
                }
            }

        });
    }

    private void createLogoutHandler() {
        btnLogout.addClickListener((clickEvent -> {
            getUI().getSession().close();
            getUI().getPage().setLocation("/logout");
        }));
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
