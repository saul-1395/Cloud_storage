package com.geekbrains.april.cloud.box.common;

import java.util.ArrayList;

public class FileAuth extends AbstractMessage {



    public enum UserStatus {
        NEW_USER, IS_AUTH, NOT_AUTH
    }

    private ArrayList<FileInfo> info;
    private String login;
    private String password;
    private UserStatus userStatus;

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void setInfo(ArrayList<FileInfo> info) {
        this.info = info;
    }

    public ArrayList<FileInfo> getInfo() {
        return info;
    }

    public String getLogin() {
                return login;
            }

    public FileAuth(String login, String password) {

        this.userStatus = UserStatus.NOT_AUTH;
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
