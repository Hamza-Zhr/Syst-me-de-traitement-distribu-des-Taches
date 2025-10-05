
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSlave implements Runnable {
    private ConcurrentHashMap<Socket, ObjectOutputStream> listSlaveConnected;
    private ConcurrentHashMap<Socket, Boolean> unavailableSlaves;
    private ConcurrentHashMap<TaskResult, UUID> taskResult;

    private int port;

    public ServerSlave(int port) {
        this.port = port;
        this.listSlaveConnected = new ConcurrentHashMap<>();
        this.unavailableSlaves = new ConcurrentHashMap<>();
        this.taskResult = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        Socket socket = null;

        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("> Waiting for slave connections...");
                System.out.println("----------------------------------------------");

                while (true) {
                    socket = serverSocket.accept();
                    System.out.println("» A new slave has connected!");

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    listSlaveConnected.put(socket, oos);
                    SlaveHandler slavetHandler = new SlaveHandler(socket, unavailableSlaves, taskResult,
                            listSlaveConnected);
                    new Thread(slavetHandler).start();
                }
            }

        } catch (IOException e) {

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    public int getPort() {
        return port;
    }

    public ConcurrentHashMap<TaskResult, UUID> getTaskResult() {
        return taskResult;
    }

    public ConcurrentHashMap<Socket, ObjectOutputStream> getListSlaveConnected() {
        return listSlaveConnected;
    }

    public ConcurrentHashMap<Socket, Boolean> getUnavailableSlaves() {
        return unavailableSlaves;
    }

}
