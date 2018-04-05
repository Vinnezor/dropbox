package ru.geekbrains.dropbox.backend.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {

    OutputStream getOutputStream(String fileName) throws IOException;
    InputStream getFileInputStream(String fileName) throws FileNotFoundException;
}
