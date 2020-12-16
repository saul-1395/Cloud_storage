package com.geekbrains.april.cloud.box.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FileInfoList extends AbstractMessage {


    private String login;
    private String pathLocation;
    private ArrayList <FileInfo> fileInfoList;

    public ArrayList<FileInfo> getFileInfoList() {
        try {
            fileList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  fileInfoList;
    }

    public FileInfoList(String login, String pathLocation) {
        this.pathLocation = pathLocation;
        this.login = login;
    }

    private FileInfo getFileInfo(Path p)  {

        try {
            return new FileInfo(
                    p.getFileName().toString(),
                    Files.size(p)
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void  fileList () throws IOException {

        String pathDirectory = (new StringBuilder().append(pathLocation).append(login).append("/")).toString();
        System.out.println("формируем список файлов по адресу " + pathDirectory);
        fileInfoList = (ArrayList<FileInfo>)Files.list(Paths.get(pathDirectory)).map(p -> getFileInfo(p)).collect(Collectors.toList());

    }

}
