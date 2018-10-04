package io.github.resilience4j.retry.internal;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.test.HelloWorldService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import javax.xml.ws.WebServiceException;
import java.util.concurrent.CompletableFuture;

public class InfiniteRetryTest {
    private HelloWorldService helloWorldService;
    //private long sleptTime = 0L;

    @Before
    public void setUp(){
        helloWorldService = Mockito.mock(HelloWorldService.class);
        //RetryImpl.sleepFunction = sleep -> sleptTime += sleep;
    }

    @Test
    public void shouldRunInfinitly() throws InterruptedException {
        // Given the HelloWorldService throws an exception
        BDDMockito.willThrow(new WebServiceException("BAM!")).given(helloWorldService).sayHelloWorld();

        // Create a Retry with default configuration
        RetryConfig config = RetryConfig.custom().infiniteAttempts().build();
        Retry retry = Retry.of("infinite", config);
        // Decorate the invocation of the HelloWorldService
        Runnable retryableRunnable = Retry.decorateRunnable(retry, helloWorldService::sayHelloWorld);

        // When
        CompletableFuture future = CompletableFuture.runAsync(retryableRunnable);
        Thread.sleep(100);
        future.cancel(true);
    }
}
