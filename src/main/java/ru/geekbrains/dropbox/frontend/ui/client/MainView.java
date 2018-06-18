package ru.geekbrains.dropbox.frontend.ui.client;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import com.vaadin.ui.components.grid.HeaderCell;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@UIScope
public class MainView extends HorizontalLayout implements View {

    @Qualifier("fileService")
    private FileService fileService;

    public static final String NAME = "";
    private HorizontalLayout searchWorkArea;
    private VerticalLayout searchPanelLayout;
    private VerticalLayout filePanelLayout;
    private VerticalLayout buttonPanelLayout;
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
    private Button btnUpDir;

    private File focusFile;
    private FileDownloader fileDownloader;

    private HeaderCell pathToDir;

    private User user;


    @Autowired
    public MainView(FileService fileService) {

        this.fileService = fileService;
        filterList = new ArrayList<>();
        createBtnColumns();
        createFileWidget();
        createSearchPanel();
        setMargin(true);
        setSpacing(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setUser();
        createUploadReciever();
        createBtnDownloadHandler();
        createBtnDeleteHandler();
        createBtnLogoutHandler();
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
        fileList.addColumn((file) -> fileService.getFilesSize(file)).setCaption("KiB");
        fileList.addItemClickListener(this::gridItemClickListenerHandler);
        fileList.addHeaderRowAt(0);
        pathToDir = mergeGridRows(0);
    }

    private void gridItemClickListenerHandler(Grid.ItemClick<File> itemClick) {
        int click = 1;
        int dbclick = 2;
        File file = itemClick.getItem();
        focusFile = file;
        if(itemClick.getMouseEventDetails().getType() == dbclick && file.isDirectory()){
            fileService.getDir(file.getName());
            fillFileList();
        }
    }

    private Resource getPicture(File file) {
        if(file.isFile()) return IconsContainer.FILE.getThemeResource();
        else if(file.isDirectory()) return IconsContainer.FOLDER.getThemeResource();
        else throw new RuntimeException("вместо картинки пришел не файл и не директория");
    }


    private void fillFileList() {

        fileList.setItems(fileService.getFileList());
        pathToDir.setText(fileService.getCurrentPath());
    }

    private void createFileWidget() {
        HorizontalLayout buttonUpPanelLayout = new HorizontalLayout();
        HorizontalLayout nameOfFolderPanel = new HorizontalLayout();

        btnNewFolder = new Button("Новая Папка");
        btnUpDir = new Button("Уровень вверх");
        btnConfirmFolderOrFileName = new Button("Подтвердить");
        btnConfirmFolderOrFileName.setStyleName("tiny");

        filePanelLayout = new VerticalLayout();
        filePanelLayout.setWidth("900");

        fileList = new Grid<>();
        fileList.setSizeFull();

        folderOrFileName = new TextField();
        folderOrFileName.setPlaceholder("Введите название");

        buttonUpPanelLayout.addComponents(btnNewFolder, btnUpDir);
        nameOfFolderPanel.addComponents(folderOrFileName, btnConfirmFolderOrFileName);
        nameOfFolderPanel.setVisible(false);

        createFileList();
        createAddNewFolderHandler();
        createUpDirHandler();

        filePanelLayout.addComponents(buttonUpPanelLayout, nameOfFolderPanel, fileList);
        addComponent(filePanelLayout);
    }

    private void createUpDirHandler() {
        btnUpDir.addClickListener(clickEvent -> {
            fileService.upDir();
            fillFileList();
        });
    }

    private HeaderCell mergeGridRows(int indexRow) {
        HashSet<HeaderCell> hashSet = fileList.getColumns().stream().
                map(column -> fileList.getHeaderRow(indexRow).getCell(column)).
                distinct().
                collect(Collectors.toCollection(HashSet::new));
        return fileList.getHeaderRow(indexRow).join(hashSet);
    }

    private void createBtnColumns() {
        VerticalLayout btnColumnsLayout = new VerticalLayout();
        btnUpload = new Upload();
        btnUpload.setButtonCaption("Upload");
        btnDownload = new Button("Download");
        btnDelete = new Button("Delete");
        btnLogout = new Button("Logout");
        btnColumnsLayout.addComponents(btnUpload, btnDownload, btnDelete, btnLogout);
        addComponent(btnColumnsLayout);

    }

    private void createSearchPanel(){
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
        addComponent(searchPanelLayout);
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
