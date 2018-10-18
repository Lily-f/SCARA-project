import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageProcessor {

    //fields
    private int[][] edgeValues;

    //create arrayList to hold all coordinates for edges
    private List<double[]> cannyCoords = new ArrayList<>();

    //constructor
    public ImageProcessor(){
        int[][] image = storeImage();

        //if image is not null, find the edges
        if(image != null) {
            int[][] horizontal = findHorizontalEdges(image);
            int[][] vertical = findVerticalEdges(image);
            edgeValues = combineKernels(horizontal, vertical);

            //fill the arraylist with coords for points.
            for(int row = 1; row < edgeValues.length - 1; row ++){
                for(int col = 1; col < edgeValues[0].length - 1; col ++){
                    findEdges(row, col);
                }
            }

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


    //finds edges from a given image
    private void findEdges(int row, int col){

        int threshold1 = 200;
        int threshold2 = 200;
        int minLineLength = 30; //number of pixels needed before a line will be drawn
        int currentLineLength = 0;
        List<double[]> edgeCoords = new ArrayList<>();  //current coords to copy to cannycoords if line is big enough

        //check if pixel is strong edge, to start new edge to draw
        if(edgeValues[row][col] > threshold1){
            //System.out.println("Edge Started");

            //start following the edge
            boolean following = true;
            int rowDirection = 0;
            int colDirection = 0;

            while(following) {
                //System.out.println("following edge! " + (row + rowDirection) + " " + (col + colDirection));

                //add coordinates to arrayList, and increase line length
                edgeCoords.add(new double[]{row + rowDirection, col + colDirection});
                currentLineLength ++;

                if(row + rowDirection == 0 || col + colDirection == 0){
                    return;
                }

                //find max value of adjacent pixels. go to this max
                int max = Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(
                        edgeValues[row+1+rowDirection][col+1+colDirection],  edgeValues[row+1+rowDirection][col+colDirection]),
                        edgeValues[row+1+rowDirection][col-1+colDirection]), edgeValues[row+rowDirection][col+1+colDirection]),
                        edgeValues[row+rowDirection][col - 1+colDirection]), edgeValues[row-1+rowDirection][col+1+colDirection]),
                        edgeValues[row-1+rowDirection][col+colDirection]),   edgeValues[row-1+rowDirection][col-1+colDirection]);

                //System.out.println("Max pixel value: " + max);

                //move to adjacent pixel with max value
                if (edgeValues[row - 1 + rowDirection][col - 1 + colDirection] == max) {
                    rowDirection --;
                    colDirection --;
                }
                else if (edgeValues[row - 1 + rowDirection][col + colDirection] == max) {
                    rowDirection --;
                }
                else if (edgeValues[row - 1 + rowDirection][col + 1 + colDirection] == max) {
                    rowDirection --;
                    colDirection ++;
                }
                else if (edgeValues[row + rowDirection][col - 1 + colDirection] == max) {
                    colDirection --;
                }
                else if (edgeValues[row + rowDirection][col + 1 + colDirection] == max) {
                    colDirection ++;
                }
                else if (edgeValues[row + 1 + rowDirection][col - 1 + colDirection] == max) {
                    rowDirection ++;
                    colDirection --;
                }
                else if (edgeValues[row + 1 + rowDirection][col + colDirection] == max) {
                    rowDirection ++;
                }
                else if (edgeValues[row + 1 + rowDirection][col + 1 + colDirection] == max) {
                    rowDirection --;
                    colDirection ++;
                }
                else if (edgeValues[row + 1 + rowDirection][col + 1 + colDirection] == max) {
                    rowDirection ++;
                    colDirection ++;
                }

                //check max value against threshold2. if less terminate edge
                if (max < threshold2) {
                    following = false;
                    //System.out.println("Edge dropped!");
                }

                //erase current pixel from image
                edgeValues[row + rowDirection][col + colDirection] = 0;
            }

            //check line was big enough to copy over to canny coords
            if(currentLineLength >= minLineLength){
                System.out.println("Line was long enough! " + currentLineLength);
                for(double[] coord : edgeCoords){
                    cannyCoords.add(new double[]{coord[0], coord[1]});
                }
            }

        }
    }

    //get the list of canny Coordinates
    public List<double[]> getCannyCoords() {
        return cannyCoords;
    }
}
