/*
 * Author: Bryan Hoang
 *
 * The code has been formatted to conform with the Google Java Style Guide
 */

import java.util.Random;
import java.util.Scanner;

/**
 * A class that wraps methods which allow someone to play the two dice version of the game of Pig.
 */
public class Assn1_GameOfPig {

  // A generator object that is seeded using the current time to ensure the randomness in the
  // sequence of pseudorandom numbers used to simulate the dice rolls
  private static final Random GENERATOR = new Random(System.currentTimeMillis());
  // A scanner object used obtain the player's input
  private static final Scanner PLAYER_INPUT = new Scanner(System.in);
  // Used to translate numbers into words for printing to the screen (i.e. 1 becomes one). The
  // first element is unused and is only meant as a placeholder.
  private static final String[] NUM_NAMES = {"", "one", "two", "three", "four", "five", "six"};
  // The number of dice for the version of the game of Pig that has been implemented by the class
  private static final int NUMBER_OF_DICE = 2;

  /**
   * Displays the game instructions and starts the game method.
   *
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    printInstructions();
    playGame();
  }

  /**
   * Simulates playing the two-dice version of the game of Pig against the computer.
   */
  private static void playGame() {
    // Indicates if the player wants to play the game again
    boolean playAgain = true;
    // Keeps track of the current round
    int turnCounter;
    // The player's game sum
    int playerSum;
    // The computer's game sum
    int computerSum;
    // Each loop represents a full play through of the game
    while (playAgain) {
      // Initialize the values for a new game
      turnCounter = 0;
      playerSum = 0;
      computerSum = 0;
      System.out.println(
          "\nPlayer's sum is: " + playerSum + ", Computer's sum is: " + computerSum + ".\n");
      // Each loop represents a round during the game.
      while (true) {
        // Prompt to begin the round, pausing the game until the player presses enter
        System.out.println("Press <enter> to start round " + ++turnCounter + ".");
        PLAYER_INPUT.nextLine();
        System.out.println("Player's turn:\n");
        // Simulate the player's turn
        playerSum = playTurn("Player", playerSum);
        System.out.println(
            "\nPlayer's sum is: " + playerSum + ", Computer's sum is: " + computerSum + ".\n");
        // Check if the player has won
        if (playerSum >= 100) {
          System.out.println("*****The Player wins!*****\n");
          break;
        }
        System.out.println("Computer's turn:\n");
        // Simulate the computer's turn
        computerSum = playTurn("Computer", computerSum);
        System.out.println(
            "\nPlayer's sum is: " + playerSum + ", Computer's sum is: " + computerSum + ".\n");
        // Check if the computer has won
        if (computerSum >= 100) {
          System.out.println("*****The Computer wins!*****\n");
          break;
        }
        // End of round
      }
      // Prompt the user to play again
      playAgain = getPlayerDecision("Play");
    }
    System.out.println("\nThanks for playing! Have a wonderful day!");
  }

  /**
   * Simulates either the player's or computer's turn
   *
   * @param competitor the name of the game participant, either "Player" or "Computer"
   * @param gameSum    the game sum of the competitor
   *
   * @return the turn sum
   */
  private static int playTurn(String competitor, int gameSum) {
    int[] dice = new int[NUMBER_OF_DICE];
    // initialize turn sum
    int turnSum = 0;
    // Indicates whether or not the player is going to roll again. A boolean is used instead of
    // 'break' in a while(true) loop since getDecision is able stop the loop if either the player
    // or computer want to stop rolling.
    boolean rollAgain = true;
    // Each loop represents a roll of the dice
    while (rollAgain) {
      rollDice(dice);
      System.out.println(competitor + " rolled " + NUM_NAMES[dice[0]] + " + " + NUM_NAMES[dice[1]]);
      // Check the special conditions and act accordingly
      if (dice[0] == 1 && dice[1] == 1) {
        turnSum = playDoubleOnes(competitor, dice, turnSum, gameSum);
      } else if (dice[0] == dice[1]) {
        turnSum = playDoubles(competitor, dice, turnSum, gameSum);
      } else if (dice[0] == 1 || dice[1] == 1) {
        System.out.println("TURN OVER! Turn sum is zero!");
        turnSum = 0;
        rollAgain = false;
      } else {
        // The default condition where the competitor gets to choose if they want to roll again
        turnSum += dice[0] + dice[1];
        displayPotentialSums(competitor, gameSum, turnSum);
        rollAgain = getDecision(competitor, gameSum, turnSum);
      }
    }
    return gameSum + turnSum;
  } // end playTurn method

  /**
   * Wraps a print statement displaying the potential sums for a competitor after each roll during a
   * turn.
   *
   * @param competitor the game participants, either "Player" or "Computer"
   * @param gameSum    the competitor's game sum
   * @param turnSum    the competitor's turn sum
   */
  private static void displayPotentialSums(String competitor, int gameSum, int turnSum) {
    System.out.println(
        competitor + "'s turn sum is: " + turnSum + " and game sum would be: " + (gameSum
            + turnSum));
  }

  /**
   * Simulates rolling two 6 sided dice by randomizing the values in a dice array of size 2.
   *
   * @param dice the dice that need to be rolled
   */
  private static void rollDice(int[] dice) {
    // Since .nextInt(int bound) returns an integer between 0 (inclusive) to the bound passed in
    // (exclusive), we need to pass in 6 to get an integer between 0 to 5 (inclusive) and add one
    // to the final random number to get it in the range of 1 to 6 (inclusive) to simulate a 6
    // sided die.
    dice[0] = GENERATOR.nextInt(6) + 1;
    dice[1] = GENERATOR.nextInt(6) + 1;
  }

  /**
   * Handles the event when both dice are equal to one.
   *
   * @param competitor the name of a game participant, either "Player" or "Computer"
   * @param dice       the rolled dice
   * @param turnSum    the competitor's turn sum
   * @param gameSum    the competitor's game sum
   *
   * @return the competitor's turn sum
   */
  private static int playDoubleOnes(String competitor, int[] dice, int turnSum, int gameSum) {
    System.out.println("DOUBLE ONES!");
    turnSum += dice[0] + dice[1] + 25;
    displayPotentialSums(competitor, gameSum, turnSum);
    System.out.println(competitor + " must roll again!");
    return turnSum;
  }

  /**
   * Handles the event when both dice match, other than ones.
   *
   * @param competitor the name of the game participant, either "Player" or "Computer"
   * @param dice       the rolled dice
   * @param turnSum    the competitor's turn sum
   * @param gameSum    the competitor's game sum
   *
   * @return the competitor's turn sum
   */
  private static int playDoubles(String competitor, int[] dice, int turnSum, int gameSum) {
    System.out.println("DOUBLES!");
    turnSum += (dice[0] + dice[1]) * 2;
    displayPotentialSums(competitor, gameSum, turnSum);
    System.out.println(competitor + " must roll again!");
    return turnSum;
  }

  /**
   * Calls a competitor specific decision making based on whose turn it currently is.
   *
   * @param competitor the name of the game participant, either "Player" or "Computer"
   * @param gameSum    the competitor's game sum
   * @param turnSum    the competitor's turn sum
   *
   * @return true if the competitor wants to roll again, false otherwise
   */
  private static boolean getDecision(String competitor, int gameSum, int turnSum) {
    // .equals ensures each string being compared has the same value, ignoring if they are
    // referring to the same String object
    if (competitor.equals("Player")) {
      return getPlayerDecision("Roll");
    }
    return getComputerDecision(gameSum, turnSum);
  }

  /**
   * Prompts the player if they want to roll or play again and returns true or false based on the
   * input. Makes sure the player types the correct input by prompting them again after invalid
   * inputs.
   *
   * @param prompt the prompt asking the player if they want to "Roll" or "Play" again
   *
   * @return true if the player wants to roll/play again, false otherwise
   */
  private static boolean getPlayerDecision(String prompt) {
    String playerDecision;
    // Loop until the player inputs the correct response
    while (true) {
      System.out.print(prompt + " again? (Enter 'y' or 'n'): ");
      playerDecision = PLAYER_INPUT.nextLine();
      if (playerDecision.equals("y")) {
        return true;
      } else if (playerDecision.equals("n")) {
        return false;
      } else {
        System.out.println(playerDecision + " is not 'y' nor 'n'. Please try again.");
      }
    }
  }

  /**
   * Simulates the computer determining if it should roll again when it has the option to choose
   * with two simple rules to follow.
   *
   * @param computerSum the computer's game sum
   * @param turnSum     the potential turn sum
   *
   * @return true if the computer wants to roll again, false otherwise
   */
  private static boolean getComputerDecision(int computerSum, int turnSum) {
    // The computer wants to roll again if it hasn't won yet and if it's turn score
    // is below 40. Otherwise don't roll again.
    return (computerSum + turnSum) < 100 && turnSum < 40;
  }

  /**
   * Prints the rules of the game to the console.
   */
  private static void printInstructions() {
    System.out.println("Hello player, and welcome to the 2 dice version of the game of Pig!\n" +
        "You will be competing against the computer.\n" +
        "Here are the rules:\n" +
        "-The first player to accumulate a score of 100 or more wins.\n" +
        "-The human goes first.\n" +
        "-After one roll, a player has the choice to \"hold\" or to roll again.\n" +
        "-Two dice are rolled. Certain conditions apply:\n" +
        "\t-If both dice are ones, then you add 25 to your turn score, and you must roll again.\n" +
        "\t-If one dice is one, then your turn is over and your turn score is set to zero.\n" +
        "\t-If both dice match (\"doubles\"), other than ones, then you gain twice the sum of the" +
        " dice, and you must roll again.\n" +
        "\t For example if you rolled two fours, you would gain 16 and then have to roll again.\n" +
        "\t-For any other dice combination, you just add the dice total to your turn score and " +
        "you have the choice of rolling again.\n" +
        "-When your turn is over, either through your choice or you rolled a one, then your turn "
        + "sum is added to your accumulated score.\n" +
        "Good luck!");
  }

}
