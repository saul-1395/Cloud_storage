package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MainHandler extends ChannelInboundHandlerAdapter {
    static final String serverPath = AuthHandler.serverPath;
    private static boolean continueLoad = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FileInfoList fileInfoList = new FileInfoList(AuthHandler.getLogin(), serverPath);
        StringBuilder pathDirectory = new StringBuilder().append(serverPath).append(AuthHandler.getLogin()).append("/");
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                System.out.println("запрос на скачивание");
                  FileRequest fr = (FileRequest) msg;

                if (Files.exists(Paths.get(pathDirectory.toString() + fr.getFilename()))) {

                    FileMessage  fm = new FileMessage(Paths.get(pathDirectory.toString() + fr.getFilename()));

                    try {
                        this.sendFile(fm, pathDirectory.toString(), fr.getFilename(), ctx);
                    } finally {
                        ReferenceCountUtil.release(msg);
                    }
                }
            }
             if (msg instanceof FileMessage){
                 System.out.println("запрос на закачивание на сервер");
                 FileMessage fm = (FileMessage) msg;

                 if(fm.getNumber()==1){
                     System.out.println("создан новый файл");
                     if (Files.exists(Paths.get(pathDirectory.toString() + fm.getFilename()))) {
                         Files.delete(Paths.get(pathDirectory.toString() + fm.getFilename()));
                     }
                    Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);

                    ctx.writeAndFlush(new FileListMessage(fileInfoList.getFileInfoList())); //формируем список файлов и отправляем клменту
                     } else {
                     Files.write(Paths.get(pathDirectory.toString() + fm.getFilename()), fm.getData(), StandardOpenOption.APPEND);
                 }
                 ctx.fireChannelRead(fm);

             }

           /*  if (msg instanceof CommandMessage){
                 CommandMessage cm = (CommandMessage) msg;
                 if(CommandMessage.Command.CONTINUE.equals(cm.getCommand())){
                     continueLoad = true;

                 }else{
                     ctx.fireChannelRead(msg);
                 }

             }
             else {
                 ctx.fireChannelRead(msg);
             }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendFile(FileMessage fm, String pathDirectory, String fileName, ChannelHandlerContext ctx) throws IOException {
        if (fm.getFilename()!=null) {

            FileInputStream inputStream = new FileInputStream(pathDirectory + fileName);
            System.out.println(pathDirectory + fileName + " path");
            System.out.println(inputStream.available());
            int arrByteSize = 1024*1024;
            byte[] arrbyte = new byte[arrByteSize];
            int count = 0;

                    while (inputStream.available()>0){


                        if (inputStream.available() < arrByteSize) {
                            System.out.println(" файл меньше чем " + arrByteSize);

                            byte[] arrTemp = new byte[inputStream.available()];
                            inputStream.read(arrTemp);

                            System.out.println(arrTemp.length + " размер последнего файла " + System.currentTimeMillis() + " cформирован последний пакет");
                            count++;
                            fm.setData(arrTemp);
                            fm.setNumber(count);

                            ctx.writeAndFlush(fm);
                            break;
                        }


                        inputStream.read(arrbyte);

                        count++;
                        System.out.println(System.currentTimeMillis() + " сформирован первый пакет");
                        System.out.println("пакет " + count + "  отправлен");
                        fm.setData(arrbyte);
                        fm.setNumber(count);
                        ctx.writeAndFlush(fm);

                    }

           /* while (inputStream.available()>0){


                    if (inputStream.available() < arrByteSize) {
                        System.out.println(" файл меньше чем " + arrByteSize);

                        byte[] arrTemp = new byte[inputStream.available()];
                        inputStream.read(arrTemp);

                        System.out.println(arrTemp.length + " размер последнего файла " + System.currentTimeMillis() + " cформирован последний пакет");
                        count++;
                        fm.setData(arrTemp);
                        fm.setNumber(count);

                        ctx.writeAndFlush(fm);
                        break;
                    }

                        inputStream.read(arrbyte);

                        count++;
                        System.out.println(System.currentTimeMillis() + " сформирован первый пакет");
                        System.out.println("пакет " + count + "  отправлен");
                        fm.setData(arrbyte);
                        fm.setNumber(count);
                        ctx.writeAndFlush(fm);


            }*/
        }
    }
}
