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
