package ee.MinuTood.Quest.registration.system.userInterface.dtos;

public class ApiResponse {
    private String message;
    private long eventId;


    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse() {
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
