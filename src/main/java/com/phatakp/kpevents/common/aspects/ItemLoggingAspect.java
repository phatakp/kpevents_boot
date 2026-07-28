package com.phatakp.kpevents.common.aspects;

import com.phatakp.kpevents.common.exceptions.ActionNotAllowedException;
import com.phatakp.kpevents.common.exceptions.DuplicateResourceException;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ItemLoggingAspect {

    @Value("${app.env}")
    private String appEnv;

    @Pointcut("within(com.phatakp.kpevents.transactions.services.impl.ItemServiceImpl)")
    public void itemControllers(){}

    @Around("itemControllers()")
    public Object logAroundItem(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!appEnv.equals("dev")) {
            return joinPoint.proceed();
        }
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        // Log method entry and arguments

        log.info("[ENTER]==> {}.{}() with argument[s] = {}", className, methodName, arguments);
        long start = System.currentTimeMillis();

        try {
            // Execute the actual business method
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - start;

            // Log method exit, result, and performance duration
            log.info("[EXIT]==> {}.{}() Execution time = {} ms . ",
                    className, methodName, executionTime);

            return result;

        } catch (BadRequestException | ResourceNotFoundException | DuplicateResourceException |
                 ActionNotAllowedException e) {
            // Log any exceptions thrown during execution
            log.error("Exception in {}.{}() with cause = {}", className, methodName,
                    e.getMessage());
            throw e;
        } catch (Throwable e) {
            // Log any exceptions thrown during execution
            log.error("Exception in {}.{}() with cause = {}", className, methodName,
                    e.getCause() != null ? e.getCause() : e.getMessage());
            throw e;
        }
    }
}
