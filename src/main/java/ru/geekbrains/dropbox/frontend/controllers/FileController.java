package ru.geekbrains.dropbox.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.dropbox.modules.filehandler.service.FileService;

@RestController
public class FileController {

    @Autowired
    @Qualifier("fileService")
    FileService fileService;

    @RequestMapping(value = "/files/{$fileName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource getFileMethod(@PathVariable("fileName") String fileName, @PathVariable("$fileName") String $fileName) {
        return new FileSystemResource(fileService.getFileByName(fileName));
    }
}
