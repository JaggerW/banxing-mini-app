package com.nju.banxing.demo.aspect;

import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.exception.RetryException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @Author: jaggerw
 * @Description: 乐观锁重试AOP
 * @Date: 2020/12/17
 */
@Aspect
@Component
@Slf4j
public class RetryAspect implements Ordered {

    private static final int DEFAULT_RETRY_TIME = 5;

    private int retryTime = DEFAULT_RETRY_TIME;

    private int order = 2;

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * 定义注解 @Retry 为切点
     */
    @Pointcut("@annotation(com.nju.banxing.demo.annotation.Retry)")
    public void retry(){}


    @Around("retry()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        int attempt = 0;
        do {
            ++attempt;
            try {
                return proceedingJoinPoint.proceed();
            } catch (RetryException e){
                if(attempt > this.retryTime){
                    log.error("已超过最大重试次数");
                    throw new GlobalException(CodeMsg.SERVER_ERROR);
                }else {
                    log.info("===== 正在重试第{}次 =====",attempt);
                }
            }
        } while (attempt <= this.retryTime);
        return null;
    }


}
