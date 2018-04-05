package ru.geekbrains.dropbox.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.backend.dao.FileDaoService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service("backFilesService")
public class FileServiceImpl implements FileService {

    @Autowired
    FileDaoService dao;

    @Override
    public OutputStream getOutputStream(String fileName) throws IOException {
        return dao.getFileOutputStream(fileName);
    }

    @Override
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return dao.getFileInputStream(fileName);
    }
}
