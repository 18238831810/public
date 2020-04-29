package com.cf.crs.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author frank
 * 2019/11/17
 **/
@Configuration
public class ResttemplateConfig {

    private static final HostnameVerifier PROMISCUOUS_VERIFIER = (s, sslSession ) -> true;


    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setHostnameVerifier(PROMISCUOUS_VERIFIER);
                }
                super.prepareConnection(connection, httpMethod);
            }
        };
        simpleClientHttpRequestFactory.setReadTimeout(10000);
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        return simpleClientHttpRequestFactory;
    }


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        //将原来的StringHttp字符编码改成utf-8
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) list.get(i)).setDefaultCharset(Charset.forName("utf-8"));
                break;
            }
        }
        return restTemplate;
    }
}
