package com.ztc.nettylogin;

import io.netty.util.AttributeKey;

/**
 * @ClassName Attributes
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/23 9:52
 */
public interface Attributes {
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");

}
