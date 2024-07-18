# netty

## 第一章 即时聊天系统简介

​		Netty是一个异步基于事件驱动的高性能网络通信框架，在互联网中间件领域网络通信层是无可争议的最强王者。在本书中，笔者将带领大家使用Netty一步一步实现即时聊天工具的核心功能。 

###1.1 单聊流程

![](D:\study\资料\框架\picture\单聊流程.png)

1. A要与B聊天都需要与服务端建立连接，然后进入登录流程，服务端保存用户标识和TCP链接的映射。
2. A发消息给B,需要将B标识的数据包发送到服务器端，然后服务端找到数据中B的标识，在将数据发送给B。
3. 任意一方不在线，需要将消息缓存

### 1.2 单聊的指令

登录请求发送接收

登录响应接收发送

客户端发消息发送接收

服务端发消息接收发送

登出请求发送接收

登出响应接收发送

###1.3 群聊流程

![](D:\study\资料\框架\picture\群聊流程.png)

1. A、B、C依然会经历登录流程，服务端保存用户标识对应的TCP连接。
2. A发起群聊的时候，将A、B、C的标识发送至服务端，服务端拿到标识之后建立一个群ID，然后把这个ID与A、B、C的标识绑定。
3. 群聊中任意一方在群里聊天的时候，将群ID发送至服务端，服务端获得群ID之后，取出对应的用户标识，遍历用户标识对应的TCP连接，就可以将消息发送至每一个群聊成员

###1.4 群聊指令

创建群聊请求发送接收

群聊创建成功通知接收发送

加入群聊请求发送接收

群聊加入通知接收发送

发送群聊消息发送接收

接收群聊消息接收发送

退出群聊请求发送接收

退出群聊通知接收发送

### 1.5 netty

####1.5.1 客戶端

1. 客户端会解析控制台指令，比如发送消息或者建立群聊等指令。
2. 客户端会基于控制台的输入创建一个指令对象，用户告诉服务端具体要干什么事情。
3. TCP通信需要的数据格式为二进制，因此，接下来通过自定义二进制协议将指令对象封装成二进制，这一步被称为协议的编码。
4. 对于收到服务端的数据，首先需要截取出一段完整的二进制数据包（拆包/粘包相关的内容后续小节会讲解）。
5. 将此二进制数据包解析成指令对象，比如收到消息。6.将指令对象送到对应的逻辑处理器来处

![](D:\study\资料\框架\picture\客户端.png)

####1.5.2 服务端

与客户端类似

![](D:\study\资料\框架\picture\服务端.png)

##第二章 netty 是什么

###2.1 IO编程

**客户端**

~~~java
package com.ztc.io;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * @ClassName IOClient
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/12 15:30
 */
public class IOClient {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8000);
                while (true) {
                    try {
                        socket.getOutputStream().write((new Date()+"：helloworld").getBytes());
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

~~~

**服务端**

~~~java
package com.ztc.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName IOServer
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/12 15:20
 */
public class IOServer {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(8000);

        //接受新链接的线程
        new Thread(() -> {
            while (true) {
                try {
                    //(1)阻塞方法获取新连接
                    Socket accept = server.accept();
                    //(2)为每一个新连接都创建一个新线程，负责读取数
                    new Thread(() -> {
                        try {
                            int len;
                            byte[] data = new byte[1024];
                            InputStream inputStream = accept.getInputStream();
                            //(3)按字节流方式读取数据
                            while ((len = inputStream.read(data)) != -1) {
                                System.out.println(new String(data, 0, len));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}

~~~

​		socket需要一个线程来执行任务，该socket独占线程，当请求多、链接长的时候，线程无法有效回收和利用，对内存和CPU的开销都非常大。

### 2.2 NIO

​		同步非阻塞式IO，提供了选择器、通道和缓冲的概念。NIO 具有更高的并发性、可拓展性以及更少的资源消耗等优点。

#### 2.2.1 线程资源受限

​		在 NIO 的模型中，服务端在接受到一个新的链接的时候，它会绑定一个固定的线程，然后这个链接的所有读写都由这个线程来完成。在 NIO 的模型中，一个链接来了，不会创建一个线程去监听是否有数据可读，而是直接把链接注册到选择器上 Selector 上。然后通过检查这个选择器 Selector ，就可以批量检查出是否有数据可读。

####2.2.2 切换线程的效率低下

​		由于在 NIO 的模型中线程数量较少，所以切换线程的效率就大大的降低。

####2.2.3 IO 读写面向流

​		IO 读写是面向流的，一次性只能读写一个或者多个字节，并且读取完之后，无法在次进行读取，需要再次读取，需要自己进行缓存。而 NIO 读取的是 Buffer，可以任意读取里面的字节，不需要缓存数据，只需要移动读写的指针即可

~~~java
package com.ztc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName NIOServer
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/14 10:36
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        // 服务端选择器
        Selector serverSelector = Selector.open();
        // 客户端选择器
        Selector clientSelector = Selector.open();

        new Thread(() -> {
            try {
                //对应IO编程中的服务端启动,打开服务端的通道
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(8000));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
                while (true) {
                    // 监测是否有新的链接，这里的1指的是阻塞时间为 1ms
                    if (serverSelector.select(1) > 0) {
                        Set<SelectionKey> selectionKeys = serverSelector.selectedKeys();
                        Iterator<SelectionKey> iteratorKey = selectionKeys.iterator();
                        if (iteratorKey.hasNext()) {
                            SelectionKey key = iteratorKey.next();
                            if (key.isAcceptable()) {
                                try {
                                    //1)每来一个新连接，不需要创建一个线程，而是直接注册到
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    iteratorKey.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                while (true) {
                    //批量轮询哪些连接有数据可读，这里的1指阻塞的时间为1ms
                    if (clientSelector.select(1) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isReadable()) {
                                try {
                                    SocketChannel clientChannel =(SocketChannel)key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    //面相buffer
                                    clientChannel.read(byteBuffer);
                                    byteBuffer.flip();
                                    System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

~~~

**NIO 的核心思想**

1. NIO 的模型通常是两个线程，每一个线程都绑定一个 Selector 。在这个例子中，serverSelector 轮询是否有新的连接，一个 clientSelector 轮询是否有数据可读。
2. 服务端监测到有新的连接，将连接绑定到 clientSelector 上，这样就不用像 IO 模型一样在创建一个线程或者while死循环。
3. clientSelector 被一个 while 死循环包裹着，如果在某一时刻有多个链接可以读，那么通过clientSelector.select(1) 轮询出来，进而进行批量处理。
4. 数据读写是面向Buffer的。

**NIO 的缺点**

1. jdk 自带的 NIO 编程需要理解很多概念，编程复杂。对于NIO 入门很不友好，ByteBuffer 的 API 十分的反人类。
2. 对于 NIO 来说，一个比较合适的线程模式，能够很好的发挥它的优势，但是JDK 中没有实现，需要自己实现，就连简单的拆包和粘包都需要自己实现。
3. 项目庞大后 NIO 的各种接口会出现各种 BUG 维护成本很高。 

### 2.3 netty 编程

什么是 Netty : netty 是封装了 NIO 接口的框架，它简化了我们对 NIO 的操作。用官方语言说就是：Netty 是一个异步事件驱动的网络框架，用于快速开发可维护的客户端的服务端。

**netty 在编程中比 NIO 存在的优势** 

1. 使用 JDK 原生 NIO 需要了解太多概念，编程复杂，一不小心就Bug横飞
2. Netty底层IO模型随意切换，而这一切只需要做微小的改动，改改参数，Netty可以直接从 NIO 模型变身为IO模型。
3. Netty自带的拆包/粘包、异常检测等机制让你从 NIO 的繁重细节中脱离出来，只需要关心业务逻辑即可。
4. Netty解决了 JDK 很多包括空轮询在内的Bug。
5. Netty底层对线程、Selector做了很多细小的优化，精心设计的Reactor线程模型可以做到非常高效的并发处理。
6. 自带各种协议栈，让你处理任何一种通用协议都几乎不用亲自动手。
7. Netty社区活跃，遇到问题随时邮件列表或者Issue。
8. Netty已经历各大 RPC 框架、消息中间件、分布式通信中间件线上的广泛验证，健壮性无比强大。

**NETTY服务端**

~~~java
package com.ztc.netty;

import com.ztc.nio.NIOServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @ClassName NettyServer
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/14 16:21
 */
public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        serverBootstrap
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                }).bind(80000);
    }
}

~~~

1. boss对应 IOServer.java 中的负责接收新连接的线程，主要负责创建新连接。
2. worker对应 IOServer.java 中的负责读取数据的线程，主要用于读取数据及业务逻辑处理。

**NETTY客户端代码**

~~~java
package com.ztc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * @ClassName NettyClient
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/14 17:07
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringDecoder());

                    }
                });
        Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();
        while (true) {
            channel.writeAndFlush(new Date() + ": hello word!");
            Thread.sleep(2000);
        }
    }
}

~~~

## 第三章 Netty 开发环境配置

安装 jdk maven idea，使用idea创建maven项目，配置maven，添加依赖

## 第四章 服务端启动流程

​		在这一章我们来学习如何使用 Netty 来启动一个服务端程序。

~~~java
package com.ztc.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;



/**
 * @ClassName NettySimpleServer
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 10:25
 */
public class NettySimpleServer {
    public static void main(String[] args) {
        // 启动一个netty服务端需要指定 线程模型 IO模型 业务处理逻辑

        //监听端口，接收新连接的线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理每一个连接的数据读写的线程组
        NioEventLoopGroup bossWork = new NioEventLoopGroup();
        //引导服务端的启动工作
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, bossWork)
                .channel(NioServerSocketChannel.class)//指定 IO 模型
                .childHandler(new ChannelInitializer<NioSocketChannel>() {//定义后续每个连接的数据读写
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder()).addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                });
        //handle() 方法，用于服务器启动的时候添加逻辑；注意 childHandler() 方法是为每一个新的连接添加逻辑
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            @Override
            protected void initChannel(NioServerSocketChannel ch) throws Exception {

            }
        });

        //attr() 方法，给NioServerSocketChannel 维护一个map
        serverBootstrap.attr(AttributeKey.newInstance("serverName"), "nettyServer");
        // childAttr() 方法 可以为每个连接都指定自定义的属性
        serverBootstrap.childAttr(AttributeKey.newInstance("clientKey"), "clientValue");
        // option() 方法 用于给服务端Channel设置一些TCP参数 SO_BACKLOG 表示系统用于临时存放已完成三次握手的请求的队列的最大长度
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // childOption() 方法 用于给每个连接都设置一些TCP参数，SO_KEEPALIVE 表示是否开启TCP心跳机制
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        // TCP_NODELAY 表示是否开启Nagle算法
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bind(serverBootstrap, 8000);
    }

    public static void bind(final ServerBootstrap bootstrap, final int port) {
        bootstrap.bind(port).addListener((future) -> {
                    if (future.isSuccess()) {
                        System.out.println("端口绑定成功！");
                    } else {
                        System.out.println("端口绑定失败");
                        bind(bootstrap, port + 1);
                    }
                }
        );
    }
}

~~~

## 第五章 客户端启动流程

~~~java
package com.ztc.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName NettySimpleClient
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 10:25
 */
public class NettySimpleClient {
    /**
     * 最大重连间隔
     */
    private static final int MAX_RETRY = 5;
    public static void main(String[] args) throws InterruptedException {
        // 启动一个netty客户端需要指定 线程模型 IO模型 业务处理逻辑
        // 负责客户端的启动
        Bootstrap bootstrap = new Bootstrap();
        // 客户端的线程模型
        NioEventLoopGroup group = new NioEventLoopGroup();

        // 指定线程组和NIO模型
        bootstrap.group(group).channel(NioSocketChannel.class)
                // handler() 方法 业务处理逻辑
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                });
        // attr() 方法 用于给NioSocketChannel指定一个Map 按需从其中取值
        bootstrap.attr(AttributeKey.newInstance("clientName"), "nettyClient");
        // option() 方法用于指定一些TCP参数 CONNECT_TIMEOUT_MILLIS 指定连接超时的时间
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        // SO_KEEPALIVE 表示是否开启TCP心跳机制
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        // TCP_NODELAY 表示是否开启Nagle算法
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        // 建立连接
        Channel channel = connect(bootstrap, "127.0.0.1", 8000, MAX_RETRY);

        while (true) {
            channel.writeAndFlush(new Date() + ": Hello world!");
            Thread.sleep(1000);
        }
    }

    /**
     * 建立连接的方法，使用监听器来进行重试
     */
    private static Channel connect(Bootstrap bootstrap, String host, int port, int retry) {
        return bootstrap.connect(host,port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("建立连接成功");
            }else if(retry == 0){
                System.err.println("重试次数已用完，放弃连接！");
            }else{
                //连接次数
                int order = (MAX_RETRY - retry) + 1;
                // 定时任务下次执行重连的时间
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1),
                        delay, TimeUnit.SECONDS);
            }
        }).channel();
    }
}

~~~

## 第六章 客户端与服务端双向通讯

​		在本章需要实现的功能是：在客户端连接成功过后，向服务端写一段数据；服务端收到数据后开始打印，并向客户端返回一段数据。

### 6.1 客户端发送数据到服务端

​		在第五章我们知道，客户端相关的数据读写逻辑，是通过 Bootstrp 的 handler() 方法指定的

**客户端的代码**

~~~java
package com.ztc.netty.handlerchildhandler.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * @ClassName FirstClientHandler
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 14:44
 */
public class FirstClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * @Author ztc
     * @Description 这个方法在客户端建立连接成功后会被调用
     * @Date 2024/5/15
     * @param ctx
     *
    **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        System.out.println(new Date() + ": 客户端写出数据");
        //获取数据
        ByteBuf buffer = getBuffer(ctx);
        //写数据
        ctx.channel().writeAndFlush(buffer);
    }

    /**
     * @Author ztc
     * @Description 获取二进制 抽象ByteBuf的实体对象
     * @Date 2024/5/15
     * @param ctx 
     * @return io.netty.buffer.ByteBuf
     * 
    **/
    private ByteBuf getBuffer(ChannelHandlerContext ctx){
        //获取二进制抽象 ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();
        //准备数据，指定字符串的字符集为 UTF-8
        byte[] bytes = "你好，闪电侠，我跟你学习 NETTY".getBytes(Charset.forName("UTF-8"));
        //填充数据到 ByteBuf
        buffer.writeBytes(bytes);
        return buffer;
    }
}

~~~

**写数据的逻辑分为三步**

1. 获取 Netty 对二进制数据的抽象 ByteBuf，
2. 将二进制数据填充到 ByteBuf 中去，
3. 调用ctx.channel().writeAndFlush()把数据写到服务端

### 6.2 服务端读取客户端数据

~~~java
package com.ztc.netty.handlerchildhandler.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName CommunicationNettyServer
 * @Description 客户端向服务端通信
 * @Author ztc
 * @Date 2024/5/15 15:36
 */
public class CommunicationNettyClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FirstClientHandler());
                    }
                });
        bootstrap.connect("127.0.0.1",8000);
    }
}
package com.ztc.netty.handlerchildhandler.server;

import com.ztc.netty.handlerchildhandler.client.FirstClientHandler;
import com.ztc.netty.in.InboundHandlerA;
import com.ztc.netty.in.InboundHandlerB;
import com.ztc.netty.out.OutboundHandlerA;
import com.ztc.netty.out.OutboundHandlerB;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName CommunicationNettyServer
 * @Description 客户端向服务端通信
 * @Author ztc
 * @Date 2024/5/15 15:36
 */
public class CommunicationNettyServer {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //处理读数据的逻辑
                        ch.pipeline().addLast(new InboundHandlerA()).addLast(new InboundHandlerB());
                        ch.pipeline().addLast(new FirstServerHandler());
                        //处理写数据的逻辑
                        ch.pipeline().addLast(new OutboundHandlerA()).addLast(new OutboundHandlerB());
                    }
                });
        bootstrap.bind("127.0.0.1",8000);
    }
}

~~~

### 6.3 服务端返回数据到客户端

​		服务端写数据与客户端一致也是使用ByteBuf，然后填充二进制数据，最后使用 writeAndFlush()方法写出去。

​		在服务端写上 客户端的发送语句，在客户端重写channelRead()方法。

~~~java
  @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        //读取客户端的数据
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ":服务端读取到的数据 ->" + byteBuf.toString(Charset.forName("UTF-8")));

        // 返回数据到客户端
        //获取数据
        ByteBuf buffer = getBuffer(ctx);
        //写数据
        ctx.channel().writeAndFlush(buffer);
    }

@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ":客户端读取到的数据 ->" + byteBuf.toString(Charset.forName("UTF-8")));
    }
~~~

### 6.4 总结

- 我们了解到客户端和服务端的逻辑处理均在启动的时候，通过为逻辑处理链Pipeline添加逻辑处理器，来编写数据的读写逻辑。
- 在客户端连接成功之后，会回调到逻辑处理器的channelActive()方法。不管服务端还是客户端，收到数据之后都会调用channelRead()方法。

## 第七章 数据载体 ByteBuf 的介绍

​		Netty 的读写数据是以ByteBuf 单位进行交互的，结构如下图所示

- 如图所示 ByteBuf 有一下几个属性：容量上线(容量(废弃字节、可读字节、可写字节 )、可拓展字节)、读指针、写指针


**常用 API**

- capacity() ：底层占用多少个字节
- maxCapacity()：最大能占用多少字节
- readableBytes() 和 isReadable()  : 前者表示当前可读的字节数，值等与 writerIndex - readerIndex，如果两者相等，说明没有数据可读，isReadable() 为 false  。

- writableBytes()、isWritable()与maxWritableBytes()：writableBytes()表示可写的字节数，如果capacity - writerIndex 这连个数相等，表示不可写，但是还是可以写数据的，Netty会拓展字节到maxCapacity

**读写指针的API**

- readerIndex() 与 readerIndex(int)：前者表示返回当前指针，后者表示设置读指正
- writeIndex() 与 writeIndex() : 前者表示返回当前写指正，后者表示设置写指针

- markReaderIndex()与 resetreadIndex() :前者表示保存当前指针，后者表示将指针恢复到之前保存的指针
- markWriterIndex()与resetWriterIndex() ：与上面一组类似

**读写API**

​		关于ByteBuf 的读写，可以看做是从指针的开始位置开始读写数据。

- writeBytes(byte[] src) 与 buffer.readBytes(byte[] dst) : writeBytes表示将数据写入 ByteBuf 中，而buffer.readBytes 表示将 ByteBuf 中的数据读出来。这里dst字节数组的大小通常等于readableBytes()，而src字节数组大小的长度通常小于等于writableBytes()。
- writeByte(byteb)与buffer.readByte()：writeByte()表示往ByteBuf中写一字节，而buffer.readByte()表示从ByteBuf中读取一字节
- release()与retain()：由于Netty使用了堆外内存，而堆外内存是不被JVM直接管理的。也就是说，申请到的内存无法被垃圾回收器直接回收，所以需要我们手动回收。
- slice()、duplicate()、copy()：这三个方法会被放到一起比较，三者的返回值分别是一个新的ByteBuf对象。

## 第八章 客户端与服务端通讯协议编解码

### 8.1 什么是客户端与服务端的通讯协议

​		无论是 Netty 还是 Socket  编程都是基于 TCP 通讯的数据包，数据格式都是二进制的

### 8.2 通讯协议的设计

1. 魔数：固定几个字节，让服务器按照我们的协议处理数据，如果没有这个，会按照默认的的协议进行处理
2. 预留字段，大多数情况下，这个字段用不到，但是为了协议能够升级，还是要保留
3. 序列化算法
4. 指令的介绍
5. 数据长度

### 8.3 通讯协议的实现

​	将java对象封装成二进制数据包的方法叫做编码，把二进制中的数据包中解析出 java 对象的方法叫解码。

#### 8.3.1 java对象

1.通信过程中Java对象的抽象类。

~~~java
@Data
public abstract class Packet {
    /**
     * 协议banben
     */
    private Byte version =1;

    /**
     * 指令
     * @return
     */
    public abstract Byte getCommand();
}
~~~

2.定义请求登录数据包

~~~java
@Data
public class LoginRequestPacket extends Packet{

    private Integer userId;
    private String userName;
    private String password;

    @Override
    public Byte getCommand() {
        return LOGIN_REQUEST;
    }
}

~~~

#### 8.3.2 序列化

​		在本书中，我们使用最简单的JSON序列化方式，将阿里巴巴的Fastjson作为序列化框架。

~~~java
public interface Serializer {
    /**
     * 序列化算法
     * @return
     */
    byte getSerializerAlgorithm();

    /**
     * java對象轉二進制數據
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 二進制數據轉java對象
     * @param clz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clz,byte[] bytes);
}
~~~

#### 8.3.3 编码：封装成二进制数据的过程

~~~java
public class PacketCodeC {
    private static final int MAGIC_NUMBER = 0x12345678;
    public ByteBuf encode(Packet packet){
        //1.創建 ByteBuf 對象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        //2.序列化java對象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);
        //3.實際編碼過程
        byteBuf.writeByte(MAGIC_NUMBER);//魔數
        byteBuf.writeByte(packet.getVersion());//版本
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());//算法
        byteBuf.writeByte(packet.getCommand());//指令
        byteBuf.writeByte(bytes.length);//二進制數據長度
        byteBuf.writeBytes(bytes);//二進制數據
        return byteBuf;
    }
}
~~~

#### 8.3.4 解码：解析Java对象的过程

~~~java
public class PacketCodeC { 
	public Packet decode(ByteBuf byteBuf){
        //跳過魔數
        byteBuf.skipBytes(4);
        //跳過版本號
        byteBuf.skipBytes(1);
        //序列號算法標識
        byte serializeAlgorithm = byteBuf.readByte();
        //指令
        byte command = byteBuf.readByte();
        //數據包長度
        int length = byteBuf.readInt();
        //二進制數據
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer =  getSerializer(serializeAlgorithm);
        if(!Objects.isNull(requestType) && !Objects.isNull(serializer)){
            return serializer.deserialize(requestType,bytes);
        }
        return null;
    }
    private Class<? extends Packet> getRequestType(byte command){
        return LoginRequestPacket.class;
    }

    private Serializer  getSerializer(byte serializeAlgorithm){
        return new JSONSerializer();
    }
}
~~~



