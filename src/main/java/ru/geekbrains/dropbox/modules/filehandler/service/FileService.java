package ru.geekbrains.dropbox.modules.filehandler.service;

import ru.geekbrains.dropbox.modules.authorization.dao.User;

import java.io.*;
import java.util.List;

public interface FileService {


    OutputStream getFileOutputStream (String fileName) throws IOException;
    InputStream getFileInputStream( String fileName) throws FileNotFoundException;
    List<File> getFileList ();
    File getFileByName(String fileName);
    boolean deleteFile(String fileName);
    void setUser(User user);
    void addDir(String dirname);
    void getDir(String dirname);
}
