import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageProcessor {

    private int[][] image;

    //constructor
    public ImageProcessor(){
        storeImage();

    }


    //loads ppm image
    private void storeImage() {
        try{
            //get user input on what the file to load is called
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the image filename: ");
            String filename = input.next();
            input.close();

            //create a 2d array, and fill it with all the values in the image file.
            Scanner scan = new Scanner(new File(filename));
            if(scan.hasNext()){
                scan.next();    //is the file type
                int numberOfCols = scan.nextInt();
                int numberOfRows = scan.nextInt();
                int maxValue = scan.nextInt();
                image = new int[numberOfCols][numberOfRows];

                //store in the values to the array
                for(int col = 0; col < numberOfCols; col ++){
                    for(int row = 0; row < numberOfRows; row ++){
                        int r = scan.nextInt();
                        int g = scan.nextInt();
                        int b = scan.nextInt();
                        image[col][row] = (r+g+b)/3;
                    }
                    System.out.println();
                }
            }
        }catch (IOException e){
        }
    }


    //
    private void findEdges(){


    }
}
