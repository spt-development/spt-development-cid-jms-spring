package com.spt.development.cid.jms.spring;

import com.spt.development.cid.CorrelationId;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;

import javax.jms.JMSException;
import javax.jms.Message;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Aspect for initialising {@link CorrelationId} when receiving messages from a JMS queue with a
 * Spring {@link org.springframework.jms.annotation.JmsListener}.
 */
@Aspect
@Order(0)
public class CorrelationIdSetter {
    private static final Logger LOG = LoggerFactory.getLogger(CorrelationIdSetter.class);

    /**
     * Given a method annotated with the {@link org.springframework.jms.annotation.JmsListener} annotation, this aspect
     * uses the incoming JMS message to initialise the {@link CorrelationId}. It does this in one of two ways, whilst
     * looping through the listener method parameters:
     *
     * <ol>
     *     <li>If it finds a parameter annotated with the {@link Header} annotation and a name/value matching
     *     {@link JmsHeaders#CORRELATION_ID} it will use the value of that parameter.</li>
     *     <li>If it finds a {@link Message} parameter it will use the value returned by
     *     {@link Message#getJMSCorrelationID()}.</li>
     * </ol>
     *
     * @param point the aspect join point required for implementing a {@link Before} aspect.
     *
     * @throws NoSuchMethodException an unexpected exception thrown when retrieving the listener method details.
     * @throws JMSException when a {@link Message} parameter is found, but there is a problem call
     *     {@link Message#getJMSCorrelationID()}.
     */
    @Before("@annotation(org.springframework.jms.annotation.JmsListener)")
    public void setCorrelationId(final JoinPoint point) throws NoSuchMethodException, JMSException {
        final MethodSignature signature = (MethodSignature) point.getSignature();
        final String methodName = signature.getMethod().getName();
        final Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        final Annotation[][] annotations = point.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            final boolean isCorrelationId = Arrays.stream(annotations[i])
                    .filter(a -> a instanceof Header)
                    .map(Header.class::cast)
                    .anyMatch(h -> JmsHeaders.CORRELATION_ID.equals(h.value()));

            if (isCorrelationId) {
                CorrelationId.set(point.getArgs()[i].toString());

                LOG.debug("[{}] Set correlationId from JMS Listener parameter annotated as correlation ID header", CorrelationId.get());

                return;
            }

            if (Message.class.isAssignableFrom(parameterTypes[i])) {
                CorrelationId.set(Message.class.cast(point.getArgs()[i]).getJMSCorrelationID());

                LOG.debug("[{}] Set correlationId from JMS message correlationId", CorrelationId.get());

                return;
            }
        }
        CorrelationId.reset();

        LOG.warn("[{}] Unable to set correlation ID. Method annotated with JmsListener annotation must have a parameter " +
                "annotated with the Header annotation with its value set to JmsHeaders.CORRELATION_ID *or* a parameter of " +
                "type Message", CorrelationId.get());
    }
}
