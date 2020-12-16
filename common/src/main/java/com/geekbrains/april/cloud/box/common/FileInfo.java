package com.geekbrains.april.cloud.box.common;

import java.io.Serializable;

public class FileInfo  implements Serializable {

    private String fileName;
    private Long fileSize;


   public FileInfo(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;

    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize / (1024 * 1024);
    }
}



