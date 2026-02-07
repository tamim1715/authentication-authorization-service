package com.tamim.auth.advice;

import com.tamim.auth.dto.error.ApiErrorResponse;
import com.tamim.auth.dto.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType contentType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof ApiErrorResponse) {
            return body;
        }

        return new ApiResponse<>(body, resolveMessage(request));
    }

    private String resolveMessage(ServerHttpRequest request) {
        if (request.getMethod() == HttpMethod.POST) return "Created successfully";
        if (request.getMethod() == HttpMethod.PUT) return "Updated successfully";
        if (request.getMethod() == HttpMethod.DELETE) return "Deleted successfully";
        return "Success";
    }
}
