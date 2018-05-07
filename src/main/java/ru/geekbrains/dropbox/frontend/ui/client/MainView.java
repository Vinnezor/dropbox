package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.server.authorization.dao.User;
import ru.geekbrains.dropbox.server.filehandler.service.FileService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
@UIScope
public class MainView extends VerticalLayout implements View {

    @Autowired
    @Qualifier("fileService")
    FileService fileService;

    public static final String NAME = "";
    private HorizontalLayout workPanelLayout;
    private HorizontalLayout searchWorkArea;
    private VerticalLayout searchPanelLayout;

    private Grid<File> fileList;
    private List<TextField> filterList;
    private Upload btnUpload;
    private Button btnDownload;
    private Button btnDelete;
    private Button btnLogout;
    private Button btnAddNewSearchFilter;
    private Button btnFind;

    private File focusFile;
    private FileDownloader fileDownloader;

    private User user;

    public MainView() {
        fileList = new Grid<>();
        filterList = new ArrayList<>();
        workPanelLayout = new HorizontalLayout();
        searchPanelLayout = new VerticalLayout();
        createWorkPanel();
        createFileList();
        createBtnLogoutHandler();
        createSearchPanel();
        addComponents(fileList, workPanelLayout, searchPanelLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setUser();
        createUploadReciever();
        createBtnDownloadHandler();
        createBtnDeleteHandler();
        fillFileList();
    }

    private void setUser () {
        user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user == null) throw new RuntimeException("Пользователь вошел, а юзера нет.");
        fileService.setUser(user);
    }

    private void createFileList () {
        fileList.addColumn(File::getName).setCaption("Имя файла");
        fileList.addColumn((file) -> (file.length() / 1024)).setCaption("размер в кбайтах");
        fileList.addItemClickListener(itemClick -> focusFile = itemClick.getItem());
    }


    private void fillFileList() {
        fileList.setItems(fileService.getFileNameList());
    }



    private void createWorkPanel () {
        btnUpload = new Upload();
        btnUpload.setButtonCaption("Добавить");
        btnDownload = new Button("Скачать");
        btnDelete = new Button("Удалить");
        btnLogout = new Button("Выйти");
        workPanelLayout.addComponents(btnUpload, btnDownload, btnDelete, btnLogout);
    }

    private void createSearchPanel(){
        Label nameOfFilterBlock = new Label("Поиск по файлам");
        searchWorkArea = new HorizontalLayout();
        TextField searchFilterField = new TextField();
        filterList.add(searchFilterField);
        btnAddNewSearchFilter = new Button("+");
        createAddNewFilterHandler();
        btnAddNewSearchFilter.addStyleName("tiny");
        btnFind = new Button("Искать");
        createBtnFindHandler();
        searchWorkArea.addComponents(searchFilterField, btnAddNewSearchFilter);
        searchPanelLayout.addComponents(nameOfFilterBlock ,searchWorkArea, btnFind);
    }

    private void createUploadReciever () {
        btnUpload.setReceiver((String fileName, String mimeType) -> {
            //return OutputStream
            try {
                OutputStream stream = fileService.getFileOutputStream(fileName);
                fillFileList();
                return stream;
            } catch (IOException e) {
                e.printStackTrace();
                Notification.show("Не удалось загрузить файл").setDelayMsec(1000);
            }
            return null;
        });
    }

    private void createBtnDownloadHandler () {
        List<File> files = fileService.getFileNameList();
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
                Notification.show("Не выбран файл").setDelayMsec(1000);
            } else {
                if (fileService.deleteFile(focusFile.getName())) {
                    fillFileList();
                    focusFile = null;
                }
            }
        });
    }

    private void createBtnFindHandler() {
        btnFind.addClickListener((clickEvent) -> {
            fileList.setItems(
                    fileService.getFileNameList().stream().filter(
                        file -> filterList.stream().anyMatch(
                                filter -> file.getName().contains(filter.getValue())
                        )
            ));
       });

    }

    private void createBtnLogoutHandler() {
        btnLogout.addClickListener((clickEvent -> {
            getUI().getSession().close();
            getUI().getPage().setLocation("/logout");
        }));
    }

    private void createAddNewFilterHandler() {
        btnAddNewSearchFilter.addClickListener((clickEvent) -> {
           TextField textField = new TextField();
           HorizontalLayout horizontal = new HorizontalLayout();
           searchPanelLayout.addComponent(horizontal, filterList.size() + searchPanelLayout.getComponentIndex(searchWorkArea));
           Button btnDeleteFilter = new Button("X");
           btnDeleteFilter.addStyleName("tiny");
           horizontal.addComponents(
                   textField,
                   btnDeleteFilter
           );
           createDeleteFilterHandler(btnDeleteFilter, textField);
           filterList.add(textField);

        });
    }

    private void createDeleteFilterHandler(Button btn, TextField tx) {
        btn.addClickListener(clickEvent -> {
            filterList.remove(tx);
            searchPanelLayout.removeComponent(btn.getParent());
        });
    }

    private void createResource(String fileName) {

        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    return fileService.getFileInputStream(fileName);
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
