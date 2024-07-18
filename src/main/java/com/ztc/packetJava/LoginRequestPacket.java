package com.ztc.packetJava;

import lombok.Data;

import static com.ztc.packetJava.Command.LOGIN_REQUEST;

/**
 * @ClassName LoginRequestPacket
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 11:16
 */
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
