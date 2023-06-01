package com.zinan.im.common;

import com.zinan.im.common.exception.ApplicationExceptionsStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzn
 * @date 2023/05/30 16:27
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {

    private Integer code;

    private String msg;

    private T data;

    public static ResponseVO<?> successResponse(Object data) {
        return new ResponseVO<>(BaseErrorCode.SUCCESS.getCode(), BaseErrorCode.SUCCESS.getError(), data);
    }

    public static ResponseVO<?> successResponse() {
        return new ResponseVO<>(BaseErrorCode.SUCCESS.getCode(), BaseErrorCode.SUCCESS.getError());
    }

    public static ResponseVO<?> errorResponse() {
        return new ResponseVO<>(500, "Internal error");
    }

    public static ResponseVO<?> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static ResponseVO<?> errorResponse(ApplicationExceptionsStrategy enums) {
        return new ResponseVO<>(enums.getCode(), enums.getError());
    }

    public boolean isOk() {
        return this.code == 200;
    }

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO<?> success() {
        this.code = 200;
        this.msg = "success";
        return this;
    }

    public ResponseVO<?> success(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
        return this;
    }
}
