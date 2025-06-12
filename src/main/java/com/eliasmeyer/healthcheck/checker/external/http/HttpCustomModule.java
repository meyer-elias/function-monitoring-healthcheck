package com.eliasmeyer.healthcheck.checker.external.http;

import com.eliasmeyer.healthcheck.json.JsonModule;
import dagger.Module;
import dagger.Provides;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

@Module(
    includes = {
        JsonModule.class
    })
public class HttpCustomModule {

    @Provides
    @Singleton
    HttpConfig httpConfig() {
        return new HttpConfig(
            Long.parseLong(System.getenv("HTTP_REQUEST_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("HTTP_RESPONSE_TIMEOUT")),
            Integer.parseInt(System.getenv("HTTP_RETRY_COUNT")));
    }

    @Provides
    @Singleton
    CloseableHttpClient provideHttpClient() {
        // Configuration
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.of(httpConfig().connectionTimeout(), TimeUnit.MILLISECONDS))
            .setResponseTimeout(Timeout.of(httpConfig().responseTimeout(), TimeUnit.MILLISECONDS))
            .build();

        PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder
            .create()
            .setDefaultSocketConfig(SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(1))
                .build())
            .setDefaultConnectionConfig(ConnectionConfig.custom()
                .setSocketTimeout(Timeout.ofSeconds(1))
                .setConnectTimeout(Timeout.ofSeconds(2))
                .setTimeToLive(TimeValue.ofSeconds(5))
                .build())
            .build();
        connManager.setMaxTotal(3);
        connManager.setDefaultMaxPerRoute(3);

        return HttpClients.custom()
            // Retry strategy: max 1 retry with 1 second interval
            .setRetryStrategy(new DefaultHttpRequestRetryStrategy(httpConfig().retries(), TimeValue.ofSeconds(1)))
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(connManager)
            .evictExpiredConnections()
            .evictIdleConnections(TimeValue.ofSeconds(5))// also will close idle ones in this client
            .build();
    }
}
