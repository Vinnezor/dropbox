package ru.geekbrains.dropbox.server.filehandler.service;

import ru.geekbrains.dropbox.server.authorization.dao.User;

import java.io.*;
import java.util.List;

public interface FileService {

    boolean createDir(String path);
    OutputStream getFileOutputStream (String fileName) throws IOException;
    File getFileByName (String fileName);
    InputStream getFileInputStream( String fileName) throws FileNotFoundException;
    List<File> getFileList ();
    void clearFileList();
    boolean deleteFile(String fileName);
    void setUser(User user);
}
