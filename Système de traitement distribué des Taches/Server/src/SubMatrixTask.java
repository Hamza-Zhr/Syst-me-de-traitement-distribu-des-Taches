
import java.io.Serializable;
import java.util.UUID;

public class SubMatrixTask implements Serializable {
    private String clientId; // The identifier for the client
    private final UUID taskId; // This is the unique ID for each Task instance
    private String operation;
    private int taskIndex;
    private int[][] subMatrixA;
    private int[][] subMatrixB;
    private int[][] taskResult;

    public SubMatrixTask(UUID taskId, String clientId, String opeartion,
            int[][] subMatrixA, int[][] subMatrixB, int taskIndex) {
        this.clientId = clientId;
        this.taskId = taskId;
        this.operation = opeartion;
        this.taskIndex = taskIndex;
        this.subMatrixA = subMatrixA;
        this.subMatrixB = subMatrixB;
        this.taskResult = null;
    }

    public void execute() {
        switch (operation) {
            case "ADD":
                this.taskResult = AddOperation(subMatrixA, subMatrixB);
                break;

            case "SUBTRACT":
                this.taskResult = SubtractOperation(subMatrixA, subMatrixB);
                break;

            case "MULTIPLY":
                this.taskResult = MultiplyOperation(subMatrixA, subMatrixB);
                break;
        }
    }

    public int[][] AddOperation(int[][] subMatrixA, int[][] subMatrixB) {
        int rows = subMatrixA.length;
        int columns = subMatrixA[0].length;

        int[][] result = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = subMatrixA[i][j] + subMatrixB[i][j];
            }
        }
        return result;
    }

    public int[][] SubtractOperation(int[][] subMatrixA, int[][] subMatrixB) {
        int rows = subMatrixA.length;
        int columns = subMatrixA[0].length;

        int[][] result = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = subMatrixA[i][j] - subMatrixB[i][j];
            }
        }
        return result;
    }

    public int[][] MultiplyOperation(int[][] matrixA, int[][] matrixB) {
        int rowsA = matrixA.length;
        int columnsA = matrixA[0].length;
        int columnsB = matrixB[0].length;

        int[][] result = new int[rowsA][columnsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < columnsB; j++) {
                for (int k = 0; k < columnsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result;
    }

    public void viewTasks() {
        System.out.println("---------- View my tasks ---------");
        System.out.println("\n> List the rows from Matrice A");
        for (int i = 0; i < subMatrixA.length; i++) {
            System.out.print("[ ");
            for (int j = 0; j < subMatrixA[i].length; j++) {
                System.out.print(subMatrixA[i][j] + " ");
            }
            System.out.print("]");
            System.out.println();
        }

        if (operation == "Multiplication")
            System.out.println("\n> List the columns from Matrice B");
        else
            System.out.println("\n> List the rows from Matrice B");

        for (int i = 0; i < subMatrixB.length; i++) {
            System.out.print("[ ");
            for (int j = 0; j < subMatrixB[i].length; j++) {
                System.out.print(subMatrixB[i][j] + " ");
            }
            System.out.print("]");
            System.out.println();
        }

    }

    public void viewResult() {
        System.out.println("\n--- View my results for my task \"" + operation + "\" ---");
        for (int i = 0; i < taskResult.length; i++) {
            System.out.print("[ ");
            for (int j = 0; j < taskResult[i].length; j++) {
                System.out.print(taskResult[i][j] + " ");
            }
            System.out.print("]");
            System.out.println();
        }

    }

    public String getClientId() {
        return clientId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getOperation() {
        return operation;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public int[][] getSubMatrixA() {
        return subMatrixA;
    }

    public int[][] getSubMatrixB() {
        return subMatrixB;
    }

    public int[][] getTaskResult() {
        return taskResult;
    }

}
