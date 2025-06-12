package com.eliasmeyer.healthcheck.checker.external.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eliasmeyer.healthcheck.exception.HealthCheckMonitoringException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class HttpService {

    private final ObjectMapper objectMapper;

    private final CloseableHttpClient httpClient;

    private final Logger logger = LogManager.getLogger(HttpService.class);


    @Inject
    public HttpService(ObjectMapper objectMapper, CloseableHttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    // --------- HTTP GET without query parameters ---------
    public <T> T get(String url, Class<T> clazz, Map<String, String> headers) {
        HttpGet request = new HttpGet(url);
        headers.forEach(request::addHeader);

        return executeRequest(request, clazz);
    }

    public <T> T form(String url, Map<String, String> body, Class<T> clazz, Map<String, String> headers) {

        // Build form data
        List<NameValuePair> formParams = new ArrayList<>();
        body.forEach((key, value) -> formParams.add(new BasicNameValuePair(key, value)));

        // Set form entity
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams);

        HttpPost request = new HttpPost(url);
        headers.forEach(request::addHeader);
        request.setEntity(formEntity);

        return executeRequest(request, clazz);
    }

    // --------- Generic request executor ---------
    private <T> T executeRequest(HttpUriRequestBase request, Class<T> clazz) {
        try {
            return httpClient.execute(request, response -> {
                int status = response.getCode();
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return objectMapper.readValue(responseBody, clazz);
                } else {
                    logger.error("Error response http status: {}", status);
                    throw new HealthCheckMonitoringException("Unexpected response status: " + status);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new HealthCheckMonitoringException("Error converting JSON to object", e);
        } catch (IOException e) {
            logger.error(e);
            throw new HealthCheckMonitoringException("HTTP request failed: " + e.getMessage(), e);
        }
    }
}
