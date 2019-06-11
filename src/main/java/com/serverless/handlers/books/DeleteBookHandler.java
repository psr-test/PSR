package com.serverless.handlers.books;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Headers;
import com.serverless.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class DeleteBookHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String bookId = pathParameters.get("id");

            boolean success = Book.delete(bookId);

            if (success) {
              return ApiGatewayResponse.builder()
                        .setStatusCode(204)
                        .setHeaders(Headers.headers)
                        .build();
            } else {
              return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Book with id: '" + bookId + "' not found.")
                        .setHeaders(Headers.headers)
                        .build();
            }
        } catch (Exception ex) {
            logger.error("Error in deleting book: ", ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}