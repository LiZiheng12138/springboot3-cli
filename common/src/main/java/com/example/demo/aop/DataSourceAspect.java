package com.example.demo.aop;

import com.example.demo.handle.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Order(-1)
public class DataSourceAspect {

    @Around("@annotation(com.example.demo.aop.DataSource) || @within(com.example.demo.aop.DataSource)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 读取方法或类上的注解
        DataSource ds = method.getAnnotation(DataSource.class);
        if (ds == null) {
            ds = point.getTarget().getClass().getAnnotation(DataSource.class);
        }

        if (ds != null) {
            String dataSourceName = ds.value();
            log.info("切换到数据源：{}", dataSourceName);
            DataSourceContextHolder.set(dataSourceName);
        }

        try {
            return point.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}