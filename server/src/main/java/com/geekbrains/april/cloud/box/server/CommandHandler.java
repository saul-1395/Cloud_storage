package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.CommandMessage;
import com.geekbrains.april.cloud.box.common.FileInfoList;
import com.geekbrains.april.cloud.box.common.FileListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandHandler  extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("СommandHandler");
        if (msg == null) {

            return;
        }

        if (msg instanceof CommandMessage) {
            CommandMessage cm = (CommandMessage)msg;

            if (cm.getCommand().equals(CommandMessage.Command.FILELIST)){ // допилить

            }

            switch (cm.getCommand()) {
                case DELETE:

                    if (Files.exists(Paths.get(MainHandler.serverPath + AuthHandler.getLogin() + "/" + cm.getFileName()))) {

                        Files.delete(Paths.get(MainHandler.serverPath + AuthHandler.getLogin() + "/" + cm.getFileName()));

                        FileInfoList fileInfoList = new FileInfoList(AuthHandler.getLogin(), MainHandler.serverPath);

                        ctx.writeAndFlush(new FileListMessage(fileInfoList.getFileInfoList()));
                    }
                    break;
                case FILELIST:
                    FileInfoList fileInfoList = new FileInfoList(AuthHandler.getLogin(), MainHandler.serverPath);
                    ctx.writeAndFlush(new FileListMessage(fileInfoList.getFileInfoList()));
                    break;
            }
        } else {
            ctx.fireChannelRead(msg);
        }





    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
