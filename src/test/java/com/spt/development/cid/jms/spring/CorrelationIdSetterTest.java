package com.spt.development.cid.jms.spring;

import com.spt.development.cid.CorrelationId;
import jakarta.jms.Message;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

class CorrelationIdSetterTest {
    private interface TestData {
        String CORRELATION_ID = "2e28c03d-ee46-43b4-a364-94bfa9eb7e87";
    }

    @BeforeEach
    void setUp() {
        CorrelationId.set("?");
    }

    @Test
    void setCorrelationId_validJoinPointForMethodWithCorrelationIdArg_shouldSetCorrelationId() throws Exception {
        final JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        final MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(new TestTarget());
        when(joinPoint.getArgs()).thenReturn(new Object[] { "ignore", TestData.CORRELATION_ID, "{}" });
        when(methodSignature.getMethod()).thenReturn(TestTarget.class.getMethod("test", String.class, String.class, String.class));

        createSetter().setCorrelationId(joinPoint);

        assertThat(CorrelationId.get(), is(TestData.CORRELATION_ID));
    }

    @Test
    void setCorrelationId_validJoinPointForMethodWithMessageArg_shouldSetCorrelationId() throws Exception {
        final JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        final MethodSignature methodSignature = Mockito.mock(MethodSignature.class);
        final Message message = Mockito.mock(Message.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(new TestTarget());
        when(joinPoint.getArgs()).thenReturn(new Object[] { message });
        when(methodSignature.getMethod()).thenReturn(TestTarget.class.getMethod("test", Message.class));

        when(message.getJMSCorrelationID()).thenReturn(TestData.CORRELATION_ID);

        createSetter().setCorrelationId(joinPoint);

        assertThat(CorrelationId.get(), is(TestData.CORRELATION_ID));
    }

    @Test
    void setCorrelationId_validJoinPointForMethodWithoutHeaderOrMessageArg_shouldNotSetCorrelationId() throws Exception {
        final JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        final MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(new TestTarget());
        when(joinPoint.getArgs()).thenReturn(new Object[] { String.class });
        when(methodSignature.getMethod()).thenReturn(TestTarget.class.getMethod("test", String.class));

        createSetter().setCorrelationId(joinPoint);

        assertThat(CorrelationId.get(), is("no-cid"));
    }

    private CorrelationIdSetter createSetter() {
        return new CorrelationIdSetter();
    }

    private static class TestTarget {
        public void test(@Header("ignore") String ignored,
                         @Header(JmsHeaders.CORRELATION_ID) String correlationId,
                         @Payload String payload) {

        }

        public void test(@Payload String payload) {

        }

        public void test(Message message) {

        }
    }
}