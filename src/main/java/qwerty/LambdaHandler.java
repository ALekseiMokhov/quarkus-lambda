package qwerty;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@ApplicationScoped
public class LambdaHandler implements RequestHandler<S3Event, String> {
    private static final Logger LOGGER = Logger.getLogger(LambdaHandler.class);

    @Inject
    private final DynamoDbClient dynamoDB;

    public LambdaHandler(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            LOGGER.info(dynamoDB);
            S3EventNotificationRecord rec = event.getRecords().get(0);
            final String id = String.valueOf(Math.random() * (1000000000 - 2));
            final String value = rec.getEventName();
            final String counter = rec.getEventVersion();
            final DynamoDTO dynamoDTO = new DynamoDTO(id, counter, value);
            putRequest(dynamoDTO);
            return String.valueOf(dynamoDTO);
        } catch (Throwable error) {
            LOGGER.error("KABOOM!");
            throw new RuntimeException(error);
        }
    }

    public PutItemRequest putRequest(DynamoDTO dynamoDTO) {
        LOGGER.info("PUT REQUEST starts....");
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(dynamoDTO.getId()).build());
        item.put("value", AttributeValue.builder().s(dynamoDTO.getValue()).build());
        item.put("counter", AttributeValue.builder().s(dynamoDTO.getCounter()).build());
        final PutItemRequest request = PutItemRequest.builder()
                .tableName("crud-demo")
                .item(item)
                .build();
        LOGGER.info(" REQUEST FINISHED");
        try {
            dynamoDB.putItem(request);
        } catch (Exception e) {
            LOGGER.error(" Can't persist data: " + e);
        } finally {
            LOGGER.info(" finally block");
        }
        return request;
    }
}
