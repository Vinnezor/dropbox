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


    private FileDaoService fileDao;
    @Value("${rootFilesDir}")
    private String filesPath;
    private String userName;
    private String pathToUserDir;

    @Autowired
    public FileServiceImpl(FileDaoService fileDao) {
        this.fileDao = fileDao;
    }

    @PostConstruct
    public void init() {
        filesPath = FileDaoImpl.SEPARATOR + filesPath;
        if (!fileDao.dirExists(filesPath)) fileDao.createDir(filesPath);
    }

    @Override
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        return fileDao.getFileOutputStream(pathToUserDir + FileDaoImpl.SEPARATOR + fileName);
    }


    @Override
    public File getFileByName(String fileName) {
        return new File(pathToUserDir + FileDaoImpl.SEPARATOR + fileName);
    }


    @Override
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(pathToUserDir + FileDaoImpl.SEPARATOR + fileName);
    }


    @Override
    public List<File> getFileList() {
        return fileDao.getFileList();
    }


    @Override
    public boolean deleteFile(String fileName) {
        return fileDao.deleteFile(fileName);
    }

    @Override
    public void setUser(User user) {
         userName = user.getUsername();
         pathToUserDir  = filesPath + FileDaoImpl.SEPARATOR + userName;
         if (!fileDao.dirExists(pathToUserDir)) fileDao.createDir(pathToUserDir);
         fileDao.setPath(pathToUserDir);
    }

    public void addDir(String dirname) {
        fileDao.createDir(pathToUserDir + FileDaoImpl.SEPARATOR  + dirname);
    }

    public void getDir(String dirname) {
        fileDao.setPath(pathToUserDir + FileDaoImpl.SEPARATOR  + dirname);
    }

    public long getFilesSize(File file) {
        return fileDao.getFileSize(file);
    }
}
