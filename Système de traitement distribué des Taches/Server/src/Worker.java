
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Worker implements Runnable {

    private TaskQueue taskQueue;
    private ConcurrentHashMap<Socket, ObjectOutputStream> slavesList;
    private ConcurrentHashMap<TaskResult, UUID> taskResult;
    private ConcurrentHashMap<String, ObjectOutputStream> clientMap;
    private ConcurrentHashMap<Socket, Boolean> unavailableSlaves; // Updated to ConcurrentHashMap

    public Worker(TaskQueue taskQueue,
            ConcurrentHashMap<Socket, ObjectOutputStream> slavesList,
            ConcurrentHashMap<Socket, Boolean> unavailableSlaves,
            ConcurrentHashMap<TaskResult, UUID> taskResult,
            ConcurrentHashMap<String, ObjectOutputStream> clientMap) {
        this.taskQueue = taskQueue;
        this.slavesList = slavesList;
        this.unavailableSlaves = unavailableSlaves;
        this.taskResult = taskResult;
        this.clientMap = clientMap;
    }

    @Override
    public void run() {
        while (true) {
            ClientTask clientTask = taskQueue.dequeueTask();
            if (clientTask != null) {
                long threadId = Thread.currentThread().threadId();
                System.out.println("> The worker with Thread ID " + threadId + " handles a task...");

                // Vérification de type pour déterminer si la tâche est une instance de
                // MatrixTask
                if (clientTask.getTask() instanceof MatrixTask) {
                    MatrixTask matrixTask = (MatrixTask) clientTask.getTask();

                    distributeTaskToSlaves(clientTask.getClientId(), matrixTask);
                } else if (clientTask.getTask() instanceof FilterTask) {
                    FilterTask filterTask = (FilterTask) clientTask.getTask();

                    // Obtenez le nom du fichier image à partir de la tâche
                    String imageFileName = filterTask.getImageFileName();
                    // String imageFileName = "C:\\Users\\mrhos\\OneDrive - hossam
                    // dev\\Desktop\\TaskClient_demo\\Server\\src\\test.png";

                    // Chargez l'image correspondante et convertissez-la en matrice
                    int[][] imageMatrix = loadImageAndConvertToMatrix(imageFileName);
                    // printMatrix(imageMatrix);

                    filterTask.setImageMatrix(imageMatrix);

                    distributeTaskToSlaves(clientTask.getClientId(), filterTask);

                }

            }
        }
    }

    private void distributeTaskToSlaves(String clientID, MatrixTask matrixTask) {
        try {
            int numberTasks = matrixTask.getMatrixA().length;
            int numberSlavesAvailable = checkAvailableSlaves(numberTasks);

            List<Integer> tasksPerSlave = calculateTasksForEachSlave(numberSlavesAvailable, numberTasks);
            int tasksToDistribute = tasksPerSlave.size();
            int currentSlaveIndex = 0;
            int taskIndex = 0;

            for (Map.Entry<Socket, ObjectOutputStream> entry : slavesList.entrySet()) {
                Socket slaveSocket = entry.getKey();
                ObjectOutputStream oos = entry.getValue();

                if (unavailableSlaves.containsKey(slaveSocket))
                    continue;
                unavailableSlaves.put(slaveSocket, true);

                SubMatrixTask subMatrixTask = createSubMatrixTask(clientID, matrixTask, tasksPerSlave.get(0),
                        currentSlaveIndex, taskIndex);

                currentSlaveIndex += tasksPerSlave.remove(0);
                taskIndex++;

                oos.writeObject(subMatrixTask);
                oos.reset();

                if (tasksPerSlave.isEmpty())
                    break;
            }

            CheckResult checkResult = new CheckResult(taskResult, clientMap, matrixTask.getTaskId(),
                    tasksToDistribute);

            new Thread(checkResult).start();
        } catch (Exception e) {
            System.err.println("Error in distributeMatrixTaskToSlaves: " + e.getMessage());
        }
    }

    private void distributeTaskToSlaves(String clientID, FilterTask filtreTask) {

        try {

            int numberTasks = filtreTask.getImageMatrix().length;
            int numberSlavesAvailable = checkAvailableSlaves(numberTasks);

            List<Integer> tasksPerSlave = calculateTasksForEachSlave(numberSlavesAvailable, numberTasks);
            int tasksToDistribute = tasksPerSlave.size();
            int currentSlaveIndex = 0;
            int taskIndex = 0;

            for (Map.Entry<Socket, ObjectOutputStream> entry : slavesList.entrySet()) {
                Socket slaveSocket = entry.getKey();
                ObjectOutputStream oos = entry.getValue();

                if (unavailableSlaves.containsKey(slaveSocket))
                    continue;
                unavailableSlaves.put(slaveSocket, true);

                SubFilterTask subFilterTask = createSubFiltreTask(clientID, filtreTask, tasksPerSlave.get(0),
                        currentSlaveIndex, taskIndex);
                currentSlaveIndex += tasksPerSlave.remove(0);
                taskIndex++;

                oos.writeObject(subFilterTask);
                oos.reset();

                if (tasksPerSlave.isEmpty())
                    break;

            }

            CheckResult checkResult = new CheckResult(taskResult, clientMap, filtreTask.getTaskId(),
                    tasksToDistribute);
            new Thread(checkResult).start();

        } catch (Exception e) {
            System.err.println("Error in distributeFiltreTaskToSlaves: " + e.getMessage());
        }

    }

    private SubFilterTask createSubFiltreTask(String clientID, FilterTask filterTask, int tasksCount,
            int currentSlaveIndex, int taskIndex) {

        int[][] imageMatrix = filterTask.getImageMatrix();

        int[][] subMatrix = Arrays.copyOfRange(imageMatrix, currentSlaveIndex, currentSlaveIndex + tasksCount);

        return new SubFilterTask(clientID, filterTask.getTaskId(), filterTask.getFilterType(),
                filterTask.getIntensityValue(), subMatrix, taskIndex);
    }

    private int checkAvailableSlaves(int numberTasks) {

        int numberSlavesAvailable = slavesList.size() - unavailableSlaves.size();
        while (!(numberTasks <= numberSlavesAvailable || numberSlavesAvailable == slavesList.size())) {

            System.out.println("\n> The worker waits for the slaves to be available");
            numberSlavesAvailable = slavesList.size() - unavailableSlaves.size();
        }

        return numberSlavesAvailable;
    }

    private SubMatrixTask createSubMatrixTask(String clientID, MatrixTask matrixTask, int tasksCount,
            int currentSlaveIndex, int taskIndex) {

        int[][] subMatrixA = null;
        int[][] subMatrixB = null;

        int[][] matrixA = matrixTask.getMatrixA();
        int[][] matrixB = matrixTask.getMatrixB();

        subMatrixA = Arrays.copyOfRange(matrixA, currentSlaveIndex, currentSlaveIndex + tasksCount);

        if (matrixTask.getOperation().equals("MULTIPLY")) {
            subMatrixB = Arrays.copyOfRange(matrixB, 0, matrixB.length);

        } else {
            subMatrixB = Arrays.copyOfRange(matrixB, currentSlaveIndex, currentSlaveIndex + tasksCount);

        }

        // switch (matrixTask.getOperation()) {
        // case "ADD":
        // subMatrixB = Arrays.copyOfRange(matrixB, currentSlaveIndex, currentSlaveIndex
        // + tasksCount);
        // break;

        // case "SUBTRACT":

        // subMatrixB = Arrays.copyOfRange(matrixB, currentSlaveIndex, currentSlaveIndex
        // + tasksCount);
        // break;

        // case "MULTIPLY":

        // subMatrixB = Arrays.copyOfRange(matrixB, 0, matrixB.length);
        // break;
        // }

        return new SubMatrixTask(matrixTask.getTaskId(), clientID,
                matrixTask.getOperation(), subMatrixA, subMatrixB, taskIndex);
    }

    private List<Integer> calculateTasksForEachSlave(int availableSlaves, int totalTasks) {
        ArrayList<Integer> nbrTaskList = new ArrayList<>();
        if (totalTasks > availableSlaves) {

            // Case 1: Distribute tasks evenly among clients
            int tasksPerSlave = totalTasks / availableSlaves;
            int remainingTasks = totalTasks % availableSlaves;

            for (int i = 1; i <= availableSlaves; i++) {
                int tasksForThisSlave = tasksPerSlave + (i <= remainingTasks ? 1 : 0);
                nbrTaskList.add(tasksForThisSlave);
            }

        } else {

            for (int i = 1; i <= totalTasks; i++) {
                nbrTaskList.add(1);
            }
        }

        return nbrTaskList;
    }

    public static int[][] loadImageAndConvertToMatrix(String imageFileName) {
        try {
            System.out.println("\n> Attempting to load image from: " + imageFileName + "\n");

            // Check if the file exists
            File imageFile = new File(imageFileName);
            if (!imageFile.exists()) {
                System.err.println("Error: Image file not found.");
                return null;
            }

            // Read the image
            BufferedImage image = ImageIO.read(imageFile);

            // Get the dimensions of the image
            int width = image.getWidth();
            int height = image.getHeight();

            // Convert the image to a bidimensional array
            int[][] imageMatrix = new int[width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    imageMatrix[i][j] = image.getRGB(i, j);
                }
            }

            return imageMatrix;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void printMatrix(int[][] matrix) {
        try (FileWriter writer = new FileWriter("filtreMatrix.txt")) {
            // Write matrix dimensions to file
            writer.write("Matrix Dimensions: " + matrix.length + "x" + matrix[0].length + "\n");

            // Write matrix values to file
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    writer.write(matrix[i][j] + " ");
                }
                writer.write("---------\n");
            }

            System.out.println("Matrix has been saved to: filtreMatrix.txt");
        } catch (IOException e) {
            System.err.println("Error saving matrix to file: " + e.getMessage());
        }
    }

}
