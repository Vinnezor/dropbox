package ru.geekbrains.dropbox.server.filehandler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.server.authorization.dao.User;
import ru.geekbrains.dropbox.server.filehandler.dao.FileDaoService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;


@Service("fileService")
public class FileServiceImpl implements FileService {

    @Autowired
    FileDaoService fileDao;
    @Value("${filesPath}")
    private String filesPath;

    private String userName;
    private String delimeter = "\\";
    private String pathToUserDir;

    private User user;

    @PostConstruct
    public void init() {
        fileDao.createDir(filesPath);
    }

    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        return fileDao.getFileOutputStream(pathToUserDir + delimeter + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public File getFileByName(String fileName) {
        return new File(pathToUserDir + delimeter + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(pathToUserDir + delimeter + fileName);
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
         pathToUserDir  = filesPath + delimeter + userName;
         if (!fileDao.dirExists(pathToUserDir)) fileDao.createDir(pathToUserDir);
         fileDao.setPath(pathToUserDir);


    }
}
