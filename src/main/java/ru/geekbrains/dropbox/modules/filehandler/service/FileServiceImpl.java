package ru.geekbrains.dropbox.modules.filehandler.service;

import lombok.Getter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.geekbrains.dropbox.modules.authorization.dao.User;
import ru.geekbrains.dropbox.modules.filehandler.dao.FileDaoService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;


@Service("fileService")
public class FileServiceImpl implements FileService {


    public static final char SEPARATOR = File.separatorChar;

    @Value("${rootFilesDir}")
    private String filesPath;

    private FileDaoService fileDao;

    @Getter
    private String currentPath;

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
        return fileDao.getFileOutputStream(createPath(fileName));
    }


    @Override
    public File getFileByName(String fileName) {
        return new File(createPath(fileName));
    }


    @Override
    public InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(createPath(fileName));
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
         pathToUserDir  = filesPath + SEPARATOR + user.getUsername();
         currentPath = pathToUserDir;
         if (!fileDao.dirExists(currentPath)) fileDao.createDir(currentPath);
         fileDao.setPath(currentPath);
    }

    @Override
    public void addDir(String dirname) {
        fileDao.createDir(createPath(dirname));
    }

    @Override
    public void getDir(String dirname) {
        currentPath = createPath(dirname);
        fileDao.setPath(currentPath);
    }

    @Override
    public long getFilesSize(File file) {
        return fileDao.getFileSize(file);
    }

    @Override
    public void upDir() {
        if(!isRoot(currentPath)) {
            char[] chars = currentPath.toCharArray();
            for (int i = chars.length - 1; i > 0; i--) {
                if(chars[i] == SEPARATOR) {
                    currentPath = currentPath.substring(0, i);
                    break;
                }
            }
            fileDao.setPath(currentPath);
        }
    }

    private String createPath(String filename) {
        return currentPath + SEPARATOR + filename;
    }

    private boolean isRoot(String path) {
        return pathToUserDir.equals(path);
    }
}
