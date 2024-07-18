package com.ztc.packetJava;

import com.alibaba.fastjson2.JSON;

/**
 * @ClassName JSONSerializer
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 11:34
 */
public class JSONSerializer implements Serializer{
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(Class<T> clz, byte[] bytes) {
        return JSON.parseObject(bytes,clz);
    }
}
