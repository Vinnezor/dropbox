package ru.geekbrains.dropbox.modules.filehandler.dao;

import java.io.*;
import java.util.List;

public interface FileDaoService {

    boolean createDir(String path);
    boolean dirExists(String path);
    List<File> getFileList();
    OutputStream getFileOutputStream(String filePath) throws IOException;
    InputStream getFileInputStream(String filePath) throws FileNotFoundException;
    boolean deleteFile(String fileName);
    void setPath(String path);
    long getFileSize(File file);
}
