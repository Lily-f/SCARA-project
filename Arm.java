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
    private int penUp = 1300;
    private int motorLeftX = 270;
    private int motorLeftY = 480;
    private int motorRightX = 400;
    private int motorRightY = 480;
    private int motorDistance = motorRightX - motorLeftX;  //unit = pixels
    private int armRadius = 200; //unit = pixels

    //constructor
    private Arm() {

        //create a new ImageProcessor object, and get the first image to load
        //ImageProcessor imageProcessor = new ImageProcessor();

        //drawTriangle();
        drawLine(2);

    }
    
    //draw a circle
     

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
    private void drawLine(int direction) {

        //create a file for storing the pwm values
        try {
            PrintStream writer = new PrintStream(new FileOutputStream(new File("drawLine.txt")));

            //create an arraylist of coordinates (each coordinate is an array with an x and y value)
            List<double[]> coordinates = new ArrayList<>();


			if(direction == 0){
				//loop through some coordinates for a straight horizontal line.
				int x = 280; // x value must be greater than or equal to 280
				while(x < 350){

					//increment x
					x += 2;

					//add new coordinates to arraylist
					coordinates.add(new double[]{x, 120});
				}
			}
			else if (direction == 1){
				//loop through some coordinates for a straight  vertical line.
				int y = 100;
				while(y < 200){ // max y cannot be any greater than 200 to avoid singularity

					//increment y
					y += 2;

					// add new coordinates to arraylist
					coordinates.add(new double[]{300, y});
				}
			}
			else if (direction == 2){
				//loop through some coordinates for a diagonal line.
				int y = 100;
				int x = 280;
				while(y < 200 && x < 350){

					//increment x and y
					y += 2;
					x += 2;

					// add new coordinates to arraylist
					coordinates.add(new double[]{x, y});
				}
			}
			
			else if (direction == 3){
				//loop through some coordinates for a square line of 70px/70px
				int y = 100;
				int x = 280;
				
				while(x < 350){

					//increment x
					x += 2;

					// add new coordinates to arraylist
					coordinates.add(new double[]{x, y});
				}
				while(y < 170){

					//increment y
					y += 2;

					// add new coordinates to arraylist
					coordinates.add(new double[]{x, y});
				}
				while(x > 280){

					//increment x
					x -= 2;

					// add new coordinates to arraylist
					coordinates.add(new double[]{x, y});
				}
				while(y > 100){

					//increment y
					y -= 2;

					// add new coordinates to arraylist
					coordinates.add(new double[]{x, y});
				}
				
				
			}
			
			else if (direction == 4){
				//loop through some coordinates for a circular line.
				int centerX = 325;
				int centerY = 135;
				int radius = 20;
				for(double i = 0; i < 360; i += 2){
					
					double x = 0.9 * radius * Math.cos(Math.toRadians(i)) + centerX;
					double y = radius * Math.sin(Math.toRadians(i)) + centerY;
					
					// add new coordinates to arraylist
					coordinates.add(new double[]{x, y});
				}
			}
			
			//pen up for first value
			double[] firstCoordinate = coordinates.get(0);
			double firstLeftArmAngle = findLeftArmAngle(firstCoordinate[0], firstCoordinate[1]);
			double firstRightArmAngle = findRightArmAngle(firstCoordinate[0], firstCoordinate[1]);
			double firstLeftPwmValue = leftAnglePwmConverter(firstLeftArmAngle);
			double firstRightPwmValue = rightAnglePwmConverter(firstRightArmAngle);
			writer.println( (int)firstLeftPwmValue + "," + (int)firstRightPwmValue + "," + penUp);


            //Calculate motor control signals and print to writer
            for(double[] coordinate : coordinates){
                System.out.println(coordinate[0] + ", " + coordinate[1]);

                double leftArmAngle = findLeftArmAngle(coordinate[0], coordinate[1]);
                double rightArmAngle = findRightArmAngle(coordinate[0], coordinate[1]);
                
                System.out.println("Left angle: " + leftArmAngle + ". Right angle: " + rightArmAngle);

                double leftPwmValue = leftAnglePwmConverter(leftArmAngle);
                double rightPwmValue = rightAnglePwmConverter(rightArmAngle);

                writer.println( (int)leftPwmValue + "," + (int)rightPwmValue + "," + penDown);
                if (coordinate == coordinates.get(coordinates.size() - 1)){
					writer.println( (int)leftPwmValue + "," + (int)rightPwmValue + "," + penUp);
				}
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
