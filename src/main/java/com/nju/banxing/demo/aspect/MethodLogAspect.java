package com.nju.banxing.demo.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.logs.MethodErrorInfo;
import com.nju.banxing.demo.common.logs.MethodLogInfo;
import com.nju.banxing.demo.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author: jaggerw
 * @Description: 日志注解切面类
 * @Date: 2020/11/3
 */
@Aspect
@Component
@Slf4j
public class MethodLogAspect implements Ordered {


    private static final String LINE_SEPARATOR = System.lineSeparator();

    private int order = 1;

    /**
     * 定义切点，有注解的地方
     */
    @Pointcut("@annotation(com.nju.banxing.demo.annotation.MethodLog)")
    public void methodLog(){}

    @Before("methodLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("================================START=====================================");
        try {
            log.info("Description    :{}",getAspectLogDescription(joinPoint));
            log.info("URL            :{}",request.getRequestURL().toString());
            log.info("HTTP Method    : {}", request.getMethod());
            log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            log.info("IP             : {}", NetworkUtil.getIpAddress(request));
            log.info("Request Args   : {}", JSON.toJSONString(getRequestParamsByJoinPoint(joinPoint)));
        }catch (Exception e){
            log.error("==== ERROR : DO BEFORE EXCEPTION");
        }

    }

    /**
     * 定义环绕体
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("methodLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Object result = proceedingJoinPoint.proceed();
        try {
            MethodLogInfo requestInfo = new MethodLogInfo();
            String ipAddress = NetworkUtil.getIpAddress(request);
            requestInfo.setIp(ipAddress);
            requestInfo.setUrl(request.getRequestURL().toString());
            requestInfo.setHttpMethod(request.getMethod());
            requestInfo.setClassMethod(String.format("%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                    proceedingJoinPoint.getSignature().getName()));
            requestInfo.setRequestParams(getRequestParamsByProceedingJoinPoint(proceedingJoinPoint));
            requestInfo.setResult(result);
            requestInfo.setTimeCost(System.currentTimeMillis() - start);
            log.info("===============METHOD INFO================= : {} =====================================",
                    JSON.toJSONString(requestInfo));
        }catch (Exception e){
            log.error("==== ERROR : DO AROUND EXCEPTION");
        }

        log.info("================================END=====================================");

        return result;
    }

    /**
     * 打印异常信息
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "methodLog()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        try {
            MethodErrorInfo requestErrorInfo = new MethodErrorInfo();
            String ipAddress = NetworkUtil.getIpAddress(request);
            requestErrorInfo.setIp(ipAddress);
            requestErrorInfo.setUrl(request.getRequestURL().toString());
            requestErrorInfo.setHttpMethod(request.getMethod());
            requestErrorInfo.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName()));
            requestErrorInfo.setRequestParams(getRequestParamsByJoinPoint(joinPoint));
            requestErrorInfo.setExceptionMsg(e.getMessage());
            log.error("=============== Error Request Info   : {} ===============", JSON.toJSONString(requestErrorInfo));
        }catch (Exception ep){
            log.error("==== ERROR : DO AFTER_THROW EXCEPTION");
        }
    }

    /**
     * 获取入参
     * @param proceedingJoinPoint
     *
     * @return
     * */
    private Map<String, Object> getRequestParamsByProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature)proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        //参数值(是controller层的所有入参而非仅是url请求中的参数)
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {

        Map<String, Object> requestParams = Maps.newHashMap();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];

            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();  //获取文件名
            }

            //如果是HttpServletRequest
            if(value instanceof HttpServletRequest){
                continue;
            }

            requestParams.put(paramNames[i], value);
        }

        return requestParams;
    }

    /**
     * 获取切面注解的描述
     *
     * @param joinPoint 切点
     * @return 描述信息
     * @throws Exception
     */
    public String getAspectLogDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder("");
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description.append(method.getAnnotation(MethodLog.class).value());
                    break;
                }
            }
        }
        return description.toString();
    }


    @Override
    public int getOrder() {
        return this.order;
    }
}
