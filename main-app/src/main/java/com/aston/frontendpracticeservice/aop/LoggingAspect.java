package com.aston.frontendpracticeservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;
import java.util.stream.IntStream;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String METHOD_CALL_WITH_PARAMS = "Вызов метода {} в классе {} с параметрами: {}";
    private static final String METHOD_SUCCESSFUL_COMPLETION = "Метод {} в классе {} успешно завершен";
    private static final String METHOD_ERROR = "Ошибка в методе {} класса {}: {}";

    @Around("execution(* com.aston.frontendpracticeservice.service.*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        StringJoiner params = new StringJoiner(", ");
        IntStream.range(0, args.length)
                .forEach(i -> params.add(paramNames[i] + "=" + args[i]));

        log.info(METHOD_CALL_WITH_PARAMS, methodName, className, params.toString());

        try {
            Object result = joinPoint.proceed();
            log.info(METHOD_SUCCESSFUL_COMPLETION, methodName, className);
            return result;
        } catch (Exception e) {
            log.error(METHOD_ERROR, methodName, className, e.getMessage());
            throw e;
        }
    }

}