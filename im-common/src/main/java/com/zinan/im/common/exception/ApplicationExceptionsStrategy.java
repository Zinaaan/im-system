package com.zinan.im.common.exception;

/**
 * @author lzn
 * @date 2023/05/30 16:13
 * @description
 */
public interface ApplicationExceptionsStrategy {

    int getCode();

    String getError();
}
