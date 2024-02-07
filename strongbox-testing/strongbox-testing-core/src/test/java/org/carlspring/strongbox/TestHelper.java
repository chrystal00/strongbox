```java
package org.carlspring.strongbox;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Helper class for testing operations.
 * 
 * @author Przemyslaw Fusik
 */
public class TestHelper
{

    /**
     * Checks if an operation succeeds within a specified timeout period.
     * 
     * @param operation       The operation to be executed.
     * @param argument        The argument to be passed to the operation.
     * @param millisTimeout   The timeout period in milliseconds.
     * @param millisSleepTime The sleep time in milliseconds between retries.
     * @return {@code true} if the operation succeeds within the timeout period, {@code false} otherwise.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    public static <I> boolean isOperationSuccessed(Function<I, Boolean> operation,
                                                   I argument,
                                                   int millisTimeout,
                                                   int millisSleepTime)
            throws InterruptedException
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        boolean result = BooleanUtils.isTrue(operation.apply(argument));
        // Retry the operation until either it succeeds or the timeout is reached
        while (stopWatch.getTime() < millisTimeout && !result)
        {
            result = BooleanUtils.isTrue(operation.apply(argument));
            Thread.sleep(millisSleepTime); // Wait for the specified sleep time before retrying
        }
        stopWatch.stop();
        return result;
    }
    
    /**
     * Checks if a resource is available via HTTP.
     * 
     * @param url            The URL of the resource to check.
     * @param millisTimeout  The timeout period in milliseconds.
     * @return {@code true} if the resource is available within the timeout period, {@code false} otherwise.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    public static boolean isHttpResourceAvailable(String url, int millisTimeout)
            throws InterruptedException
    {
        // isOperationSuccessed method using a predefined method reference
        return isOperationSuccessed(TestHttpResourceAvailability::isResourceAvailable, url, millisTimeout, 1000);
    }

    /**
     * Custom class for checking HTTP resource availability
     */
    private static class TestHttpResourceAvailability
    {
        /**
         * Checks if a HTTP resource is available
         * 
         * @param url The URL of the resource to check.
         * @return {@code true} if the resource is available, {@code false} otherwise.
         */
        public static boolean isResourceAvailable(String url)
        {
            try {
                URL resourceUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
                connection.setRequestMethod("HEAD");
                int responseCode = connection.getResponseCode();
                // Check if the response code indicates success
                return responseCode >= 200 && responseCode < 300;
            } catch (Exception e) {
                // Error occurred or resource is not available
                return false;
            }
        }
    }
}

