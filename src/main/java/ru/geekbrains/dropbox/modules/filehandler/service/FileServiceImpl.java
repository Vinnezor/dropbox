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

    public static final String SEPARATOR = File.separator;
    private String currentPath;

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
        filesPath = SEPARATOR + filesPath;
        if (!fileDao.dirExists(filesPath)) fileDao.createDir(filesPath);
    }

    @Override
    public OutputStream getFileOutputStream(String fileName) throws IOException {
        return fileDao.getFileOutputStream(currentPath + SEPARATOR + fileName);
    }


    @Override
    public File getFileByName(String fileName) {
        return new File(currentPath + SEPARATOR + fileName);
    }


    @Override
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(currentPath + SEPARATOR + fileName);
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
         currentPath  = filesPath + SEPARATOR + userName;
         pathToUserDir = currentPath;
         if (!fileDao.dirExists(currentPath)) fileDao.createDir(currentPath);
         fileDao.setPath(currentPath);
    }

    public void addDir(String dirname) {
        fileDao.createDir(currentPath + SEPARATOR  + dirname);
    }

    public void getDir(String dirname) {
        currentPath = currentPath + SEPARATOR  + dirname;
        fileDao.setPath(currentPath);
    }

    public long getFilesSize(File file) {
        return fileDao.getFileSize(file);
    }
}
