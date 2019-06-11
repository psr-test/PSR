package com.serverless.handlers.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Headers;
import com.serverless.model.BookAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

public class GetActionHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
            String id = pathParameters.get("id");

            Optional<BookAction> action = BookAction.get(id);

            if (action.isPresent()) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(action.get())
                        .setHeaders(Headers.headers)
                        .build();
            } else {
                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Book action with id: '" + id + "' not found.")
                        .setHeaders(Headers.headers)
                        .build();
            }
        } catch (Exception ex) {
            logger.error("Error in retrieving book action: ", ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}