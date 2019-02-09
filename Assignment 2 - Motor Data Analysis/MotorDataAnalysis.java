/*
 * Author: Bryan Hoang
 * NetID: 16bch1
 * Student Number: 20053722
 * 2019/2/8
 *
 * The code has been formatted to conform with the Google Java Style Guide
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class reads data from the Logger.csv file and outputs .cvs files for each
 * motor in Logger.csv.
 */
class MotorDataAnalysis {

  private static final int NUM_MOTORS = 7;
  private static final int NUM_LINES = 1000;

  /**
   * Calls methods to read the data, analyze the data, and write the data. Also displays the
   * status of the program in the terminal.
   *
   * @param args The command line arguments (Unused).
   */
  public static void main(String[] args) {
    System.out.println("Analyzing the logged motor data.");
    double[][] motorData = readMotorData();
    String[] analyzedMotorData = analyzeMotorData(motorData);
    writeReports(analyzedMotorData);
    System.out.println("Analysis of the motor data is complete!\n"
        + "The results have been written to the Motor .csv files");
  }

  /**
   * Reads the raw data from Logger.csv into a 2D array of doubles.
   *
   * @return The 2D array of doubles containing data from the file.
   */
  private static double[][] readMotorData() {
    var motorData = new double[1 + NUM_MOTORS][NUM_LINES];
    Path file = Paths.get("Logger.csv");
    String[] line;
    try (var reader = Files.newBufferedReader(file)) {
      for (int i = 0; i < NUM_LINES; i++) {
        // Read a line, remove leading and trailing whitespace, and split up the line into an
        // array of strings containing each separated value
        line = reader.readLine().trim().split(",");
        for (int j = 0; j < 1 + NUM_MOTORS; j++) {
          motorData[j][i] = Double.parseDouble(line[j]);
        }
      }
    } catch (IOException | NumberFormatException err) {
      System.err.println(err);
      System.exit(1);
    }
    return motorData;
  }

  /**
   * Interprets the raw motor data to create an array of strings that will be used create the
   * report. Can assume that the motors won't be running at time 0 and at time 999 seconds.
   *
   * @param motorData The data to process.
   *
   * @return The array of strings to be used to write each of the motor files.
   */
  private static String[] analyzeMotorData(double[][] motorData) {
    String[] analyzedData = new String[NUM_MOTORS];
    Arrays.fill(analyzedData, "");
    boolean motorOn = false;
    boolean currExceeded = false;
    int startTime = 0;
    for (int motorNum = 1; motorNum < 1 + NUM_MOTORS; motorNum++) {
      for (int time = 0; time < NUM_LINES; time++) {
        if (!motorOn && motorData[motorNum][time] > 1) {
          motorOn = true;
          startTime = time;
        }
        if (motorData[motorNum][time] > 8) {
          currExceeded = true;
        }
        if (motorOn && motorData[motorNum][time] < 1) {
          // Concatenate to the start time, end time, and average current during a
          // pulse.
          analyzedData[motorNum - 1] += startTime + ", " + (time - 1) + ", " +
              String.format("%.3f", calcAvgCurr(motorData[motorNum], startTime, time));
          analyzedData[motorNum - 1] += currExceeded ? ", ***Current Exceeded***\r\n" : "\r\n";
          currExceeded = false;
          motorOn = false;
        }
      }
      if (analyzedData[motorNum - 1].isEmpty()) {
        analyzedData[motorNum - 1] = "Not used.\r\n";
      }
    }
    return analyzedData;
  }

  /**
   * Computes the average current detected during a motor's period of activity.
   *
   * @param motorData The current values for a particular motor.
   * @param startTime The time when the current pulse starts.
   * @param endTime   The time when the current pulse ends.
   *
   * @return The average current value.
   */
  private static double calcAvgCurr(double[] motorData, int startTime,
      int endTime) {
    double sum = 0;
    for (int time = startTime; time < endTime; time++) {
      sum += motorData[time];
    }
    return sum / (endTime - startTime);
  }

  /**
   * Writes the processed data of each motor into csv files.
   *
   * @param analyzedData The analyzed data to write to the files.
   */
  private static void writeReports(String[] analyzedData) {
    Path outputFile;
    for (int i = 0; i < NUM_MOTORS; i++) {
      outputFile = Paths.get("Motor" + (i + 1) + ".csv");
      try (var writer = Files.newBufferedWriter(outputFile)) {
        writer.write("Start (s), Finish (s), Current (A)\r\n" + analyzedData[i]);
      } catch (IOException err) {
        System.err.println(err);
        System.exit(1);
      }
    }
  }

}
