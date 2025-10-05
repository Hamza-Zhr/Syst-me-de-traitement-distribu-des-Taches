import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.imageio.ImageIO;

public class FilterServer extends UnicastRemoteObject implements FilterOperation {
    public FilterServer() throws RemoteException {
        super();
    }

    @Override
    public int[][] applyFilter(int[][] imageMatrix, String filterType, float intensity) throws RemoteException {
        ImageFilterProcessor imageProcessor = new ImageFilterProcessor();

        switch (filterType) {
            case "INVERT":
                return imageProcessor.applyInvertFilter(imageMatrix);
            case "NOISE":
                return imageProcessor.applyNoiseFilter(imageMatrix, intensity);
            case "EDGE":
                return imageProcessor.applyEdgeDetectionFilter(imageMatrix, intensity);
            case "SHARPEN":
                return imageProcessor.applySharpenFilter(imageMatrix, intensity);
            case "GRAYSCALE":
                return imageProcessor.applyGrayScaleFilter(imageMatrix);
            case "BRIGHTNESS":
                return imageProcessor.applyBrightnessFilter(imageMatrix, intensity);
            // Ajoutez d'autres cas pour chaque type de filtre que vous avez
            default:
                throw new IllegalArgumentException("Filter type not supported: " + filterType);
        }
    }

    @Override
    public void saveFiltredImage(int[][] filteredMatrix, String outputFilePath) {
        int width = filteredMatrix.length;
        int height = filteredMatrix[0].length;

        BufferedImage image;

        // Create a BufferedImage based on the ARGB values
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = filteredMatrix[x][y];
                image.setRGB(x, y, argb);
            }
        }

        try {
            // Split the outputFilePath into folderName and outputFileName
            String[] parts = outputFilePath.split("\\\\");
            String folderName = parts[0];
            String outputFileName = parts[1];

            String folderPath = "src/images/" + folderName;
            File folder = new File(folderPath);

            // Create the folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String outputPath = folderPath + "/" + outputFileName + ".jpg";
            File outputImage = new File(outputPath);

            // Write the image as a PNG file
            ImageIO.write(image, "png", outputImage);
            System.out.println("» Filtered image saved successfully: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error saving filtered image: " + e.getMessage());
        }
    }

}
