package ru.geekbrains.dropbox.backend.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileDaoService {

    OutputStream getFileOutputStream(String fileName) throws IOException;
    InputStream getFileInputStream(String fileName) throws FileNotFoundException;
}
