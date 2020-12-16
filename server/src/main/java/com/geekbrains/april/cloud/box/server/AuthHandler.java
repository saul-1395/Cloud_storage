package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.FileAuth;
import com.geekbrains.april.cloud.box.common.FileInfoList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.IOException;

public class AuthHandler extends ChannelInboundHandlerAdapter  {
    private static String login;
    private static String nickname;
    static final String serverPath = "server\\server_storage\\";


    //private String login = new String();
   // private String nickname = new String();

    private  static AuthService authService = new DBAuthService();
    private boolean isAuth = false;




     static String getLogin() {
        return login;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("кто-то что-то прислал на сервер");

        if (msg == null) {

            return;
        }

        if (isAuth){

            System.out.println("это не аутентификация");
            ctx.fireChannelRead(msg);  //если файл уже имеет флажок тогда кидаем дальше по конвейру
        }

        if (msg instanceof FileAuth) {  //если входящий файл аутинфикатор(создали тип в коммоне)
            System.out.println("Это аутентификация");
            FileAuth am = (FileAuth)msg;  //делаем каст

            this.login = am.getLogin(); //выцепляем логин
            SQLHandler.connect();   //подключаем базу
            System.out.println("успешно подключена база");
            if(am.getUserStatus().equals(FileAuth.UserStatus.NEW_USER) & !SQLHandler.loginIsUnique(am.getLogin())){ //пробегаемся по базе и проверяем есть уже такой логин, это для регистрации нового пользователя
                System.out.println("регистрация новго пользователя");
                SQLHandler.setNewUser(am.getLogin(), am.getPassword()); //если ок, отдаем сюда новыйы логин и пароль

                createDirectory(login);
            }
            //дальше непосредственно аутентификация
             nickname = authService.getNicknameByLoginAndPassword(login, am.getPassword()); //здесь выцепляем никнейм по логину



            if (nickname != null){  //если есть такое то дальше идём
                System.out.println("подключён пользователь " + " login - " + login + "  nickname - " + nickname);
                isAuth =true; //ставим флажок аутентификацию
                am.setUserStatus(FileAuth.UserStatus.IS_AUTH); //ставим флажок в сообщении


              //  am.setInfo(fileListServer(login));
                am.setInfo(new FileInfoList(login, serverPath).getFileInfoList());
                ctx.writeAndFlush(am); // и кидаем обратно
            }
        }







    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void createDirectory(String login) throws IOException {
        StringBuilder pathNewDirectory = new StringBuilder().append(serverPath).append(login).append("/");

        File f = new File(pathNewDirectory.toString());
        if(f.mkdir()){
            System.out.println("create");
        }
    }

  /*  private FileInfo getFileInfo(Path path)  {

        try {
            return new FileInfo(
                    path.getFileName().toString(),
                    Files.size(path)
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  ArrayList<FileInfo> fileListServer (String login) throws IOException {


        String pathDirectory = (new StringBuilder().append("server\\server_storage\\").append(login).append("/")).toString();
        System.out.println("print path " + pathDirectory);
        ArrayList<FileInfo> fileListServer = (ArrayList<FileInfo>)Files.list(Paths.get(pathDirectory)).map(p -> getFileInfo(p)).collect(Collectors.toList());



        return fileListServer;
    }
*/


}
