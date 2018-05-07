package ru.geekbrains.dropbox.server.filehandler.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;


@Repository
public class FileDaoImpl implements FileDaoService {

    @Value("${filesPath}")
    private String filePath;


    @Override
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        File file = new File(filePath + "\\" + fileName);
        return new FileOutputStream(file);
    }

    @Override
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        File file = new File(filePath + "\\" + fileName);
        return new FileInputStream(file);
    }
}
