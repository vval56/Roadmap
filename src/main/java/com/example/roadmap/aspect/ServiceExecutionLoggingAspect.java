package com.example.roadmap.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceExecutionLoggingAspect {

  @Around("execution(public * com.example.roadmap.service..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    long startedAt = System.nanoTime();
    try {
      Object result = joinPoint.proceed();
      long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
      logger.info("Service method {} executed in {} ms",
          joinPoint.getSignature().toShortString(), durationMs);
      return result;
    } catch (Throwable ex) {
      long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
      logger.warn("Service method {} failed in {} ms: {}",
          joinPoint.getSignature().toShortString(), durationMs, ex.getMessage());
      throw ex;
    }
  }
}
