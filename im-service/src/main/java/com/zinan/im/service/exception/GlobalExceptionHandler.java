package com.zinan.im.service.exception;

import com.zinan.im.common.BaseErrorCode;
import com.zinan.im.common.ResponseVO;
import com.zinan.im.common.exception.ApplicationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author lzn
 * @date 2023/05/30 20:51
 * @description
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO<?> unknownException(Exception e) {
        e.printStackTrace();
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.SYSTEM_ERROR.getCode());
        resultBean.setMsg(BaseErrorCode.SYSTEM_ERROR.getError());
        // Do the logic of unknown exception, for example, send an email or mobile message

        return resultBean;
    }

    /**
     * Exception processing of parameter verification
     *
     * @param ex: ConstraintViolationException
     * @return ResponseVO
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Object handleMethodArgumentNotValidException(ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
            String paramName = pathImpl.getLeafNode().getName();
            String message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());
            resultBean.setMsg(message);

            return resultBean;
        }
        resultBean.setMsg(BaseErrorCode.PARAMETER_ERROR.getError() + ex.getMessage());
        return resultBean;
    }

    /**
     * Custom exception
     *
     * @param e: BindException
     * @return ResponseVO
     */
    @ExceptionHandler(value = ApplicationException.class)
    @ResponseBody
    public Object applicationExceptionHandler(ApplicationException e) {
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getError());
        return resultBean;
    }

    /**
     * Exception processing of parameter verification
     *
     * @param ex: BindException
     * @return ResponseVO
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Object handleException2(BindException ex) {
        FieldError err = ex.getFieldError();
        String message = "";
        if (err != null) {
            message = "参数{".concat(err.getField()).concat("}").concat(err.getDefaultMessage() == null ? "" : err.getDefaultMessage());
        }
        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        resultBean.setMsg(message);
        return resultBean;
    }

    /**
     * json format
     *
     * @param ex: MethodArgumentNotValidException
     * @return ResponseVO
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Object handleException1(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult re = ex.getBindingResult();
        for (ObjectError error : re.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());

        ResponseVO<?> resultBean = new ResponseVO<>();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        resultBean.setMsg(BaseErrorCode.PARAMETER_ERROR.getError() + " : " + errorMsg.toString());
        return resultBean;
    }
}
