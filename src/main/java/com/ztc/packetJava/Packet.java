package com.ztc.packetJava;

import lombok.Data;

/**
 * @ClassName Packet
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 11:12
 */
@Data
public abstract class Packet {
    /**
     * 协议版本
     */
    private Byte version =1;

    /**
     * 指令
     * @return
     */
    public abstract Byte getCommand();
}
