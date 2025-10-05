
import java.io.Serializable;
import java.util.UUID;

public class TaskResult implements Serializable {
    private String clientId; // The identifier for the client
    private UUID taskId;
    private String resultType;
    private int TaskIndex;
    private int[][] result;

    public TaskResult(String clientId, UUID taskId, String resultType, int[][] result, int TaskIndex) {
        this.TaskIndex = TaskIndex;
        this.taskId = taskId;
        this.resultType = resultType;
        this.clientId = clientId;
        this.result = result;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getClientId() {
        return clientId;
    }

    public int getTaskIndex() {
        return TaskIndex;
    }

    public int[][] getResult() {
        return result;
    }

    public String getResultType() {
        return resultType;
    }

}
