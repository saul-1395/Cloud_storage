package com.geekbrains.april.cloud.box.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;
    private long number = 0l;
    private long fileSize = 0l;

    public long getFileSize() {
        return fileSize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }


    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public FileMessage(Path path) throws IOException {

        filename = path.getFileName().toString();
        fileSize = Files.size(path);
       // data = Files.readAllBytes(path);
    }
}
