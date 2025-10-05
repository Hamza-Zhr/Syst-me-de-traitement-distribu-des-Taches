
import java.io.Serializable;

public class ClientTask implements Serializable {
    private Object task;
    private String clientId; // The identifier for the client

    public ClientTask(Object task, String clientId) {
        this.task = task;
        this.clientId = clientId;
    }

    // Getters and setters for task and clientId
    public Object getTask() {
        return task;
    }

    public String getClientId() {
        return clientId;
    }
}
