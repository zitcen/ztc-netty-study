package com.ztc.packetJava;

import lombok.Data;

import static com.ztc.packetJava.Command.LOGIN_REQUEST;
import static com.ztc.packetJava.Command.LOGIN_RESPONSE;

/**
 * @ClassName LoginRequestPacket
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 11:16
 */
@Data
public class LoginResponsePacket extends Packet{

    private String userId;

    private String userName;

    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {

        return LOGIN_RESPONSE;
    }
}
