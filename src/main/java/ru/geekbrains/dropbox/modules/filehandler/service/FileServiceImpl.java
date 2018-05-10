package ru.geekbrains.dropbox.modules.filehandler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.modules.authorization.dao.User;
import ru.geekbrains.dropbox.modules.filehandler.dao.FileDaoImpl;
import ru.geekbrains.dropbox.modules.filehandler.dao.FileDaoService;

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
    private String pathToUserDir;

    private User user;

    @PostConstruct
    public void init() {
        fileDao.createDir(filesPath);
    }

    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        return fileDao.getFileOutputStream(pathToUserDir + FileDaoImpl.SEPARATOR + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public File getFileByName(String fileName) {
        return new File(pathToUserDir + FileDaoImpl.SEPARATOR + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(pathToUserDir + FileDaoImpl.SEPARATOR + fileName);
    }


    @Override
    //@PreAuthorize("hasAnyRole('USER')")
    public List<File> getFileList() {
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
         pathToUserDir  = filesPath + FileDaoImpl.SEPARATOR + userName;
         if (!fileDao.dirExists(pathToUserDir)) fileDao.createDir(pathToUserDir);
         fileDao.setPath(pathToUserDir);


    }
}
