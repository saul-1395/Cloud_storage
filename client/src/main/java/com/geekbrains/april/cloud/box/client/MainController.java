package com.geekbrains.april.cloud.box.client;

import com.geekbrains.april.cloud.box.common.*;
import io.netty.util.ReferenceCountUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private boolean authenticated;
    private static final String pathLocation = "client\\client_storage\\";
    private static StringBuilder pathDirectory = new StringBuilder();
    private static StringBuilder pathServDirectory = new StringBuilder();
    private static ArrayList<FileInfo> fileServerList = new ArrayList<>();
    private static ArrayList<FileInfo> fileClientList = new ArrayList<>();

    @FXML
    TextArea progressField;
    @FXML
    HBox filePanel, fileList, authPanel, commandButton;
    @FXML
    TextField tfFileName;
    @FXML
    TableView<FileInfo> filesListServerTable, filesListClientTable;
    @FXML
    TableColumn<FileInfo, String> fileNameServer, fileNameClient;
    @FXML
    TableColumn<FileInfo, Long> fileSizeServer, fileSizeClient;
    @FXML
    PasswordField passField;
    @FXML
    TextField loginField;


    double progress ;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Network.start();

        this.authenticated = false;

        fileNameServer.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeServer.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileNameClient.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeClient.setCellValueFactory(new PropertyValueFactory<>("fileSize"));

        setAuthenticated();

        Thread t = new Thread(() -> {
            try {
                while (true) {
                     AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;

                        if(fm.getNumber()==1){
                            System.out.println(pathDirectory.toString() + fm.getFilename() + "  получен адрес");
                            if (Files.exists(Paths.get(pathDirectory.toString() + fm.getFilename()))) {
                                System.out.println("файл найден");
                                Files.delete(Paths.get(pathDirectory.toString() + fm.getFilename()));
                            }
                            Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                            fileClientList.add(new FileInfo(fm.getFilename(), fm.getFileSize()));
                            System.out.println("создан файл  " + System.currentTimeMillis());
                            refreshLocalFilesList();
                        } else {
                            System.out.println("пакет " + fm.getNumber() + "  получен  " + System.currentTimeMillis());
                            Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.APPEND);
                        }



                    }

                 /*   if (am instanceof CommandMessage){
                        CommandMessage cm = (CommandMessage) am;
                        if (cm.getCommand().equals(CommandMessage.Command.CONTINUE)){
                            System.out.println("оповещение об отправке файла: " + cm.getFileName() + " в " + System.currentTimeMillis()/1000);
                            Network.sendMsg(cm);
                        }
                    }*/

                    if (am instanceof FileAuth){
                        FileAuth fileAuth = (FileAuth) am;
                        fileServerList = fileAuth.getInfo(); //получили список файлов с сервака
                        fileClientList = new FileInfoList(fileAuth.getLogin(), pathLocation).getFileInfoList();

                        if(fileAuth.getUserStatus().equals(FileAuth.UserStatus.IS_AUTH)){
                            authenticated = true;
                        }
                        setAuthenticated();

                        System.out.println("вызван при авторизации");
                        refreshLocalFilesList();

                        pathDirectory.append(pathLocation).append(fileAuth.getLogin()).append("/");
                        pathServDirectory.append("server\\server_storage\\").append(fileAuth.getLogin()).append("/");
                        File f = new File(pathDirectory.toString());
                        if(f.mkdir()){
                            System.out.println("создана директория " + fileAuth.getLogin());
                        }
                    }

                    if (am instanceof FileListMessage ){
                        System.out.println("пришел список файлов");
                        fileServerList = ((FileListMessage) am).getFileList();
                        refreshLocalFilesList();
                    }

                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
               Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();

    }



    public void setAuthenticated() {  // ГУИ не доделал с видимостью
        commandButton.setVisible(authenticated);
        fileList.setVisible(authenticated);
        fileList.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        filePanel.setVisible(authenticated);
        filePanel.setManaged(authenticated);

        if (!authenticated) {
           // nickname = "";
        }
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {

        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new FileRequest(tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            filesListServerTable.getItems().clear();
            filesListServerTable.setItems(FXCollections.observableList(fileServerList));
            filesListClientTable.setItems(FXCollections.observableList(fileClientList));

        } else  {
            Platform.runLater(() -> {
                filesListServerTable.getItems().clear();
                filesListServerTable.setItems(FXCollections.observableList(fileServerList));
                filesListClientTable.setItems(FXCollections.observableList(fileClientList));

            });
        }

    }

    public void pressOnLoadBtn(ActionEvent actionEvent) throws IOException {
        FileMessage  msg = new FileMessage(Paths.get(pathDirectory.toString() + tfFileName.getText()));

        try {
            sendFile(msg, pathDirectory.toString(), tfFileName.getText());
            } finally {
            ReferenceCountUtil.release(msg);
            }
    }

    public void sendAuth(ActionEvent actionEvent) {

        FileAuth msg = new FileAuth(loginField.getText(), passField.getText());

        try {
            if (loginField.getText().length()>0 &
                    passField.getText().length()>0) {

                Network.sendMsg(msg);

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
        loginField.clear();
        passField.clear();
    }


    public void sendNewUser(ActionEvent actionEvent) {
        FileAuth msg = new FileAuth(loginField.getText(), passField.getText());

        msg.setUserStatus(FileAuth.UserStatus.NEW_USER);
        try {
            if (loginField.getText().length()>0 &
                    passField.getText().length()>0) {

                Network.sendMsg(msg);

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
        loginField.clear();
        passField.clear();
    }

    private void sendFile (FileMessage msg, String pathDirectory, String fileName) throws IOException {
        if (fileName.length() > 0) {


           long fileSize = msg.getFileSize();
            long sendFileSize = 0l;



            FileInputStream inputStream = new FileInputStream(pathDirectory + fileName);
            System.out.println(pathDirectory + fileName + " path");
            System.out.println(inputStream.available());
            int arrByteSize = 1024*1024;
            byte[] arrbyte = new byte[arrByteSize];
            int count = 0;

            while (inputStream.available()!=-1){
                System.out.println("читаем файл");
                if (inputStream.available()<arrByteSize){
                    System.out.println(" файл меньше чем " + arrByteSize);
                    byte[] arrTemp = new byte[inputStream.available()];
                    inputStream.read(arrTemp);

                    System.out.println(arrTemp.length + " размер последнего файла");
                    count++;
                    msg.setData(arrTemp);
                    msg.setNumber(count);
                  /*  sendFileSize = fileSize;
                    progress = ((double)sendFileSize/(double) fileSize)*100;
                    System.out.println((long)sendFileSize +"****" +  (long)fileSize+ "*******" + (int)progress);*/

                    Network.sendMsg(msg);
                    break;
                }
                inputStream.read(arrbyte);
                count++;
                msg.setData(arrbyte);
                msg.setNumber(count);
              /*  sendFileSize = (sendFileSize+arrByteSize);
                progress = ((double)sendFileSize/(double) fileSize)*100;
                System.out.println((long)sendFileSize +"****" +  (long)fileSize+ "*******" + (int)progress);*/
                Network.sendMsg(msg);
            }

        }
    }

    public void deleteFile(ActionEvent actionEvent) {

        if (tfFileName.getLength() > 0) {
            System.out.println("запрос на удаление ");
            Network.sendMsg(new CommandMessage(CommandMessage.Command.DELETE, tfFileName.getText()));
            tfFileName.clear();
        }

    }

    public void refreshList(ActionEvent actionEvent) {

            Network.sendMsg(new CommandMessage(CommandMessage.Command.FILELIST));


    }
}
