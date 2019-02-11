/*
 * Author: Bryan Hoang
 * NetID: 16bch1
 * Student Number: 20053722
 * 2019/2/8
 *
 * The code has been formatted to conform with the Google Java Style Guide
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class reads data from the Logger.csv file and outputs .cvs files for each
 * motor in Logger.csv.
 */
class MotorDataAnalyzer {

  private static final int NUM_MOTORS = 7;
  private static final int NUM_LINES = 1000;

  /**
   * Calls methods to read the data, analyze the data, and write the data as well as displaying the
   * status of the program in the terminal.
   *
   * @param args The command line arguments (Unused).
   */
  public static void main(String[] args) {
    System.out.println("Analyzing the logged motor data.");
    // TODO: Ask if this chain of method calls is stylistically okay
    writeReports(analyzeMotorData(getMotorData()));
    System.out.println("The analysis of the motor data has successfully completed.\r\n"
        + "The results have been written to the Motor(num).csv files");
  }

  /**
   * Gets the motor data from the Logger.csv file to process it.
   *
   * @return A 2D array of doubles containing the data from the file.
   */
  private static double[][] getMotorData() {
    // TODO: Ask if I should read in redundant time values from file
    double[][] motorData = new double[NUM_MOTORS][NUM_LINES];
    Path file = Paths.get("Logger.csv");
    try (var reader = Files.newBufferedReader(file)) {
      readMotorData(reader, motorData);
    } catch (IOException | NumberFormatException err) {
      System.err.println(err);
      System.exit(1);
    }
    return motorData;
  }

  /**
   * Reads the motor data from the file line by line by removing leading and trailing whitespace,
   * and splitting up the line into anv array of strings containing the comma separated values.
   *
   * @param reader    The file reader.
   * @param motorData The destination of the data to be read into.
   *
   * @throws IOException           If an I/O error occurs.
   * @throws NumberFormatException If the file contains a comma separated value that cannot be
   *                               parsed into a double.
   */
  private static void readMotorData(BufferedReader reader, double[][] motorData)
      throws IOException, NumberFormatException {
    String[] line;
    for (int lineNum = 0; lineNum < NUM_LINES; lineNum++) {
      line = reader.readLine().trim().split(",");
      parseLine(line, lineNum, motorData);
    }
  }

  /**
   * Parses each of the comma separated values into a double and stores it into an array.
   *
   * @param line      An array of strings representing each comma separated value.
   * @param lineNum   The current line number in the file.
   * @param motorData The destination for the parsed data.
   *
   * @throws NumberFormatException If a comma separated value does not contain a parsable double.
   */
  private static void parseLine(String[] line, int lineNum, double[][] motorData)
      throws NumberFormatException {
    for (int motorNum = 1; motorNum <= NUM_MOTORS; motorNum++) {
      motorData[motorNum - 1][lineNum] = Double.parseDouble(line[motorNum]);
    }
  }

  /**
   * Interprets the raw motor data to create an array of strings that will be used write the
   * report.
   *
   * @param motorData The data to process.
   *
   * @return The array of strings to be used to write each of the motor files.
   */
  private static String[] analyzeMotorData(double[][] motorData) {
    String[] analyzedData = new String[NUM_MOTORS];
    Arrays.fill(analyzedData, "Start (s), Finish (s), Current (A)\r\n");
    for (int motorNum = 1; motorNum <= NUM_MOTORS; motorNum++) {
      analyzeMotorUsage(motorData[motorNum - 1], motorNum, analyzedData);
      isMotorUsed(analyzedData, motorNum);
    }
    return analyzedData;
  }

  /**
   * Detects pulses of current being drawn from a motor by concatenating the start time, end time,
   * and average current during a pulse, noting when the current has been exceeded during a pulse.
   *
   * @param motorCurrents The set of measured current values for a motor.
   * @param motorNum      The motor's distinguishing number.
   * @param analyzedData  The processed data.
   */
  private static void analyzeMotorUsage(double[] motorCurrents, int motorNum,
      String[] analyzedData) {
    boolean motorOn = false;
    boolean currentExceeded = false;
    for (int time = 0, startTime = 0; time < NUM_LINES; time++) {
      if (isMotorTurningOn(motorOn, motorCurrents[time])) {
        startTime = time;
        motorOn = true;
      }
      currentExceeded = isCurrentExceeded(motorCurrents[time], currentExceeded);
      if (isMotorTurningOff(motorOn, motorCurrents[time])) {
        analyzedData[motorNum - 1] += startTime + ", " + (time - 1) + ", " +
            String.format("%.3f", calcAvgCurr(motorCurrents, startTime, time)) +
            (currentExceeded ? ", ***Current Exceeded***\r\n" : "\r\n");
        motorOn = false;
        currentExceeded = false;
      }
    }
  }

  /**
   * Checks if a motor is turning on.
   *
   * @param motorOn The state of the motor.
   * @param curr    The current value of the motor.
   *
   * @return True if the motor is turning on, false otherwise.
   */
  private static boolean isMotorTurningOn(boolean motorOn, double curr) {
    return !motorOn && curr > 1;
  }

  /**
   * Checks if a motor has went over the maximum current value of 8 amps while it has been on.
   *
   * @param current      The current value of the motor.
   * @param currExceeded The flag for if the current has already been exceeded.
   *
   * @return True if the motor's current has already exceeded the maximum current, false otherwise.
   */
  private static boolean isCurrentExceeded(double current, boolean currExceeded) {
    if (current > 8) {
      return true;
    }
    return currExceeded;
  }

  /**
   * Checks if the motor is turning off.
   *
   * @param motorOn The state of the motor.
   * @param current The current value of the motor.
   *
   * @return True if the motor is turning off, false otherwise.
   */
  private static boolean isMotorTurningOff(boolean motorOn, double current) {
    return motorOn && current < 1;
  }

  /**
   * Computes the average current during a motor's period of activity.
   *
   * @param motorCurrents The current values for a particular motor.
   * @param startTime     The time when the current pulse starts.
   * @param endTime       The time when the current pulse ends.
   *
   * @return The average current value.
   */
  private static double calcAvgCurr(double[] motorCurrents, int startTime,
      int endTime) {
    double sum = 0;
    for (int time = startTime; time < endTime; time++) {
      sum += motorCurrents[time];
    }
    return sum / (endTime - startTime);
  }

  /**
   * Checks if no current activity was processed for the motor.
   *
   * @param analyzedData The analyzed motor data.
   * @param motorNum     The motor of interest.
   */
  private static void isMotorUsed(String[] analyzedData, int motorNum) {
    if (analyzedData[motorNum - 1].equals("Start (s), Finish (s), Current (A)\r\n")) {
      analyzedData[motorNum - 1] = "Not used.\r\n";
    }
  }

  /**
   * Writes the analyzed data of each motor into .csv files.
   *
   * @param analyzedData The processed data to write to the files.
   */
  private static void writeReports(String[] analyzedData) {
    Path outputFile;
    for (int motorNum = 1; motorNum <= NUM_MOTORS; motorNum++) {
      outputFile = Paths.get("Motor" + motorNum + ".csv");
      try (var writer = Files.newBufferedWriter(outputFile)) {
        writer.write(analyzedData[motorNum - 1]);
      } catch (IOException err) {
        System.err.println(err.getMessage());
        System.exit(1);
      }
    }
  }

}
