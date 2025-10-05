import java.util.Random;

public class ImageFilterProcessor {

    public int[][] applyInvertFilter(int[][] inputMatrix) {
        int width = inputMatrix.length;
        int height = inputMatrix[0].length;
        int[][] outputMatrix = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = inputMatrix[x][y];

                // Invert the color components
                int red = 255 - ((pixel >> 16) & 0xff);
                int green = 255 - ((pixel >> 8) & 0xff);
                int blue = 255 - (pixel & 0xff);

                // Create a new pixel with the inverted components and set the output pixel's
                // color
                int newPixel = (255 << 24) | (red << 16) | (green << 8) | blue;
                outputMatrix[x][y] = newPixel;
            }
        }

        return outputMatrix;
    }

    public int[][] applyGrayScaleFilter(int[][] imageMatrix) {
        int width = imageMatrix.length;
        int height = imageMatrix[0].length;
        int[][] grayscaleMatrix = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int argb = imageMatrix[i][j];

                // Extract individual color components (alpha, red, green, blue)
                int alpha = (argb >> 24) & 0xff;
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;

                // Convert to grayscale using the luminosity formula
                int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                // Create a new grayscale ARGB value
                int grayscaleArgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

                grayscaleMatrix[i][j] = grayscaleArgb;
            }
        }

        return grayscaleMatrix;
    }

    public int[][] applyNoiseFilter(int[][] image, float noiseLevel) {
        int width = image.length;
        int height = image[0].length;

        Random random = new Random();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int pixel = image[i][j];
                if (random.nextDouble() < noiseLevel / 2) {
                    pixel = 0xffffffff;
                } else if (random.nextDouble() < noiseLevel) {
                    pixel = 0xff000000;
                }
                image[i][j] = pixel;
            }
        }
        return image;
    }

    public int[][] applyBrightnessFilter(int[][] pixels, float density) {
        int width = pixels.length;
        int height = pixels[0].length;
        int[][] output = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the current pixel color
                int argb = pixels[x][y];

                // Extract the alpha, red, green, and blue components
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                // Increase the brightness by the specified amount
                red += density;
                green += density;
                blue += density;

                // Clip the values to the range of 0-255
                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                // Combine the components back into a single pixel color
                output[x][y] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        }

        return output;
    }

    public int[][] applySharpenFilter(int[][] pixels, float sharpenIntensity) {
        int width = pixels.length;
        int height = pixels[0].length;
        int[][] output = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Extract the RGB components
                int alpha = (pixels[x][y] >> 24) & 0xFF;
                int red = (pixels[x][y] >> 16) & 0xFF;
                int green = (pixels[x][y] >> 8) & 0xFF;
                int blue = pixels[x][y] & 0xFF;

                // Apply sharpen filter logic based on the sharpenIntensity parameter for each
                // channel
                int centerValueR = (int) (red + (sharpenIntensity * (red - green)));
                int centerValueG = (int) (green + (sharpenIntensity * (green - blue)));
                int centerValueB = (int) (blue + (sharpenIntensity * (blue - green)));

                // Ensure RGB values stay within the valid range (0-255)
                centerValueR = Math.max(0, Math.min(centerValueR, 255));
                centerValueG = Math.max(0, Math.min(centerValueG, 255));
                centerValueB = Math.max(0, Math.min(centerValueB, 255));

                // Combine the RGB components back into a single pixel color
                output[x][y] = (alpha << 24) | (centerValueR << 16) | (centerValueG << 8) | centerValueB;
            }
        }

        return output;
    }

    public int[][] applyEdgeDetectionFilter(int[][] imageMatrix, float intensity) {
        int width = imageMatrix.length;
        int height = imageMatrix[0].length;

        int[][] filteredImage = new int[width][height];

        // Sobel operator kernel for edge detection
        int[][] sobelX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        int[][] sobelY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                // Apply the Sobel operator to each channel separately
                int gradientXRed = applyKernel(imageMatrix, sobelX, i, j, 'R');
                int gradientYRed = applyKernel(imageMatrix, sobelY, i, j, 'R');
                int gradientXGreen = applyKernel(imageMatrix, sobelX, i, j, 'G');
                int gradientYGreen = applyKernel(imageMatrix, sobelY, i, j, 'G');
                int gradientXBlue = applyKernel(imageMatrix, sobelX, i, j, 'B');
                int gradientYBlue = applyKernel(imageMatrix, sobelY, i, j, 'B');

                // Combine gradients to get the magnitude for each channel
                int magnitudeRed = (int) Math.sqrt(gradientXRed * gradientXRed + gradientYRed * gradientYRed);
                int magnitudeGreen = (int) Math.sqrt(gradientXGreen * gradientXGreen + gradientYGreen * gradientYGreen);
                int magnitudeBlue = (int) Math.sqrt(gradientXBlue * gradientXBlue + gradientYBlue * gradientYBlue);

                // Combine magnitudes and scale based on intensity
                int magnitude = (magnitudeRed + magnitudeGreen + magnitudeBlue) / 3;
                magnitude = (int) (magnitude * (int) intensity / 100.0);

                // Ensure the magnitude stays within the valid range (0-255)
                magnitude = Math.min(255, Math.max(0, magnitude));

                // Set the magnitude for the output pixel in each channel
                filteredImage[i][j] = (magnitude << 16) | (magnitude << 8) | magnitude
                        | (imageMatrix[i][j] & 0xFF000000);
            }
        }

        return filteredImage;
    }

    private int applyKernel(int[][] imageMatrix, int[][] kernel, int x, int y, char channel) {
        int result = 0;
        int kernelSize = kernel.length;

        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                int pixel = imageMatrix[x - 1 + i][y - 1 + j];
                int value;

                // Extract the specified channel from the pixel
                if (channel == 'R') {
                    value = (pixel >> 16) & 0xFF; // Red channel
                } else if (channel == 'G') {
                    value = (pixel >> 8) & 0xFF; // Green channel
                } else {
                    value = pixel & 0xFF; // Blue channel
                }

                result += kernel[i][j] * value;
            }
        }

        return result;
    }

}
