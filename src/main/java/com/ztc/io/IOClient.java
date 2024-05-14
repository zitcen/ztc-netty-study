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
