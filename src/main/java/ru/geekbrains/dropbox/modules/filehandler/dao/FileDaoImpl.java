package ru.geekbrains.dropbox.modules.filehandler.dao;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.*;

import java.util.LinkedList;
import java.util.List;


@Repository
public class FileDaoImpl implements FileDaoService {

    public static final String SEPARATOR = File.separator;

    @Setter
    private String path;

    @Override
    public boolean createDir(String path) {
        if (!dirExists(path)) {
            return new File(path).mkdirs();
        }
        return false;
    }

    @Override
    public boolean dirExists(String path) {
        File dir = new File(path);
        if(dir.exists()) return dir.isDirectory();
        return false;
    }

    @Override
    public OutputStream getFileOutputStream(String filePath) throws IOException {
        return new FileOutputStream(new File(filePath));
    }

    @Override
    public InputStream getFileInputStream(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

    @Override
    public boolean deleteFile(String fileName) {
        File file = new File(path + SEPARATOR + fileName);
        if(file.exists()) return file.delete();
        throw new RuntimeException("Несуществующий файл запрошен на удаление");
    }


    @Override
    public void setPath(String path) {
        this.path = path;
    }

    public List<File> getFileList(){
        List<File> fileNamesList = new LinkedList<>();
        File[] files;
        File dir = new File(path);
        files = dir.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()) fileNamesList.add(files[i]);
                else if (files[i].isDirectory()) fileNamesList.add(files[i]);
            }
        }
        return fileNamesList;
    }

}
