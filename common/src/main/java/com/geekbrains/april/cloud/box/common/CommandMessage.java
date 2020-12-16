package com.geekbrains.april.cloud.box.common;

public class CommandMessage extends AbstractMessage {

   public enum Command {
        DELETE, FILELIST, CONTINUE
    }
    private String fileName;
    private Command command;

    public CommandMessage(Command command) {
        this.command = command;
    }

    public String getFileName() {
        return fileName;
    }

    public CommandMessage(Command command, String fileName) {
        this.fileName = fileName;
        this.command = command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }





}
