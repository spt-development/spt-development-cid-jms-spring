package com.spt.development.cid.jms.spring;

import com.spt.development.cid.CorrelationId;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import static com.spt.development.cid.jms.spring.MdcCorrelationIdPutter.MDC_CID_KEY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

class MdcCorrelationIdPutterTest {
    private interface TestData {
        String CORRELATION_ID = "4b5af7d0-6763-44b2-95b5-aff48ca61385";

        String ALT_MDC_CID_KEY = "test-correlation-id";
    }

    @BeforeEach
    void setUp() {
        CorrelationId.set(TestData.CORRELATION_ID);
    }

    @Test
    void proceed_defaultAspectAnyListener_shouldAddCorrelationIdToMdcContext() throws Throwable {
        final ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        final MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        final Object[] args = new Object[] { TestData.CORRELATION_ID, "{}" };
        final TestTarget target = new TestTarget(MDC_CID_KEY);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenAnswer((iom) -> target.test(args[0].toString(), args[1].toString()));
        when(methodSignature.getMethod()).thenReturn(TestTarget.class.getMethod("test", String.class, String.class));

        final Object result = createDefaultAspect().putCorrelationId(joinPoint);

        assertThat(MDC.get(MDC_CID_KEY), is(nullValue()));
        assertThat(result, is(notNullValue()));
    }

    @Test
    void proceed_customCidKeyAnyListener_shouldAddCorrelationIdToMdcContext() throws Throwable {
        final ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        final MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        final Object[] args = new Object[] { TestData.CORRELATION_ID, "{}" };
        final TestTarget target = new TestTarget(TestData.ALT_MDC_CID_KEY);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenAnswer((iom) -> {
            target.test(args[0].toString(), args[1].toString());
            return null;
        });
        when(methodSignature.getMethod()).thenReturn(TestTarget.class.getMethod("test", String.class, String.class));

        createAspect().putCorrelationId(joinPoint);

        assertThat(MDC.get(TestData.ALT_MDC_CID_KEY), is(nullValue()));
    }

    private MdcCorrelationIdPutter createDefaultAspect() {
        return new MdcCorrelationIdPutter();
    }

    private MdcCorrelationIdPutter createAspect() {
        return new MdcCorrelationIdPutter(TestData.ALT_MDC_CID_KEY);
    }

    private static class TestTarget {
        final String expectedCidKey;

        TestTarget(String expectedCidKey) {
            this.expectedCidKey = expectedCidKey;
        }

        public String test(@Header(JmsHeaders.CORRELATION_ID) String correlationId, @Payload String payload) {
            assertThat(MDC.get(expectedCidKey), is(TestData.CORRELATION_ID));

            // Just for the purpose of ensuring that the aspect returns the return value if for some reason, the method
            // is not void
            return "test";
        }
    }
}