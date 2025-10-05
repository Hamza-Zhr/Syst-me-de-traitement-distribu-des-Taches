
import java.io.Serializable;
import java.util.UUID;

public class MatrixTask implements Serializable {
    private final UUID taskId; // This is the unique ID for each Task instance
    private int[][] matrixA;
    private int[][] matrixB;
    private String operation;

    public MatrixTask(int[][] matrixA, int[][] matrixB, String operation) {
        this.taskId = UUID.randomUUID(); // Generate a unique UUID
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.operation = operation;
    }

    public int[][] getMatrixA() {
        return matrixA;
    }

    public int[][] getMatrixB() {
        return matrixB;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getOperation() {
        return operation;
    }

}
