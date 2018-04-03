package ru.geekbrains.dropbox.frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service("frontFileService")
public class FileServiceImpl implements FileService {

    @Value("${filePath}")
    private String filePath;
    private List<File> fileList =  new ArrayList<>();

    @Autowired
    ru.geekbrains.dropbox.backend.services.FileService backFileService;

    @Override
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        return backFileService.getOutputStream(fileName);
    }

    @Override
    public File getFileByName(String fileName) {
        return new File(filePath + "\\" + fileName);
    }

    @Override
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(filePath + "\\" + fileName));
    }

    @Override
    public List<File> getFileList() {
        clearFileList();
        File[] files;
        File dir = new File(filePath);
        files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) fileList.add(files[i]);
        }
        return fileList;
    }

    @Override
    public void clearFileList() {
        if (!fileList.isEmpty()) fileList.clear();
    }

    @Override
    public boolean deleteFile(String fileName) {
        for (int i = 0; i < fileList.size(); i++) {
            if(fileList.get(i).getName().equals(fileName)) {
               return fileList.get(i).delete();
            }
        }
        throw new RuntimeException("Несуществующий файл запрошен на удаление");
    }
}
