/*
 * Author: Bryan Hoang
 *
 * The code has been formatted to conform with the Google Java Style Guide
 */

import java.util.Random;
import java.util.Scanner;

/**
 * A class that wraps methods which allow a person to play the two dice version of the game of Pig
 * against the computer.
 */
public class Assn1_GameOfPig {

  private static final Random NUM_GENERATOR = new Random(System.currentTimeMillis());
  private static final Scanner PLAYER_INPUT = new Scanner(System.in);
  // Used to translate a rolled number into a word
  private static final String[] NUM_NAMES = {"", "one", "two", "three", "four", "five", "six"};
  private static final int NUM_DICE = 2;
  private static final int NUM_SIDES = 6;
  // Allows someone to change the max score of the game
  private static final int MAX_SUM = 100;

  /**
   * Displays the game instructions and starts the game method, looping to start new games if the
   * player wishes to play the game again.
   *
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    printIntroductionAndRules();
    do {
      playGame();
    } while (getPlayerDecision("Play"));
    System.out.println("\nThanks for playing! Have a wonderful day!");
  }

  /**
   * Simulates playing the two-dice version of the game of Pig against the computer by looping
   * through turns, ending once either competitor scores above the maximum
   */
  private static void playGame() {
    int playerSum = 0;    // The player's total sum during the game
    int computerSum = 0;  // The computer's total sum during the game
    // 1 loop = 1 turn
    for (int turn = 1; playerSum < MAX_SUM && computerSum < MAX_SUM; turn++) {
      // A round starts on every odd turn with the player going first
      if (turn % 2 == 1) {
        System.out.println("\nPress <enter> to start round " + (turn + 1) / 2 + ".");
        PLAYER_INPUT.nextLine();
        System.out.println("Player's turn:\n");
        playerSum = playTurn("Player", playerSum);
      } else {
        System.out.println("\nComputer's turn:\n");
        computerSum = playTurn("Computer", computerSum);
      }
      System.out.println(
          "\nPlayer's sum is: " + playerSum + ", Computer's sum is: " + computerSum + ".");
    }
    // playerSum indicates who won due to the exit condition of the for loop
    if (playerSum >= MAX_SUM) {
      System.out.println("\n*****The Player wins!*****\n");
    } else {
      System.out.println("\n*****The Computer wins!*****\n");
    }
  }

  /**
   * Simulate's either the player's or computer's turn by looping through dice rolls and handling
   * the appropriate conditions.
   *
   * @param competitor the name of the game participant, either "Player" or "Computer"
   * @param gameSum    the competitor's game sum
   *
   * @return the turn sum
   */
  private static int playTurn(String competitor, int gameSum) {
    int[] dice = new int[NUM_DICE]; // The dice to roll
    int turnSum = 0;                // The competitor's potential turn sum
    // 1 loop = 1 dice roll
    do {
      rollDice(dice);
      System.out.println(competitor + " rolled " + NUM_NAMES[dice[0]] + " + " + NUM_NAMES[dice[1]]);
      // Check the special conditions and act accordingly
      if (dice[0] == dice[1]) {
        if (dice[0] == 1) {
          System.out.println("DOUBLE ONES!");
          turnSum += dice[0] + dice[1] + 25;
        } else {
          System.out.println("DOUBLES!");
          turnSum += (dice[0] + dice[1]) * 2;
        }
        System.out.println(competitor + "'s turn sum is: " + turnSum + " and game sum would be: " +
            (gameSum + turnSum) + "\n" +
            competitor + " must roll again!");
      } else if (dice[0] == 1 || dice[1] == 1) {
        System.out.println("TURN OVER! Turn sum is zero!");
        return gameSum;
      } else {
        turnSum += dice[0] + dice[1];
        System.out.println(competitor + "'s turn sum is: " + turnSum + " and game sum would be: " +
            (gameSum + turnSum));
      }
      // Short circuit the condition, asking the  player or computer to roll again when none of the
      // special conditions apply. Removes the need for a "rollAgain" boolean variable.
    } while (dice[0] == dice[1] || getDecision(competitor, gameSum, turnSum));
    return gameSum + turnSum;
  }

  /**
   * Simulates rolling two 6 sided dice by randomizing the values in a dice array of size 2.
   *
   * @param dice the dice that need to be rolled
   */
  private static void rollDice(int[] dice) {
    dice[0] = NUM_GENERATOR.nextInt(NUM_SIDES) + 1;
    dice[1] = NUM_GENERATOR.nextInt(NUM_SIDES) + 1;
  }

  /**
   * Calls a competitor specific decision making method based on whose turn it currently is.
   *
   * @param competitor the name of the game participant, either "Player" or "Computer"
   * @param gameSum    the competitor's game sum
   * @param turnSum    the competitor's turn sum
   *
   * @return true if the competitor wants to roll again, false otherwise
   */
  private static boolean getDecision(String competitor, int gameSum, int turnSum) {
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
   * @param prompt the action to repeat, referring to if the player wants to "Roll" or "Play" again
   *
   * @return true if the player wants to roll or play again, false otherwise
   */
  private static boolean getPlayerDecision(String prompt) {
    String playerDecision;
    // Loop until the player inputs the correct response
    do {
      System.out.print(prompt + " again? (Enter 'y' or 'n'): ");
      playerDecision = PLAYER_INPUT.nextLine();
      if (!playerDecision.equals("y") && !playerDecision.equals("n")) {
        System.out.println(playerDecision + " is not 'y' nor 'n'. Please try again.");
      }
    } while (!playerDecision.equals("y") && !playerDecision.equals("n"));
    return playerDecision.equals("y");
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
    return (computerSum + turnSum) < MAX_SUM && turnSum < 40;
  }

  /**
   * Prints the intro and rules of the game to the console.
   */
  private static void printIntroductionAndRules() {
    System.out.println("\nHello player, and welcome to the 2 six-sided dice version of the " +
        "game of Pig!\n" +
        "You will be competing against the computer.\n\n" +
        "Here are the rules:\n\n" +
        "\t- The first player to accumulate a score of " + MAX_SUM + " or more wins.\n" +
        "\t- The human goes first.\n" +
        "\t- After one roll, a player has the choice to \"hold\" or to roll again.\n" +
        "\t- Two dice are rolled. Certain conditions apply:\n" +
        "\t- If both dice are ones, then you add 25 to your turn score, and you must roll again"
        + ".\n" +
        "\t\t- If one dice is one, then your turn is over and your turn score is set to zero.\n" +
        "\t\t- If both dice match (\"doubles\"), other than ones, then you gain twice the sum of" +
        " the dice, and you must roll again.\n" +
        "\t\t  For example if you rolled two fours, you would gain 16 and then have to roll again"
        + ".\n" +
        "\t\t- For any other dice combination, you just add the dice total to your turn score and" +
        " you have the choice of rolling again.\n" +
        "\t- When your turn is over, either through your choice or you rolled a one, then your" +
        " turn sum is added to your accumulated score.\n\n" +
        "Good luck!");
  }
}
