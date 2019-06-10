package com.serverless.handlers.actions.give_back;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.model.Book;
import com.serverless.model.BookAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class GiveBackActionHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

            String bookId = body.get("bookId").asText();
            String person = body.get("person").asText();

            Book book = Book.get(bookId).get();
            book.giveBack();
            book.save();
            BookAction action = new BookAction();
            action.setBookId(book.getId());
            action.setPerson(person);
            action.setAction(BookAction.Action.GIVE_BACK.name());
            action.setTimestamp(new Timestamp(new Date().getTime()).toString());
            action.save();

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(book)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception ex) {
            logger.error("Error in giving back a book: ", ex);

            Response responseBody = new Response("Error in giving back a book: " + ex, input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}