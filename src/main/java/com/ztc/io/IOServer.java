package com.ztc.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName IOServer
 * @Description TODO
 * @Author ztc
 *
 * @Date 2024/5/12 15:20
 */
public class IOServer {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(4000);

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
