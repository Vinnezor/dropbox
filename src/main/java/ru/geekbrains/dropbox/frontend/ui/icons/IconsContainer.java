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
    private String width = "20";
    private String height = "20";

    IconsContainer(String path) {
       this.path = path;
    }

    IconsContainer(String path, String width, String height) {
        this(path);
        this.width = width;
        this.height = height;
    }

    public Image getImage() {
        Image image = new Image("", createResource());
        image.setWidth(width);
        image.setHeight(height);
        return image;
    }


    private Resource createResource() {
        return new FileResource(new File(MainUI.basePath + path));
    }
}
