package com.ztc.message;

import com.ztc.packetJava.Packet;
import lombok.Data;

import static com.ztc.packetJava.Command.MESSAGE_REQUEST;

/**
 * @ClassName MessageRequestPacket
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/23 9:47
 */
@Data
public class MessageRequestPacket extends Packet {
    private String msg;
    @Override
    public Byte getCommand() {
        return MESSAGE_REQUEST;
    }
}
