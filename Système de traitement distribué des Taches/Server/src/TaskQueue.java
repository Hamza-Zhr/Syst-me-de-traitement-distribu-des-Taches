import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
    private BlockingQueue<ClientTask> taskQueue = new LinkedBlockingQueue<>();

    public void enqueueTask(ClientTask clientTask) {
        try {
            taskQueue.put(clientTask); // This is a blocking operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error enqueuing task: " + e.getMessage());
        }
    }

    public ClientTask dequeueTask() {
        try {
            return taskQueue.take(); // This is a blocking operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error dequeuing task: " + e.getMessage());
            return null;
        }
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }
}
