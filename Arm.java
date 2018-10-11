/*
 * Arm.java
 *
 * Copyright 2018 Javrielle Domingo <domingjavr@love-a-coffee-cafe.ecs.vuw.ac.nz>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 *
 *
 */

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
    private int armRadius = 200; //unit = pixels

    //constructor
    private Arm() {

        //drawTriangle();
        drawLine();

    }

    //draw a triangle
    private void drawTriangle() {

        //create a file for storing the pwm values
        try {
            PrintStream writer = new PrintStream(new FileOutputStream(new File("drawTriangle.txt")));

            //create an arraylist of coordinates (each coordinate is an array with an x and y value)
            List<double[]> coordinates = new ArrayList<>();
            coordinates.add( new double[]{190, 140});   //these are test values for a triangle
            coordinates.add( new double[]{190, 130});
            coordinates.add( new double[]{190, 120});
            coordinates.add( new double[]{190, 110});   //these are test values for a triangle
            coordinates.add( new double[]{190, 100});
            coordinates.add( new double[]{190, 90});

            //print the values to the console, to validate them
            for(double[] coordinate : coordinates){
                System.out.println(coordinate[0] + ", " + coordinate[1]);

                double leftArmAngle = findLeftArmAngle(coordinate[0], coordinate[1]);
                double rightArmAngle = findRightArmAngle(coordinate[0], coordinate[1]);

                System.out.println("Left angle: " + leftArmAngle + ". Right angle: " + rightArmAngle);

                double leftPwmValue = leftAnglePwmConverter(leftArmAngle);
                double rightPwmValue = rightAnglePwmConverter(rightArmAngle);

                writer.println(leftPwmValue + "," + rightPwmValue + "," + penDown);
            }

            writer.flush();
            writer.close();
        }catch(IOException e ){System.out.println("FileWrite IOExecption" + e);}

    }

    //Draw Line
    private void drawLine() {

        //create a file for storing the pwm values
        try {
            PrintStream writer = new PrintStream(new FileOutputStream(new File("drawLine.txt")));

            //create an arraylist of coordinates (each coordinate is an array with an x and y value)
            List<double[]> coordinates = new ArrayList<>();

            //loop through some coordinates for a straight line.
            int y = 190;
            while(y < 300){

                //increment y
                y += 2;

                //add new coordinates to arraylist
                coordinates.add(new double[]{330, y});
            }

            //Calculate motor control signals and print to writer
            for(double[] coordinate : coordinates){
                System.out.println(coordinate[0] + ", " + coordinate[1]);

                double leftArmAngle = findLeftArmAngle(coordinate[0], coordinate[1]);
                double rightArmAngle = findRightArmAngle(coordinate[0], coordinate[1]);

                System.out.println("Left angle: " + leftArmAngle + ". Right angle: " + rightArmAngle);

                double leftPwmValue = leftAnglePwmConverter(leftArmAngle);
                double rightPwmValue = rightAnglePwmConverter(rightArmAngle);

                writer.println( (int)leftPwmValue + "," + (int)rightPwmValue + "," + penDown);
            }

            writer.flush();
            writer.close();
        }catch(IOException e ){System.out.println("FileWrite IOExecption" + e);}

    }


    //loads ppm image and stores as 2d arrayList
    private void storeImage(String filename) {
        try{
            ArrayList<ArrayList<Double>> image = new ArrayList<>();
            Scanner scan = new Scanner(new File(filename));
            while(scan.hasNext()){
                scan.next();    //is the file type
                int numberOfRows = scan.nextInt();
                int numberOfCols = scan.nextInt();
                int maxValue = scan.nextInt();

                //make the 2d array have the right number of collumns
                for(int i = 0; i < numberOfCols; i ++){
                    image.add( new ArrayList<>());
                }

                //store in the values to the array
                for(int row = 0; row < numberOfRows; row ++){
                    for(int col = 0; col < numberOfCols; col ++){

                    }
                }
            }


        }catch (IOException e){
            System.out.println(e);
        }
    }


    //find angle needed for right arm from tool position
    private double findRightArmAngle(double toolX, double toolY) {

        //distance between tool and motor
        double toolMotorDistance = Math.sqrt( Math.pow(toolX - motorRightX, 2) + Math.pow(toolY - motorRightY, 2));

        //midPoints
        double motorRightToolMidpointX = (toolX + motorRightX)/2;
        double motorRightToolMidpointY = (toolY + motorRightY)/2;

        //calculate distance between the midpoints and joints
        double midpointJointDistance = Math.sqrt( Math.pow(armRadius, 2) - Math.pow(toolMotorDistance/2, 2));
        System.out.println(midpointJointDistance);

        //angle between motors and first pen coord
        double motorJointAngle = Math.acos((motorRightX - toolX)/motorDistance);

        //calculate joint positions for left join
        double jointX = motorRightToolMidpointX + midpointJointDistance * Math.sin(motorJointAngle);
        double jointY = motorRightToolMidpointY - midpointJointDistance * Math.cos(motorJointAngle);

        //calculate joint positions for right joint
        return Math.atan2(jointY - motorRightY, jointX - motorRightX);
    }


    //find angle needed for left arm from tool position
    private double findLeftArmAngle(double toolX, double toolY){
        //distance between tool and motor
        double toolMotorDistance = Math.sqrt( Math.pow(toolX - motorLeftX, 2) + Math.pow(toolY - motorLeftY, 2));

        //midPoints
        double motorLeftToolMidpointX = (toolX + motorLeftX)/2;
        double motorLeftToolMidpointY = (toolY + motorLeftY)/2;

        //calculate distance between the midpoints and joints

        double midpointJointDistance = Math.sqrt( Math.pow(armRadius, 2) - Math.pow(toolMotorDistance/2, 2));

        System.out.println("MidpointJointDistance: " + midpointJointDistance);

        //angle between motors and first pen coord
        double motorJointAngle = Math.acos((motorLeftX - toolX)/motorDistance);

        System.out.println("MidpointJointAngle: " + motorJointAngle);

        //calculate joint positions for left joint
        double jointX = motorLeftToolMidpointX - midpointJointDistance * Math.sin(motorJointAngle);
        double jointY = motorLeftToolMidpointY + midpointJointDistance * Math.cos(motorJointAngle);

        //calculate joint positions for right joint
        return Math.atan2(jointY - motorLeftY, jointX - motorLeftX);

    }


    //converts angle to PWM for right arm
    private double rightAnglePwmConverter(double angle){
        double constant = 2343;
        double gradient = 657;
        return gradient * angle + constant;
    }

    //converts angle to PWM for left arm
    private double leftAnglePwmConverter(double angle){
        double constant = 2807;
        double gradient = 657;
        return gradient * angle + constant;
    }

    //Main
    public static void main(String[] args) {
        new Arm();
    }

}
