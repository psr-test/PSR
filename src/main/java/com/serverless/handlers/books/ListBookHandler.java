package com.serverless.handlers.books;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Headers;
import com.serverless.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public class ListBookHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            List<Book> books = Book.list();

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(books)
                    .setHeaders(Headers.headers)
                    .build();
        } catch (Exception ex) {
            logger.error("Error in listing books: ", ex);
            return ApiGatewayResponse.builder().setStatusCode(500).build();
        }
    }
}