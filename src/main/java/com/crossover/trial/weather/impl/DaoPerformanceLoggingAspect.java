package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.RestWeatherQueryEndpoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.logging.Logger;

/**
 * Method execution time logging aspect
 */
@Component
@Aspect
public class DaoPerformanceLoggingAspect {

    private final static Logger LOGGER = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

    @Around("execution(* com.crossover.trial.weather..*DaoImpl.*(..))")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        StringBuilder logMessage = new StringBuilder();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            logMessage.append("'").append(arg).append("', ");
        }
        if (args.length > 0) {
            logMessage.delete(logMessage.length() - 2, logMessage.length());
        }

        logMessage.append(")");
        logMessage.append(" execution time: ");
        logMessage.append(stopWatch.getTotalTimeMillis());
        logMessage.append(" ms");

        LOGGER.fine(logMessage.toString());

        return result;
    }

}
