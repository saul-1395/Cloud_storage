package com.geekbrains.april.cloud.box.server;

import com.geekbrains.april.cloud.box.common.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("MessageHandler");
            if (msg == null) {

                return;
            }

            if (msg instanceof FileMessage) {
                FileMessage fm = (FileMessage)msg;
                if (Files.exists(Paths.get("server\\server_storage\\"+  fm.getFilename()))){ // допилить
                    //System.out.println("файл: " + fm.getFilename() + " есть на сервере");
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