# Training project for cloud storage using client server architecture.

## Applied technology:

##### <li>language: Java

##### <li>build automation tool: Maven

##### <li>frameworks: Netty on server (pipeline), NIO on client

### This example shows how the client-server architecture is implemented using the example of cloud data storage.
### The project has three modules: server, common and client:


#### Server from Netty with pipeline:

```Java
ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new AuthHandler(),
                                    new MainHandler(),
                                    new MessageHandler(),
                                    new CommandHandler()
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = b.bind(8189).sync();
            future.channel().closeFuture().sync();
```
#### Events pass handlers in the order in which you passed from the function to the pipeline.


#### Client from NIO:

```Java
  public static void start() {
        try {
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

#### The common module contains file types that are used for data exchange between the client and server.