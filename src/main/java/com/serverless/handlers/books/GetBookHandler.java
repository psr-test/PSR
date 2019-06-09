package com.serverless.handlers.books;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.model.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class GetBookHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
            String bookId = pathParameters.get("id");

            Optional<Book> book = Book.get(bookId);

            if (book.isPresent()) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(book.get())
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            } else {
                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Book with id: '" + bookId + "' not found.")
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
        } catch (Exception ex) {
            logger.error("Error in retrieving book: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}