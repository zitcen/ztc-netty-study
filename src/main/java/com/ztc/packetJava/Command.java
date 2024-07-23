package com.ztc.packetJava;

/**
 * @ClassName Command
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 11:15
 */
public interface Command {
    Byte LOGIN_REQUEST = 1;
    Byte LOGIN_RESPONSE = 2;
    Byte MESSAGE_REQUEST = 3;
    Byte MESSAGE_RESPONSE = 4;
}
