package ru.geekbrains.dropbox.server.filehandler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.server.authorization.dao.User;
import ru.geekbrains.dropbox.server.filehandler.dao.FileDaoService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service("fileService")
public class FileServiceImpl implements FileService {

    @Autowired
    FileDaoService fileDao;

    @Value("${filesPath}")
    private String filesPath;
    private User user;
    private String userName;
    private String delimeter = "\\";


    @PostConstruct
    public void init() {
        fileDao.createDir(filesPath);
    }



    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        String path = filesPath + delimeter + userName + delimeter + fileName;
        return fileDao.getFileOutputStream(path);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public File getFileByName(String fileName) {
        return new File(filesPath + delimeter + userName + delimeter + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        String path = filesPath + delimeter + userName + delimeter + fileName;
        return new FileInputStream(path);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public List<File> getFileNameList() {
        return fileDao.getFileList();
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public boolean deleteFile(String fileName) {
        return fileDao.deleteFile(fileName);
    }

    @Override
    public void setUser(User user) {
         this.user = user;
         userName = user.getUsername();
         String path = filesPath + delimeter + userName;
         if (!fileDao.dirExists(path)) fileDao.createDir(path);
         fileDao.setPath(path);
    }
}
