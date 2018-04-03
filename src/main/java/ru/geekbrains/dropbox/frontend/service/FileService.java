package ru.geekbrains.dropbox.frontend.service;

import java.io.*;
import java.util.List;

public interface FileService {

    OutputStream getFileOutputStream (String fileName) throws IOException;
    File getFileByName (String fileName);
    InputStream getFileInputStream( String fileName) throws FileNotFoundException;
    List<File> getFileList ();
    void clearFileList();
    boolean deleteFile(String fileName);
}
