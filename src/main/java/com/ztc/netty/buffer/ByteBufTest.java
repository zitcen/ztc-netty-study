package com.ztc.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @ClassName ByteBufTest
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/18 10:01
 */
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        print("allocate ByteBuf(9, 100)", buffer);
        //write方法改变写指针，写完之后写指针未到capacity的时候，buffer仍可写
        buffer.writeBytes(new byte[]{1,2,3,4});
        print("writeBytes(1,2,3,4)",buffer);
        //write方法改变写指针，写完之后写指针未到capacity的时候，buffer仍然可写，写完int
        buffer.writeInt(12);
        print("writeInt(12)",buffer);
        //write方法改变写指针，写完之后写指针等于capacity的时候，buffer不可写
        buffer.writeBytes(new byte[]{5});
        print("writeBytes(5)",buffer);
        //write方法改变写指针，写的时候发现buffer不可写则开始扩容，扩容之后capacity随即改变
        buffer.writeBytes(new byte[]{6});
        print("writeBytes(6)",buffer);
        //get方法不改变读写指针
        System.out.println("getByte(3) return：" + buffer.getByte(3));
        System.out.println("getShort(3) return：" + buffer.getShort(3));
        System.out.println("getInt(3)return： " + buffer.getInt(3));
        print("getByte()", buffer);
    }
    private static void print(String action,ByteBuf buf){
        System.out.println("after======" + action + "========");

    }
}
