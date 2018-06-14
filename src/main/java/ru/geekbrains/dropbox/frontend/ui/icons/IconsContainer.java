package ru.geekbrains.dropbox.frontend.ui.icons;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Image;
import ru.geekbrains.dropbox.frontend.ui.MainUI;

import java.io.File;

public enum IconsContainer {


    CONFIRM("/image/tick.gif"),
    ERROR("/image/redcross.png"),
    FILE("/image/file_icon.png"),
    FOLDER("/image/folder_icon.png");

    private String path;

    IconsContainer(String path) {
       this.path = path;
    }


    public Image getImage() {
        Image image = new Image("", createResource());
        image.setWidth("20");
        image.setHeight("20");
        return image;
    }


    private Resource createResource() {
        return new FileResource(new File(MainUI.basePath + path));
    }
}
