package cn.ideamake.components.im.handler;


import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.common.enums.RestEnum;
import cn.ideamake.components.im.common.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Rest handler(HttpServletRequest req, Object handler, Exception e) {

        Rest rest = new Rest();
        rest.setCode(RestEnum.ERROR.getCode()).setMsg(RestEnum.ERROR.getMsg());
        printException(e, req);
        return rest;
    }

    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public Rest handler(Exception exception, HttpServletRequest request) {

        Rest<String> rest = new Rest<>();

        BindingResult bindingResult;
        if (exception instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
        } else {
            bindingResult = ((BindException) exception).getBindingResult();
        }

        String objectName = Objects.requireNonNull(bindingResult.getFieldError()).getObjectName();
        String field = bindingResult.getFieldError().getField();
        String errorMsg = bindingResult.getFieldError().getDefaultMessage();

        rest.setCode(RestEnum.PARAMETER_ERROR.getCode()).setMsg(errorMsg);

        String requestURI = request.getRequestURI();
        log.error("表单参数错误, 请求地址: {}, 参数对象={}, 错误字段={}, 错误详情={}", requestURI, objectName, field, errorMsg);

        return rest;
    }

    private void printException(Exception e, HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        log.error("请求URI = {}", requestURI);
        printAllReqParam(request);
        if (e instanceof AbstractException) {
            AbstractException exception = (AbstractException) e;
            log.error(exception.getMsg(), e);
            return;
        }

        StackTraceElement traceElement = e.getStackTrace()[0];
        String typeName = traceElement.getClassName();
        String method = traceElement.getMethodName();
        int lineNumber = traceElement.getLineNumber();
        log.error(typeName + "#" + method + "第" + lineNumber + "行出错", e);
    }

    private void printAllReqParam(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
        for (Entry<String, String[]> entryKey : entrySet) {
            Object key = entryKey.getKey();
            Object value = entryKey.getValue();
            log.error("参数名称: {}, 参数值: {}", key, value);
        }
    }
}
