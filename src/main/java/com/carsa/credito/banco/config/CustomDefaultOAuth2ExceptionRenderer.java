package com.carsa.credito.banco.config;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.http.converter.jaxb.JaxbOAuth2ExceptionMessageConverter;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by esteban on 11/07/16.
 */
public class CustomDefaultOAuth2ExceptionRenderer extends DefaultOAuth2ExceptionRenderer {

    public static final String CARSA_TOKEN = "Carsa-Token";
    public static final String URL_FORMAT = "%s/oauth/authorize?response_type=token&client_id=CLIENT_ID&redirect_uri=REDIRECT_URI";

    private String oauthHost;

    private final Log logger = LogFactory.getLog(DefaultOAuth2ExceptionRenderer.class);

    private List<HttpMessageConverter<?>> messageConverters;

    public CustomDefaultOAuth2ExceptionRenderer(String oauthHost){
        this.oauthHost = oauthHost;
        this.messageConverters = geDefaultMessageConverters();
    }

    @Override
    public void handleHttpEntityResponse(HttpEntity<?> responseEntity, ServletWebRequest webRequest) throws Exception {
        if (responseEntity == null) {
            return;
        }
        HttpInputMessage inputMessage = createHttpInputMessage(webRequest);
        HttpOutputMessage outputMessage = createHttpOutputMessage(webRequest);
        if (responseEntity instanceof ResponseEntity && outputMessage instanceof ServerHttpResponse) {
            ((ServerHttpResponse) outputMessage).setStatusCode(((ResponseEntity<?>) responseEntity).getStatusCode());
        }
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            outputMessage.getHeaders().putAll(entityHeaders);
            addTokenURL(outputMessage,webRequest);
        }
        Object body = responseEntity.getBody();
        if (body != null) {
            writeWithMessageConverters(body, inputMessage, outputMessage);
        }
        else {
            // flush headers
            outputMessage.getBody();
        }
    }

    private void addTokenURL(HttpOutputMessage outputMessage, ServletWebRequest webRequest) {
        outputMessage.getHeaders().add(CARSA_TOKEN, getTokenURL(webRequest));
    }

    private String getTokenURL(ServletWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        //return String.format(URL_FORMAT, getBaseURL(request.getRequestURL().toString()));
        return String.format(URL_FORMAT, oauthHost);
    }

    /*private String getBaseURL(String requestURL) {
        String contentToRemove = requestURL.replaceAll("(http|https)://[a-zA-Z0-9:.]+/[a-zA-Z-]+","");
        return requestURL.replace(contentToRemove,"");
    }*/

    private void writeWithMessageConverters(Object returnValue, HttpInputMessage inputMessage,
                                            HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
        List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
        if (acceptedMediaTypes.isEmpty()) {
            acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
        }
        MediaType.sortByQualityValue(acceptedMediaTypes);
        Class<?> returnValueType = returnValue.getClass();
        List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
        for (MediaType acceptedMediaType : acceptedMediaTypes) {
            for (HttpMessageConverter messageConverter : messageConverters) {
                if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
                    messageConverter.write(returnValue, acceptedMediaType, outputMessage);
                    if (logger.isDebugEnabled()) {
                        MediaType contentType = outputMessage.getHeaders().getContentType();
                        if (contentType == null) {
                            contentType = acceptedMediaType;
                        }
                        logger.debug("Written [" + returnValue + "] as \"" + contentType + "\" using ["
                                + messageConverter + "]");
                    }
                    return;
                }
            }
        }
        for (HttpMessageConverter messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
    }

    private HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    private HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
        return new ServletServerHttpResponse(servletResponse);
    }

    private List<HttpMessageConverter<?>> geDefaultMessageConverters() {
        List<HttpMessageConverter<?>> result = new ArrayList<HttpMessageConverter<?>>();
        result.addAll(new RestTemplate().getMessageConverters());
        result.add(new JaxbOAuth2ExceptionMessageConverter());
        return result;
    }

}