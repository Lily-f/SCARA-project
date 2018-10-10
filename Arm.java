import org.omg.CORBA.MARSHAL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
public class Arm {

    //max and min values to stop arms locking
    //left  =1200
    //straight = 1500
    //right = //1800


    //straight vertical line 1850, 1250

    //fields
    private int penDown = 1500;
    private int penUp = 1500;
    private int motorLeftX = 270;
    private int motorLeftY = 480;
    private int motorRightX = 400;
    private int motorRightY = 480;
    private int motorDistance = motorRightX - motorLeftX;  //unit = pixels
    private int armRadius = 230; //unit = pixels

    //constructor
    private Arm() {

        drawTriangle();

    }

    //draw a triangle
    private void drawTriangle() {

        //create a file for storing the pwm values
        try {
            PrintStream writer = new PrintStream(new FileOutputStream(new File("drawTriangle.txt")));

            //create an arraylist of coordinates (each coordinate is an array with an x and y value)
            List<double[]> coordinates = new ArrayList<>();
            coordinates.add( new double[]{290, 240});
            coordinates.add( new double[]{345, 180});
            coordinates.add( new double[]{420, 240});

            //print the values to the console, to validate them
            for(double[] coordinate : coordinates){
                System.out.println(coordinate[0] + ", " + coordinate[1]);

                double leftArmAngle = findLeftArmAngle(coordinate[0], coordinate[1]);
                double rightArmAngle = findRightArmAngle(coordinate[0], coordinate[1]);
                double leftPwmValue = leftAnglePwmConverter(leftArmAngle);
                double rightPwmValue = rightAnglePwmConverter(rightArmAngle);
                
                writer.println(leftPwmValue + " " + rightPwmValue);
            }

            writer.flush();
            writer.close();
        }catch(IOException e ){System.out.println("FileWrite IOExecption" + e);}

    }



    //find angle needed for right arm from tool position
    private double findRightArmAngle(double toolX, double toolY) {

        //distance between tool and motor
        double toolMotorDistance = Math.sqrt( Math.pow(toolX - motorRightX, 2) + Math.pow(toolY - motorRightY, 2));

        //midPoints
        double motorRightToolMidpointX = (toolX + motorRightX)/2;
        double motorRightToolMidpointY = (toolY + motorRightY)/2;

        //calculate distance between the midpoints and joints
        double midpointJointDistance = Math.sqrt( Math.pow(armRadius, 2) - Math.pow(toolMotorDistance, 2));

        //angle between motors and first pen coord
        double motorJointAngle = Math.acos((motorRightX - toolX)/motorDistance);

        //calculate joint positions for left join
        double jointX = motorRightToolMidpointX + midpointJointDistance * Math.sin(motorJointAngle);
        double jointY = motorRightToolMidpointY - midpointJointDistance * Math.cos(motorJointAngle);

        //calculate joint positions for right joint
        double angle = Math.atan( (jointY-motorRightY)/(jointX-motorRightX) );
        angle = 180 - angle;    //want outer angle on triangle of motor position and joint position

        return angle;
    }


    //find angle needed for left arm from tool position
    private double findLeftArmAngle(double toolX, double toolY){
        //distance between tool and motor
        double toolMotorDistance = Math.sqrt( Math.pow(toolX - motorRightX, 2) + Math.pow(toolY - motorRightY, 2));

        //midPoints
        double motorLeftToolMidpointX = (toolX + motorLeftX)/2;
        double motorLeftToolMidpointY = (toolY + motorLeftY)/2;

        //calculate distance between the midpoints and joints
        double midpointJointDistance = Math.sqrt( Math.pow(armRadius, 2) - Math.pow(toolMotorDistance, 2));

        //angle between motors and first pen coord
        double motorJointAngle = Math.acos((motorLeftX - toolX)/motorDistance);

        //calculate joint positions for left join
        double jointX = motorLeftToolMidpointX - midpointJointDistance * Math.sin(motorJointAngle);
        double jointY = motorLeftToolMidpointY + midpointJointDistance * Math.cos(motorJointAngle);

        //calculate joint positions for right joint
        double angle = Math.atan( (jointY-motorLeftY)/(jointX-motorLeftX) );
        angle = 180 - angle;    //want outer angle on triangle of motor position and joint position

        return angle;
    }


    //converts angle to PWM for right arm
    private double rightAnglePwmConverter(double angle){
        double constant = 2343;
        double gradient = -11.5;
        return gradient * angle + constant;
    }

    //converts angle to PWM for left arm
    private double leftAnglePwmConverter(double angle){
        double constant = 2807;
        double gradient = -11.5;
        return gradient * angle + constant;
    }

    //Main
    public static void main(String[] args) {
        new Arm();
    }

    //steps that we want
    //give tool pos
    //find joint pos
    //find angle
    //get pwm from angle

}