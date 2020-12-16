package com.geekbrains.april.cloud.box.common;

import java.util.ArrayList;

public class FileRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequest(String filename) {
        this.filename = filename;
    }


    private ArrayList<FileInfo> info;

    public void setInfo(ArrayList<FileInfo> info) {
        this.info = info;
    }

    public ArrayList<FileInfo> getInfo() {
        return info;
    }
}

