import java.io.Serializable;

public class MatrixMessage implements Serializable {
    private String messageType;
    private int[][] matrix;

    public MatrixMessage(String messageType, int[][] matrix) {
        this.messageType = messageType;
        this.matrix = matrix;
    }

    public String getMessageType() {
        return messageType;
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
