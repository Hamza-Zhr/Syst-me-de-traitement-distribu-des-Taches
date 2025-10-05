
import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// import TasksManagement.TaskResult;

public class SlaveHandler implements Runnable {

    private Socket socket;
    private ConcurrentHashMap<TaskResult, UUID> taskResult;
    private ConcurrentHashMap<Socket, Boolean> unavailableSlaves;
    private ObjectInputStream objectInputStream;
    private ConcurrentHashMap<Socket, ObjectOutputStream> listSlaveConnected;

    public SlaveHandler(Socket socket, ConcurrentHashMap<Socket, Boolean> unavailableSlaves,
            ConcurrentHashMap<TaskResult, UUID> taskResult,
            ConcurrentHashMap<Socket, ObjectOutputStream> listSlaveConnected) throws IOException {

        this.unavailableSlaves = unavailableSlaves;
        this.socket = socket;
        this.taskResult = taskResult;
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.listSlaveConnected = listSlaveConnected;
    }

    @Override
    public void run() {
        try {
            TaskResult result;
            while (true) {
                if ((result = (TaskResult) objectInputStream.readObject()) != null) {
                    unavailableSlaves.remove(socket);
                    taskResult.put(result, result.getTaskId());
                }
            }

        } catch (Exception e) {
            closeClientConnection();
        }
    }

    public void closeClientConnection() {
        try {
            if (listSlaveConnected.get(socket) != null) {
                listSlaveConnected.get(socket).close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }

            listSlaveConnected.remove(socket);

            System.out.println("The slave has been disconnected from the server");
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

}