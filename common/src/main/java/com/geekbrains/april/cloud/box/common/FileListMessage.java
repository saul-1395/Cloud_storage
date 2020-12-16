package com.geekbrains.april.cloud.box.common;

import java.util.ArrayList;

public class FileListMessage extends AbstractMessage {

    private ArrayList<FileInfo> fileList;

    public void setFileList(ArrayList<FileInfo> fileList) {
        this.fileList = fileList;
    }

    public ArrayList<FileInfo> getFileList() {
        return fileList;
    }

    public FileListMessage(ArrayList<FileInfo> fileList) {
        this.fileList = fileList;
    }
}
