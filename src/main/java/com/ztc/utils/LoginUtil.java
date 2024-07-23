package com.ztc.utils;

import com.ztc.nettylogin.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

import java.util.Objects;

/**
 * @ClassName LoginUtil
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/23 11:39
 */
public class LoginUtil {
    public static void makeAsLogin(Channel channel){
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel){
        Attribute<Boolean> attribute = channel.attr(Attributes.LOGIN);
        return !Objects.isNull(attribute);
    }

}
