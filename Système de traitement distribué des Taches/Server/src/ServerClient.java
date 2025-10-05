
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerClient implements Runnable {

    private ConcurrentHashMap<String, ObjectOutputStream> clientMap;
    private TaskQueue taskQueue;

    private int port;

    public ServerClient(int port) {
        this.port = port;
        this.clientMap = new ConcurrentHashMap<>();
        this.taskQueue = new TaskQueue();
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("> Waiting for client connections...");
                System.out.println("----------------------------------------------");

                while (true) {
                    socket = serverSocket.accept();
                    // if a Client connected to the Server
                    System.out.println("» A new client has connected!");

                    String clientId = "" + socket.getPort(); // Implement this method
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    clientMap.put(clientId, oos);

                    ClientHandler clientHandler = new ClientHandler(socket, clientId, clientMap, taskQueue);
                    new Thread(clientHandler).start();

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

    public ConcurrentHashMap<String, ObjectOutputStream> getClientMap() {
        return clientMap;
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

}
