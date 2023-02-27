package qwerty;

public class DynamoDTO
{
 private   final String id;
    private final String counter;
    private final String value;

    public String getCounter() {
        return counter;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public DynamoDTO(String id, String counter, String value) {
        this.id = id;
        this.counter = counter;
        this.value = value;
    }

    @Override
    public String toString() {
        return "DynamoDTO{" +
                "counter='" + counter + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
