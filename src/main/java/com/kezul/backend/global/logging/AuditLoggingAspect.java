package com.kezul.backend.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Audit 어노테이션 메서드의 실행 전/후/예외 감사 로그를 남기는 Aspect.
 */
@Slf4j
@Aspect
@Component
public class AuditLoggingAspect {

    @Around("@annotation(audit)")
    public Object logAudit(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        String argsInfo = resolveArgs(pjp, audit.maskArgs(), signature);
        long startedAt = System.currentTimeMillis();

        var startBuilder = log.atInfo()
                .addKeyValue("event", audit.event().name())
                .addKeyValue("class", className)
                .addKeyValue("method", methodName)
                .addKeyValue("args", argsInfo);

        if (!audit.description().isEmpty()) {
            startBuilder = startBuilder.addKeyValue("description", audit.description());
        }
        startBuilder.log("Audit event started");

        try {
            Object result = pjp.proceed();

            log.atInfo()
                    .addKeyValue("event", audit.event().name())
                    .addKeyValue("method", methodName)
                    .addKeyValue("durationMs", System.currentTimeMillis() - startedAt)
                    .log("Audit event completed");

            return result;

        } catch (Throwable e) {
            log.atError()
                    .addKeyValue("event", audit.event().name())
                    .addKeyValue("method", methodName)
                    .addKeyValue("durationMs", System.currentTimeMillis() - startedAt)
                    .setCause(e)
                    .log("Audit event failed");

            throw e;
        }
    }

    private String resolveArgs(
            ProceedingJoinPoint pjp,
            boolean maskArgs,
            MethodSignature signature) {
        if (maskArgs) {
            return Arrays.stream(signature.getParameterTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", ", "[", "]"));
        }
        return Arrays.toString(pjp.getArgs());
    }
}
