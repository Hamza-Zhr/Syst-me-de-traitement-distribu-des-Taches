import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FilterOperation extends Remote {
    int[][] applyFilter(int[][] imageMatrix, String filterType, float intensity) throws RemoteException;

    public void saveFiltredImage(int[][] FiltredMatrix, String outputPath)
            throws RemoteException;

}
