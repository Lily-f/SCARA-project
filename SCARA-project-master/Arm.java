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
	//fields
	public static final int PEN_DOWN = 1500;
	public static final int PEN_UP = 1300;
	private int motorLeftX = 270;
	private int motorRightX = 400;
	private int motorDistance = motorRightX - motorLeftX;  //unit = pixels
	private int armRadius = 200; //unit = pixels

	//create an arrayList of coordinates (each coordinate is an array with an x and y value)
	private List<double[]> coordinates = new ArrayList<>();

	//constructor
	private Arm() {

        //get user input on what type of thing to draw
        Scanner input = new Scanner(System.in);
        System.out.println("Enter 0 to 6 inclusive for horizontal/vertical/diagonal/square/circle/image (-1 for nothing): ");
        int direction = input.nextInt();

		drawLine(direction);
	}

	//Draw Line
	private void drawLine(int direction) {
		coordinates.clear();

		//draws a horizontal line
		if(direction == 0){
			//using the getCoords method
			getCoords(280, 120, 350, 120);
		}

		//draws a vertical line
		else if (direction == 1){
			getCoords(300, 100, 300, 200);
		}

		//draws a diagonal line
		else if (direction == 2){
			getCoords(280, 100, 350, 200);
		}

        //draws a rectangle
		else if (direction == 3){
			//loop through some coordinates for a square line of 70px/70px
			getCoords(280, 100, 350, 100);	//top horizontal
			getCoords(350, 100, 350, 170);	//right vertical
			getCoords(350, 170, 280, 170);	//bottom horizontal
			getCoords(280, 170, 280, 100);	//left vertical
		}

        //loop through some coordinates for a circular line.
		else if (direction == 4){
			int centerX = 325;
			int centerY = 135;
			int radius = 20;
			for(double i = 0; i < 360; i += 2){

				double x = 0.9 * radius * Math.cos(Math.toRadians(i)) + centerX;
				double y = radius * Math.sin(Math.toRadians(i)) + centerY;

				// add new coordinates to arrayList
				coordinates.add(new double[]{x, y, PEN_DOWN});
			}
		}

        //loop through some coordinates for 'SKYNET' letters drawing.
		else if (direction == 5) {
			//offset to draw word in middle of page
			int xOffSet = 280;
			int yOffSet = 140;

			// Letter 'S'
			coordinates.add(new double[]{12 + xOffSet, yOffSet, PEN_UP});
			getCoords(12 + xOffSet, yOffSet, xOffSet, yOffSet);		//top horizontal
			getCoords(xOffSet, yOffSet, xOffSet, 9 + yOffSet);    	//top vertical
			getCoords(xOffSet, 9 + yOffSet, 12 + xOffSet, 9 + yOffSet);	  	//middle horizontal
			getCoords(12 + xOffSet, 9 + yOffSet, 12 + xOffSet, 18 + yOffSet); 	//bottom vertical
			getCoords(12 + xOffSet, 18 + yOffSet, xOffSet, 18 + yOffSet);

			// Letter 'K'
			coordinates.add(new double[]{18 + xOffSet, yOffSet, PEN_UP});
			getCoords(18 + xOffSet, yOffSet, 18 + xOffSet, 18 + yOffSet);	//left vertical
			getCoords(18 + xOffSet, 9 + yOffSet, 30 + xOffSet, yOffSet); 	//middle to top diagonal
			getCoords(18 + xOffSet, 9 + yOffSet, 30 + xOffSet, 18 + yOffSet);	//middle to bottom diagonal
			//MUST CHANGE THIS FOR NEXT DRAWING POS

			// Letter 'Y'
            coordinates.add(new double[]{36 + xOffSet, yOffSet, PEN_UP});
			getCoords(36 + xOffSet, yOffSet, 42 + xOffSet, 9 + yOffSet);	 	//left arm
            getCoords(36 + xOffSet, 18 + yOffSet, 48 + xOffSet, yOffSet);	//Right arm

			// Letter 'N'
            coordinates.add(new double[]{54 + xOffSet, 18 + yOffSet, PEN_UP});
			getCoords(54 + xOffSet, 18 + yOffSet, 54 + xOffSet, yOffSet);	//Left vertical leg
			getCoords(54 + xOffSet, yOffSet, 66 + xOffSet, 18 + yOffSet);	//Middle diagonal
			getCoords(66 + xOffSet, 18 + yOffSet, 66 + xOffSet, yOffSet);	//Right vertical leg

			// Letter 'E'
            coordinates.add(new double[]{84 + xOffSet, yOffSet, PEN_UP});
			getCoords(84 + xOffSet, yOffSet, 72 + xOffSet, yOffSet);		//Top horizontal
			getCoords(72 + xOffSet, yOffSet, 72 + xOffSet, 18 + yOffSet);	//Left vertical
			getCoords(72 + xOffSet, 18 + yOffSet, 84 + xOffSet, 18 + yOffSet);	//Bottom horizontal
            coordinates.add(new double[]{72 + xOffSet, 9 + yOffSet, PEN_UP});
			getCoords(72 + xOffSet, 6 + yOffSet, 84 + xOffSet, 6 + yOffSet);		//Middle horizonatal

			// Letter 'T'
            coordinates.add(new double[]{90 + xOffSet, yOffSet, PEN_UP});
			getCoords(90 + xOffSet, yOffSet, 102 + xOffSet, yOffSet);	//Top horizontal
			getCoords(96 + xOffSet, yOffSet, 96 + xOffSet, 18 + yOffSet);	//Middle vertical
		}

		//else draw image with the imageProcessor
		else if(direction == 6){
			System.out.println("Drawing image!");

			//add an offset so the image is drawn in the center
            int xOffset = 280;
            int yOffset = 140;

            //create a new ImageProcessor object, and get the first image to load
            ImageProcessor imageProcessor = new ImageProcessor();
            for (double[] cannyCoordinates : imageProcessor.getCannyCoords()){
                System.out.println("Coordinates of edge pixel: " + cannyCoordinates[0] + " " + cannyCoordinates[1]);
                coordinates.add(new double[]{cannyCoordinates[0]+xOffset, cannyCoordinates[1]+yOffset, PEN_DOWN});
            }
		}

		//else if one of the options not selected do nothing
		else{
		    System.out.println("Nothing selected");
        }


		//write the new coordinates to file, if there are coordinates
		if(!coordinates.isEmpty()){
			writeCoordinatesToFile();
		}
	}


	//get the coordinates for points on a line (given start & end points)
	private void getCoords(double startX, double startY, double endX, double endY){

		//number of points that make up the line
		int lineSplit = 20;

		//calculate the gradient between end and start points
		double xGradient = endX - startX;
		double yGradient = endY - startY;

		//check if x gradient is negative. if not then want positive increment
        if(startX < endX) {
            if(startY < endY) {
                //increment the x and y values, and add them to the list of coordinates
                while (startX <= endX && startY <= endY) {

                    //increment x and y
                    startX += xGradient / lineSplit;
                    startY += yGradient / lineSplit;

                    // add new coordinates to arraylist
                    coordinates.add(new double[]{startX, startY, PEN_DOWN});
                }
            }
            //else want negative y increment
            else{
                //increment the x and y values, and add them to the list of coordinates
                while (startX <= endX && startY >= endY) {

                    //increment x and y
                    startX += xGradient / lineSplit;
                    startY += yGradient / lineSplit;

                    // add new coordinates to arrayList
                    coordinates.add(new double[]{startX, startY, PEN_DOWN});
                }
            }
        }
        //else endX is smaller so want negative x increment
        else{
            if(startY < endY){
                //increment the x and y values, and add them to the list of coordinates
                while (startX >= endX && startY <= endY) {

                    //increment x and y
                    startX += xGradient / lineSplit;
                    startY += yGradient / lineSplit;

                    // add new coordinates to arrayList
                    coordinates.add(new double[]{startX, startY, PEN_DOWN});
                }
            }
            else {
                //increment the x and y values, and add them to the list of coordinates
                while (startX >= endX && startY >= endY) {

                    //increment x and y
                    startX += xGradient / lineSplit;
                    startY += yGradient / lineSplit;

                    // add new coordinates to arrayList
                    coordinates.add(new double[]{startX, startY, PEN_DOWN});
                }
            }
        }
	}

    //takes all coordinates in the field, calculates the pwm values needed for that point, and prints to file
	private void writeCoordinatesToFile(){
		try{
			PrintStream writer = new PrintStream(new FileOutputStream(new File("draw.txt")));

			//Calculate motor control signals and print to writer
			for(double[] coordinate : coordinates){
				double leftArmAngle = findLeftArmAngle(coordinate[0], coordinate[1]);
				double rightArmAngle = findRightArmAngle(coordinate[0], coordinate[1]);

				double leftPwmValue = leftAnglePwmConverter(leftArmAngle);
				double rightPwmValue = rightAnglePwmConverter(rightArmAngle);

				if (coordinate[2] == PEN_UP){
					writer.println((int) leftPwmValue + "," + (int) rightPwmValue + "," + PEN_UP);
				}
				else {
                    writer.println((int) leftPwmValue + "," + (int) rightPwmValue + "," + PEN_DOWN);
                }

				//if last coordinate, pull pen up when done
				if (coordinate == coordinates.get(coordinates.size() - 1)){
					writer.println((int) leftPwmValue + "," + (int) rightPwmValue + "," + PEN_UP);
				}
			}

			//clear the coords field and close the printstream
            System.out.println("Finished!");
			coordinates.clear();
			writer.flush();
			writer.close();
		}catch(IOException e ){System.out.println("FileWrite IOExecption: " + e);}
	}


	//find angle needed for right arm from tool position
	private double findRightArmAngle(double toolX, double toolY) {
        //y coordinate of right motor
        int motorRightY = 480;

		//distance between tool and motor
		double toolMotorDistance = Math.sqrt( Math.pow(toolX - motorRightX, 2) + Math.pow(toolY - motorRightY, 2));

		//midPoints
		double motorRightToolMidpointX = (toolX + motorRightX)/2;
		double motorRightToolMidpointY = (toolY + motorRightY)/2;

		//calculate distance between the midpoints and joints
		double midpointJointDistance = Math.sqrt( Math.pow(armRadius, 2) - Math.pow(toolMotorDistance/2, 2));

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
        //y coordinate of left motor
	    int motorLeftY = 480;

		//distance between tool and motor
		double toolMotorDistance = Math.sqrt( Math.pow(toolX - motorLeftX, 2) + Math.pow(toolY - motorLeftY, 2));

		//midPoints
		double motorLeftToolMidpointX = (toolX + motorLeftX)/2;
		double motorLeftToolMidpointY = (toolY + motorLeftY)/2;

		//calculate distance between the midpoints and joints

		double midpointJointDistance = Math.sqrt( Math.pow(armRadius, 2) - Math.pow(toolMotorDistance/2, 2));

		//angle between motors and first pen coord
		double motorJointAngle = Math.acos((motorLeftX - toolX)/motorDistance);

		//calculate joint positions for left joint
		double jointX = motorLeftToolMidpointX - midpointJointDistance * Math.sin(motorJointAngle);
		double jointY = motorLeftToolMidpointY + midpointJointDistance * Math.cos(motorJointAngle);

		//calculate joint positions for right joint
		return Math.atan2(jointY - motorLeftY, jointX - motorLeftX);

	}


	//converts angle to PWM for right arm
	private double rightAnglePwmConverter(double angle){
		double constant = 2700;	
		double gradient = 800;	
		return gradient * angle + constant;
	}
	
	//USE ARM 2 ARM 2ARM 2ARM 2ARM 2ARM 2ARM 2ARM 2ARM 2ARM 2
	//converts angle to PWM for left arm.
	private double leftAnglePwmConverter(double angle){
		double constant = 2400;	//2500	
		double gradient = 3200;	//650
		return gradient * angle + constant;
	}

	//Main. creates new arm
	public static void main(String[] args) {
		new Arm();
	}
}
