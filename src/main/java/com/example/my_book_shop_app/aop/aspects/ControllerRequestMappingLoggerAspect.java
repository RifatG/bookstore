package com.example.my_book_shop_app.aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.core.annotation.AnnotatedElementUtils.getMergedAnnotationAttributes;

@Aspect
@Component
public class ControllerRequestMappingLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(ControllerRequestMappingLoggerAspect.class);

    @Pointcut(value = "execution(@org.springframework.web.bind.annotation.RequestMapping * *(..)) ||" +
            "execution(@(@org.springframework.web.bind.annotation.RequestMapping *) * *(..))")
    public void requestMappingPointcut() {
        //pointcut
    }

    @Before("requestMappingPointcut()")
    public void requestMappingLooger(JoinPoint joinPoint) {
        String controllerClassName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().toShortString();
        String endpoint = "";
        String requestMethod = "";
        AnnotationAttributes annotationAttributes = getMergedAnnotationAttributes(
                ((MethodSignature) joinPoint.getSignature()).getMethod(),
                RequestMapping.class
        );
        if (annotationAttributes != null) {
            for (String value : (String[]) annotationAttributes.get("value"))
                endpoint = value;
            for (RequestMethod method : (RequestMethod[]) annotationAttributes.get("method"))
                requestMethod = method.toString();
            logger.info("Logged new request:");
            logger.info("Controller: {}, method {}; Request method {} to endpoint {}", controllerClassName, methodName, requestMethod, endpoint);
        }
    }

    @AfterReturning(
            pointcut = "requestMappingPointcut()",
            returning = "response")
    public void requestMappingSuccessReturningLogger(Object response) {
        logger.info("Request has been handled successfully and returned {}", response.getClass().getTypeName());
    }

    @AfterThrowing(
            pointcut = "requestMappingPointcut()",
            throwing = "exception")
    public void requestMappingSuccessReturningLogger(Exception exception) {
        logger.info("Has been gotten an exception {}", exception.getMessage());
    }
}
