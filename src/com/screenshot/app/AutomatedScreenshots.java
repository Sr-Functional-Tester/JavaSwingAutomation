package com.screenshot.app;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class AutomatedScreenshots {
    public static boolean takeSnip(int howManySnips, String howMuchTime, int delay) {
        try {
            // Create a Robot object
            Robot robot = new Robot();

            // Define the area to capture (full screen)
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            int screenshotCount = 1;
            int defaultSnips = 900;
            long fixedDelay = 3000;
            if(delay != 0)
            	fixedDelay = delay * 1000; // Fixed delay of 3 seconds

            if (howMuchTime != null) {
                // If a time is provided, you can still calculate the number of snips (this is optional)
                defaultSnips = calculateNumberOfSnips(howMuchTime);
            }

            if (howManySnips != 0) {
                defaultSnips = howManySnips; // Override with the user-specified number of snips
            }

            String folderPath = createScreensFolder();
            if (folderPath == null) {
                System.out.println("Folder path creation failed. Exiting.");
                return false;
            }

            // Thread interruption flag (to safely stop the process)
            boolean interrupted = false;

            while (screenshotCount <= defaultSnips) {
                try {
                    // Capture the screen
                    BufferedImage screenshot = robot.createScreenCapture(screenRect);

                    // Save the screenshot as PNG
                    ImageIO.write(screenshot, "PNG", new File(folderPath + File.separator + "screenshot_" + screenshotCount + ".png"));
                    System.out.println("Screenshot " + screenshotCount + " saved!");

                    // Wait for the fixed time before taking the next screenshot
                    Thread.sleep(fixedDelay); // 3000 milliseconds = 3 seconds

                    screenshotCount++;
                } catch (InterruptedException e) {
                    // If interrupted, mark the flag and exit the loop
                    interrupted = true;
                    break; // Exit the loop if interrupted
                }
            }

            if (interrupted) {
                System.out.println("Screenshot process interrupted.");
            } else {
                System.out.println("Screenshot process completed successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // Method to calculate the number of snips based on time input
    public static int calculateNumberOfSnips(String timeInput) {
        // Extract the numeric value and the unit (last character)
        int timeValue = Integer.parseInt(timeInput.substring(0, timeInput.length() - 1)); // Get the number
        char unit = timeInput.charAt(timeInput.length() - 1); // Get the unit (last character)

        // Convert time to total seconds
        long totalSeconds = 0;

        if (unit == 'm') {
            // Convert minutes to seconds
            totalSeconds = timeValue * 60;
        } else if (unit == 's') {
            // Time is already in seconds
            totalSeconds = timeValue;
        } else {
            throw new IllegalArgumentException(
                    "Invalid time unit. Only 'm' (minutes) and 's' (seconds) are supported.");
        }

        // Each snip takes 3 seconds, so calculate the number of snips
        return (int) (totalSeconds / 3); // Return the number of snips
    }

    public static String createScreensFolder() {
        // Get the user's home directory (this works on Windows, macOS, and Linux)
        String userHome = System.getProperty("user.home");
        String picturesPath = userHome + File.separator + "Pictures";

        // Create a File object for the Pictures directory
        File picturesDir = new File(picturesPath);

        // Check if the Pictures directory exists
        if (!picturesDir.exists()) {
            System.out.println("Pictures directory not found.");
            return null;
        }

        // Create the "screenshots" folder path
        String screenshotsFolderPath = picturesPath + File.separator + "screenshots";

        // Create a File object for the "screenshots" folder
        File screenshotsFolder = new File(screenshotsFolderPath);

        // Check if the "screenshots" folder exists
        if (!screenshotsFolder.exists()) {
            // If the folder doesn't exist, create it
            if (screenshotsFolder.mkdir()) {
                System.out.println("Screenshots folder created successfully at: " + screenshotsFolder.getAbsolutePath());
                return screenshotsFolder.getAbsolutePath();  // Return the absolute path of the folder
            } else {
                System.out.println("Failed to create the Screenshots folder.");
                return null;
            }
        } else {
            System.out.println("Screenshots folder already exists at: " + screenshotsFolder.getAbsolutePath());
        }

        // If the "screenshots" folder exists, start creating "screenshots0", "screenshots1", etc.
        String folderName = "screenshots0";
        File screensFolder = new File(picturesDir, folderName);

        // Check for the next available folder
        int counter = 0;
        while (screensFolder.exists()) {
            // Increment the counter and create the next folder name (screenshots1, screenshots2, ...)
            folderName = "screenshots" + (++counter);
            screensFolder = new File(picturesDir, folderName);
        }

        // Once an available folder is found, create it
        if (screensFolder.mkdir()) {
            System.out.println("Folder '" + folderName + "' created successfully in " + picturesPath);
            return screensFolder.getAbsolutePath();  // Return the absolute path of the created folder
        } else {
            System.out.println("Failed to create folder.");
            return null;
        }
    }

    public boolean takeScreenshotByNum(String howmany, String delay) {
        int howManyScreens = Integer.parseInt(howmany);
        int delayBetweenSnips = 0;
        if(delay !=null && !delay.isEmpty()) {
        	String timeWithoutS = delay.replace("s", "");
        	System.out.println(timeWithoutS);
        	delayBetweenSnips = Integer.parseInt(timeWithoutS);
        }
        return takeSnip(howManyScreens, null, delayBetweenSnips);
    }

    public boolean takeScreenshotByTime(String time, String delay) {
    	int delayBetweenSnips = 0;
        if(delay !=null && !delay.isEmpty()) {
        	String timeWithoutS = delay.replace("s", "");
        	delayBetweenSnips = Integer.parseInt(timeWithoutS);
        }
        return takeSnip(0, time, delayBetweenSnips);
    }
}
