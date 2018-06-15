package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import com.vaadin.ui.renderers.ImageRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.geekbrains.dropbox.frontend.ui.icons.IconsContainer;
import ru.geekbrains.dropbox.modules.authorization.dao.User;
import ru.geekbrains.dropbox.modules.filehandler.service.FileService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
@UIScope
public class MainView extends GridLayout implements View {

    @Qualifier("fileService")
    private FileService fileService;


    private static final int rows = 5;
    private static final int columns = 3;


    public static final String NAME = "";
    private HorizontalLayout searchWorkArea;
    private VerticalLayout searchPanelLayout;
    private TextField folderOrFileName;

    private Grid<File> fileList;
    private List<TextField> filterList;
    private Upload btnUpload;
    private Button btnDownload;
    private Button btnDelete;
    private Button btnLogout;
    private Button btnAddNewSearchFilter;
    private Button btnFind;
    private Button btnNewFolder;
    private Button btnConfirmFolderOrFileName;

    private File focusFile;
    private FileDownloader fileDownloader;


    private User user;

    @Autowired
    public MainView(FileService fileService) {
        super(columns, rows);

        this.fileService = fileService;
        filterList = new ArrayList<>();
        createBtnColumns(0);
        createFileWidget(1);
        createSearchPanel(2);
        createBtnLogoutHandler();
        //addComponents(workPanelLayout, filePanelLayout, searchPanelLayout);
        setMargin(true);
        setSpacing(true);
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
        fileList.addColumn(this::getPicture, new ImageRenderer<>());
        fileList.addColumn(File::getName).setCaption("Имя файла");
        fileList.addColumn((file) -> (file.length() / 1024)).setCaption("размер в кбайтах");
        fileList.addItemClickListener(itemClick -> focusFile = itemClick.getItem());
    }

    private Resource getPicture(File file) {
        if(file.isFile()) return IconsContainer.FILE.getThemeResource();
        else return IconsContainer.FOLDER.getThemeResource();
    }


    private void fillFileList() {
        fileList.setItems(fileService.getFileList());
    }

    private void createFileWidget(int columns) {
        btnNewFolder = new Button("Новая Папка");
        fileList = new Grid<>();

        HorizontalLayout nameOfFolderPanel = new HorizontalLayout();
        folderOrFileName = new TextField();
        folderOrFileName.setPlaceholder("Введите название");
        btnConfirmFolderOrFileName = new Button("Подтвердить");
        btnConfirmFolderOrFileName.setStyleName("tiny");
        nameOfFolderPanel.addComponents(folderOrFileName, btnConfirmFolderOrFileName);
        nameOfFolderPanel.setVisible(false);
        createFileList();
        createAddNewFolderHandler();
        addComponent(btnNewFolder, columns, 0);
        addComponent(nameOfFolderPanel, columns, 1);
        addComponent(fileList, columns, 2, columns, rows - 1);
    }

    private void createBtnColumns(int columns) {
        VerticalLayout btnColumnsLayout = new VerticalLayout();
        btnUpload = new Upload();
        btnUpload.setButtonCaption("Upload");
        btnDownload = new Button("Download");
        btnDelete = new Button("Delete");
        btnLogout = new Button("Logout");
        btnColumnsLayout.addComponents(btnUpload, btnDownload, btnDelete, btnLogout);
        addComponent(btnColumnsLayout, columns, 1, columns, rows - 1 );

    }

    private void createSearchPanel(int columns){
        Label nameOfFilterBlock = new Label("Поиск по файлам");
        searchWorkArea = new HorizontalLayout();
        searchPanelLayout = new VerticalLayout();
        TextField searchFilterField = new TextField();
        filterList.add(searchFilterField);
        btnAddNewSearchFilter = new Button("+");
        createAddNewFilterHandler();
        btnAddNewSearchFilter.addStyleName("tiny");
        btnFind = new Button("Искать");
        createBtnFindHandler();
        searchWorkArea.addComponents(searchFilterField, btnAddNewSearchFilter);
        searchPanelLayout.addComponents(searchWorkArea, nameOfFilterBlock , searchWorkArea, btnFind);
        addComponent(searchPanelLayout, columns, 1, columns, rows - 1);
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
        List<File> files = fileService.getFileList();
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

    private void createAddNewFolderHandler() {
        btnNewFolder.addClickListener(clickEvent -> {
            folderOrFileName.getParent().setVisible(true);
            folderOrFileName.clear();
            folderOrFileName.setValue("New Folder");
            createConfirmFolderOrFileNameHandler();
        });
    }

    private void createConfirmFolderOrFileNameHandler() {
        btnConfirmFolderOrFileName.addClickListener(clickEvent -> {
            fileService.addDir(folderOrFileName.getValue());
            fillFileList();
            folderOrFileName.clear();
            folderOrFileName.getParent().setVisible(false);
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
                    fileService.getFileList().stream().filter(
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
