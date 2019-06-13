package com.serverless.handlers.actions.give_back;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Headers;
import com.serverless.model.Book;
import com.serverless.model.BookAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public class ListGiveBackActionHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            List<BookAction> actions = BookAction.listGiveBacks();

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(actions)
                    .setHeaders(Headers.headers)
                    .build();
        } catch (Exception ex) {
            logger.error("Error in listing give backs: ", ex);
            return ApiGatewayResponse.builder().setStatusCode(500).build();
        }
    }
}