import java.io.IOException;
import java.net.Socket;

public class Slave {

    public static void main(String[] args) throws IOException {
        // if (args.length != 2) {
        // System.out.println("Usage: java Slave <server_host> <port_number>");
        // return;
        // }

        // String serverHost = args[0];
        // int port;
        // try {
        // port = Integer.parseInt(args[1]);
        // } catch (NumberFormatException e) {n
        // System.out.println("Error: Port number must be an integer.");
        // return;
        // }

        String serverHost = "localhost";
        int port = 8081;

        Socket socket = null;
        try {
            socket = new Socket(serverHost, port);

            System.out.println("\n» You are successfully connected to the server");
            TaskProcessor taskProcessor = new TaskProcessor(socket);
            taskProcessor.connectToRMI();
            taskProcessor.listenForTasks();
        } catch (Exception e) {

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

}
