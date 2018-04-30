package ru.geekbrains.dropbox.frontend.service;

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


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        File file = new File(filePath + "\\" + fileName);
        return new FileOutputStream(file);

    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public File getFileByName(String fileName) {
        return new File(filePath + "\\" + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(filePath + "\\" + fileName));
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
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
    //@PreAuthorize("hasAnyRole('USER')")
    public void clearFileList() {
        if (!fileList.isEmpty()) fileList.clear();
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public boolean deleteFile(String fileName) {
        for (int i = 0; i < fileList.size(); i++) {
            if(fileList.get(i).getName().equals(fileName)) {
               return fileList.get(i).delete();
            }
        }
        throw new RuntimeException("Несуществующий файл запрошен на удаление");
    }
}
