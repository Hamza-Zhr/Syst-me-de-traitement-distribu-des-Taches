import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.Naming;

public class TaskProcessor {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private FilterOperation filterServer;

    public TaskProcessor(Socket socket) throws IOException {
        this.socket = socket;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void connectToRMI() {
        try {
            // Remplacez "FilterServer" par le nom réel du serveur RMI des filtres
            filterServer = (FilterOperation) Naming.lookup("//localhost/FilterServer");
            System.out.println("» Connected to RMI server.");

        } catch (Exception e) {
            System.err.println("Error connecting to RMI server: " + e.getMessage());
        }
    }

    public void listenForTasks() {
        try {
            while (true) {
                // Lisez les tâches du flux d'entrée (peut être une tâche matricielle ou de
                // filtre)
                Object taskObject = objectInputStream.readObject();

                if (taskObject instanceof SubMatrixTask) {
                    // Si c'est une tâche matricielle, traitez-la comme avant
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    handleMatrixTask((SubMatrixTask) taskObject);
                } else if (taskObject instanceof SubFilterTask) {

                    // Si c'est une tâche de filtre, traitez-la via RMI
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    handleFilterTask((SubFilterTask) taskObject);
                }
            }
        } catch (Exception e) {
            close();
            System.out.println("\nServer is Closed!");
        }
    }

    private void handleMatrixTask(SubMatrixTask subMatrixTask) {

        try {
            System.out.println("\n» Slave processing Matrix for Client ID: " + subMatrixTask.getClientId()
                    + ", Operation Type: " + subMatrixTask.getOperation() + ", Task arrangement: "
                    + subMatrixTask.getTaskIndex());

            subMatrixTask.viewTasks();
            subMatrixTask.execute();
            subMatrixTask.viewResult();

            TaskResult taskResult = new TaskResult(subMatrixTask.getClientId(), subMatrixTask.getTaskId(),
                    "MATRIX_OPERATION", subMatrixTask.getTaskResult(),
                    subMatrixTask.getTaskIndex());

            objectOutputStream.writeObject(taskResult);
            objectOutputStream.reset(); // Reset the stream after each write
        } catch (Exception e) {
            close();
            System.out.println("\nServer is Closed!");
        }

    }

    private void handleFilterTask(SubFilterTask subFilterTask) {
        try {
            // Utilisez RMI pour traiter la tâche de filtre
            System.out.println("\n» Slave processing image for Client ID: " + subFilterTask.getClientID()
                    + ", Filter Type: " + subFilterTask.getFilterType() + ", Task arrangement: "
                    + subFilterTask.getIndexTask());

            int[][] result = filterServer.applyFilter(subFilterTask.getSubMatrix(), subFilterTask.getFilterType(),
                    subFilterTask.getIntensityValue());
            String fileName = "Client_" + subFilterTask.getClientID() + "\\Image_" + subFilterTask.getFilterType()
                    + "_" + subFilterTask.getIndexTask();
            filterServer.saveFiltredImage(result, fileName);

            // String outputPath = "C:\\Users\\mrhos\\OneDrive - hossam
            // dev\\Desktop\\TaskClient_demo\\Server\\src\\images\\"
            // + fileName;

            String outputPath = "C:\\Users\\AzComputer\\Desktop\\TaskClient_demo\\Server\\src\\images"
                    + fileName;

            System.out.println("\nFiltered image saved successfully with Client ID: " + subFilterTask.getClientID());
            System.out.println("» Click on the image path: \"" + outputPath + "\"");

            TaskResult taskResult = new TaskResult(subFilterTask.getClientID(), subFilterTask.getTaskID(),
                    "IMAGE_MATRIX", result,
                    subFilterTask.getIndexTask());

            objectOutputStream.writeObject(taskResult);
            objectOutputStream.reset(); // Reset the stream after each write

        } catch (IOException e) {
            System.err.println("Error handling filter task: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
