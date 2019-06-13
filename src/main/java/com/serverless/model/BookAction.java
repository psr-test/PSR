package com.serverless.model;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.serverless.DynamoDBAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.*;

import static java.util.Collections.singletonMap;

@DynamoDBTable(tableName = "PLACEHOLDER_bookS_TABLE_NAME")
public class BookAction {
    public enum Action {
        BORROW, GIVE_BACK
    }

    private static final String TABLE_NAME = System.getenv("BOOK_ACTION_TABLE_NAME");
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private static final DynamoDBAdapter dbAdapter;
    private static final DynamoDBMapper mapper;
    private static final AmazonDynamoDB client;
    private String id;
    private String bookId;
    private String person;
    private Action action;
    private String timestamp;

    static {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(TABLE_NAME))
                .build();
        dbAdapter = DynamoDBAdapter.getInstance();
        mapper = dbAdapter.createDbMapper(mapperConfig);
        client = dbAdapter.getClient();
    }

    public BookAction() { }

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "bookId")
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    @DynamoDBRangeKey(attributeName = "person")
    public String getPerson() {
        return person;
    }
    public void setPerson(String person) {
        this.person = person;
    }

    @DynamoDBAttribute(attributeName = "action")
    public String getAction() {
        return action.name();
    }
    public void setAction(String action) {
        this.action = Action.valueOf(action);
    }

    @DynamoDBAttribute(attributeName = "timestamp")
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static List<BookAction> list() {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<BookAction> results = mapper.scan(BookAction.class, scanExp);
        for (BookAction action : results) {
            logger.info("BookActions - list(): " + action.toString());
        }
        return results;
    }

    public static List<BookAction> listBorrows() {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression()
                .withFilterExpression("#a = :v1")
                .withExpressionAttributeNames(singletonMap("#a", "action"))
                .withExpressionAttributeValues(singletonMap(":v1", new AttributeValue().withS("BORROW")));

        List<BookAction> results = mapper.scan(BookAction.class, scanExp);
        for (BookAction action : results) {
            logger.info("BookActions - listBorrows(): " + action.toString());
        }
        return results;
    }

    public static List<BookAction> listGiveBacks() {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression()
                .withFilterExpression("#a = :v1")
                .withExpressionAttributeNames(singletonMap("#a", "action"))
                .withExpressionAttributeValues(singletonMap(":v1", new AttributeValue().withS("GIVE_BACK")));

        List<BookAction> results = mapper.scan(BookAction.class, scanExp);
        for (BookAction action : results) {
            logger.info("BookActions - listGiveBacks(): " + action.toString());
        }
        return results;
    }

    public static Optional<BookAction> get(String id) {
        Map<String, AttributeValue> av = singletonMap(":v1", new AttributeValue().withS(id));

        DynamoDBQueryExpression<BookAction> queryExp = new DynamoDBQueryExpression<BookAction>()
                .withKeyConditionExpression("action = :v1")
                .withExpressionAttributeValues(av);

        List<BookAction> result = mapper.query(BookAction.class, queryExp);

        BookAction action;
        if (!result.isEmpty()) {
            action = result.get(0);
            logger.info("BookActions - get(): action - " + action.toString());
            return Optional.of(action);
        } else {
            logger.info("BookActions - get(): action - Not Found.");
            return Optional.empty();
        }
    }

    public void save() {
        logger.info("BookActions - save(): " + toString());
        mapper.save(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAction that = (BookAction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookId, that.bookId) &&
                Objects.equals(person, that.person) &&
                action == that.action &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, person, action, timestamp);
    }

    @Override
    public String toString() {
        return "BookAction{" +
                "id='" + id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", person='" + person + '\'' +
                ", action=" + action +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}