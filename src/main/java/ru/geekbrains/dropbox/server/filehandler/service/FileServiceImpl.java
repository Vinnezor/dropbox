package ru.geekbrains.dropbox.server.filehandler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.server.authorization.dao.User;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service("fileService")
public class FileServiceImpl implements FileService {

    @Value("${filesPath}")
    private String filesPath;
    private User user;
    private String userName;
    private List<File> fileList =  new ArrayList<>();


    @PostConstruct
    public void init() {
        createDir(filesPath);
    }

    @Override
    public boolean createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return new File(path).mkdirs();
        }
        return false;
    }

    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        File file = new File(filesPath + "\\" + userName + "\\" + fileName);
        return new FileOutputStream(file);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public File getFileByName(String fileName) {
        return new File(filesPath + "\\" + userName + "\\" + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(filesPath + "\\" + userName + "\\" + fileName));
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public List<File> getFileList() {
        clearFileList();
        File[] files;
        File dir = new File(filesPath + "\\" + userName );
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

    @Override
    public void setUser(User user) {
         this.user = user;
         userName = user.getUsername();
         File userDir = new File(filesPath + "\\" + userName);
         if (!userDir.exists()) createDir(userDir.getPath());
    }
}
