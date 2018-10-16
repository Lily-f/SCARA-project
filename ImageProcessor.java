import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageProcessor {

    //fields
    private static int THRESHOLD = 200;

    //constructor
    public ImageProcessor(){
        int[][] image = storeImage();

        //if image is not null, find the edges
        if(image != null) {
            int[][] horizontal = findHorizontalEdges(image);
            int[][] vertical = findVerticalEdges(image);
            int[][] combinedKernels = combineKernels(horizontal, vertical);
        }
    }


    //loads ppm image and save in array as grayscale
    private int[][] storeImage() {
        try{
            //get user input on what the file to load is called
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the image filename: ");
            String filename = input.next();

            //create a 2d array, and fill it with all the values in the image file.
            Scanner scan = new Scanner(new File(filename));
            if(scan.hasNext()){
                scan.next();    //is the file type
                int numberOfCols = scan.nextInt();
                int numberOfRows = scan.nextInt();
                int maxValue = scan.nextInt();
                int[][] image = new int[numberOfCols][numberOfRows];

                //find luminosity value based on human perception and store in 2d array
                for(int col = 0; col < numberOfCols; col ++){
                    for(int row = 0; row < numberOfRows; row ++){
                        double r = scan.nextInt() * 0.587 / maxValue;
                        double g = scan.nextInt() * 0.299 / maxValue;
                        double b = scan.nextInt() * 0.114 / maxValue;
                        image[col][row] = (int) ((r+g+b) * 255);
                    }
                }
                System.out.println("Image has been saved!");
                return image;
            }

            //return null if the file to open was empty
            System.out.println("File was empty!");
            return null;
        }
        //catch any IO errors when reading the file
        catch (IOException e){
            System.out.println("Error saving image: " + e);
            return null;
        }
    }


    //finds the horizontal edges in a given image. return the image
    private int[][] findHorizontalEdges(int[][] image){

        //create array to hold new values
        int[][] horizontal = new int[image.length][image[0].length];

        //run through all of the pixels in the image
        for(int row = 0; row < image.length; row ++) {
            for(int col = 0; col < image[0].length; col ++){

                //make sure that the kernel is not used on the edge of the image
                if(row > 0 && col > 0 && row < image.length-1 && col < image[0].length-1){
                    double value = (image[row-1][col-1]) + (2*image[row-1][col]) + (image[row-1][col+1])
                            + (-1*image[row+1][col-1]) + (-2*image[row+1][col]) + (-1*image[row+1][col+1]);

                    horizontal[row][col] = (int)value;
                }
                //else 0 (can't run kernel)
                else{
                    horizontal[row][col] = 0;
                }
                //System.out.print(horizontal[row][col] + " ");
            }
            //System.out.println("");
        }
        System.out.println("Horizontal kernel done!");
        return horizontal;
    }


    //finds the edges in a given image
    private int[][] findVerticalEdges(int[][] image){

        int[][] vertical = new int[image.length][image[0].length];

        //run through all of the pixels in the image
        for(int row = 0; row < image.length; row ++) {
            for(int col = 0; col < image[0].length; col ++){

                //make sure that the kernel is not used on the edge of the image
                if(row > 0 && col > 0 && row < image.length-1 && col < image[0].length-1){
                    double value = (-1*image[row-1][col-1]) + (image[row-1][col+1]) + (-2*image[row][col-1])
                                    + (2*image[row][col+1]) + (-1*image[row+1][col-1]) + (image[row+1][col+1]);

                    vertical[row][col] = (int)value;
                }
                //else 0 (can't run kernel)
                else{
                    vertical[row][col] = 0;
                }
                //System.out.print(vertical[row][col] + " ");
            }
            //System.out.println("");
        }
        System.out.println("vertical kernel done!");
        return vertical;
    }


    //combine 2 given 2d arrays, for combining edge finding kernals
    private int[][] combineKernels(int[][] horizontal, int[][] vertical){
        //create new 2d array for final image
        int[][] image = new int[horizontal.length][horizontal[0].length];

        //loop through all the pixels, combining the values using pythagoras
        for(int row = 0; row < image.length; row ++){
            for(int col = 0; col < image[0].length; col ++){
                image[row][col] = (int) Math.sqrt( Math.pow(horizontal[row][col], 2) + Math.pow(vertical[row][col], 2));
                System.out.print(image[row][col] + " ");
            }
            System.out.println("");
        }
        System.out.println("kernels combined!");
        return image;
    }
}
