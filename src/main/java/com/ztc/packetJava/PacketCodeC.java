package com.ztc.packetJava;

import com.ztc.message.MessageRequestPacket;
import com.ztc.message.MessageResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ztc.packetJava.Command.*;

/**
 * @ClassName PacketCodeC
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 14:25
 */
public class PacketCodeC {
    private static final int MAGIC_NUMBER = 0x12345678;
    public static final PacketCodeC INSTANCE = new PacketCodeC();
    private final Map<Byte, Serializer> serializerMap = new HashMap<>();
    private final Map<Byte, Class<? extends Packet>> packetTypeMap = new HashMap<>();

    private PacketCodeC() {
        packetTypeMap.put(LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(LOGIN_RESPONSE, LoginResponsePacket.class);
        packetTypeMap.put(MESSAGE_REQUEST, MessageRequestPacket.class);
        packetTypeMap.put(MESSAGE_RESPONSE, MessageResponsePacket.class);
    }

    public ByteBuf encode(ByteBufAllocator alloc,Packet packet){
        //1.創建 ByteBuf 對象
        ByteBuf byteBuf = alloc.DEFAULT.ioBuffer();
        //2.序列化java對象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);
        //3.實際編碼過程
        byteBuf.writeInt(MAGIC_NUMBER);//魔數
        byteBuf.writeByte(packet.getVersion());//版本
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());//算法
        byteBuf.writeByte(packet.getCommand());//指令
        byteBuf.writeInt(bytes.length);//二進制數據長度
        byteBuf.writeBytes(bytes);//二進制數據

        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
        return byteBuf;
    }

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

        return packetTypeMap.get(command);
    }

    private Serializer  getSerializer(byte serializeAlgorithm){
        return new JSONSerializer();
    }

}
