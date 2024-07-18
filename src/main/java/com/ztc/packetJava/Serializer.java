package com.ztc.packetJava;

/**
 * @ClassName Serializer
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 11:27
 */
public interface Serializer {

    /**
     * JSON序列化
     */
    byte JSON_SERIALIZER = 1;
    Serializer DEFAULT = new JSONSerializer();
    /**
     * 序列化算法
     * @return
     */
    byte getSerializerAlgorithm();

    /**
     * java對象轉二進制數據
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 二進制數據轉java對象
     * @param clz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clz,byte[] bytes);
}
