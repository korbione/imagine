package com.dakor.imagine;

import org.junit.Assert;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * The test runner to check exception type with message.
 *
 * @author dkor
 */
public class ExpectsExceptionRunner extends BlockJUnit4ClassRunner {

    public ExpectsExceptionRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
        ExpectsException annotation = method.getAnnotation(ExpectsException.class);
        if (annotation == null) {
            return next;
        }

        return new ExpectExceptionWithMessage(next, annotation.type(), annotation.message());
    }

    private static class ExpectExceptionWithMessage extends Statement {
        private final Statement next;
        private final Class<? extends Throwable> expected;
        private final String expectedMessage;

        ExpectExceptionWithMessage(Statement next, Class<? extends Throwable> expected, String expectedMessage) {
            this.next = next;
            this.expected = expected;
            this.expectedMessage = expectedMessage;
        }

        @Override
        public void evaluate() throws Exception {
            try {
                next.evaluate();

                throw new AssertionError("There is not an exception but expected is: " + expected.getName());
            } catch (AssertionError e) {
                throw e;
            } catch (Throwable e) {
                Assert.assertTrue(String.format("Unexpected exception, expected<%s> but was <%s>", expected.getName(),
                        e.getClass().getName()), expected.isAssignableFrom(e.getClass()));

                if (expectedMessage != null && !expectedMessage.isEmpty()) {
                    assertErrorMessage(e, expectedMessage);
                }
            }
        }

        private void assertErrorMessage(Throwable e, String expectedText) {
            // concat all messages from the stack trace into one string
            String errorMessage = buildErrorStack(e, new StringBuilder()).toString();

            Assert.assertTrue(
                    String.format("The error message doesn't contain the text\nERROR MESSAGE: %s\nEXPECTED TEXT: %s",
                            errorMessage, expectedText), errorMessage.contains(expectedText));
        }

        private StringBuilder buildErrorStack(Throwable e, StringBuilder errorStackBuilder) {
            if (e != null) {
                if (errorStackBuilder.length() > 0) {
                    errorStackBuilder.append(" -> ");
                }
                errorStackBuilder.append(e.getMessage());
                buildErrorStack(e.getCause(), errorStackBuilder);
            }

            return errorStackBuilder;
        }
    }
}
