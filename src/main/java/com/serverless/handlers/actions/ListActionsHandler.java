package com.serverless.handlers.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Headers;
import com.serverless.model.BookAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public class ListActionsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            List<BookAction> books = BookAction.list();

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(books)
                    .setHeaders(Headers.headers)
                    .build();
        } catch (Exception ex) {
            logger.error("Error in listing book actions: ", ex);
            return ApiGatewayResponse.builder().setStatusCode(500).build();
        }
    }
}