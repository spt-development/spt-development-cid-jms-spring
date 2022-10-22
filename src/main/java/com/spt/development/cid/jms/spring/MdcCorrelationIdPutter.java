package com.spt.development.cid.jms.spring;

import com.spt.development.cid.CorrelationId;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

/**
 * Aspect for setting correlationID in {@link MDC} context. Aspect must be configured to be applied after
 * {@link CorrelationIdSetter}.
 */
@Aspect
@Order(1)
public class MdcCorrelationIdPutter {
    /**
     * Default MDC correlation ID key, overridden with constructor.
     */
    public static final String MDC_CID_KEY = "cid";

    private final String cidKey;

    /**
     * Creates default aspect that uses MDC_CID_KEY for the correlation ID key.
     */
    public MdcCorrelationIdPutter() {
        this(MDC_CID_KEY);
    }

    /**
     * Creates an aspect with a custom correlation ID MDC key.
     *
     * @param cidKey the custom name of the correlation ID key.
     */
    public MdcCorrelationIdPutter(final String cidKey) {
        this.cidKey = cidKey;
    }

    /**
     * Given a method annotated with the {@link org.springframework.jms.annotation.JmsListener} annotation, this aspect
     * adds the correlationID to the {@link MDC} context.
     *
     * @param point the aspect join point required for implementing a {@link Around} aspect.
     *
     * @return the value returned from the annotated method.
     *
     * @throws Throwable thrown if the method throws a {@link Throwable}.
     */
    @Around("@annotation(org.springframework.jms.annotation.JmsListener)")
    public Object putCorrelationId(final ProceedingJoinPoint point) throws Throwable {
        try (final MDC.MDCCloseable mdc = MDC.putCloseable(cidKey, CorrelationId.get())) {
            assert mdc != null;

            return point.proceed();
        }
    }
}
