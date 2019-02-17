/*
 * Author: Bryan Hoang
 *
 * The code has been formatted to conform with the Google Java Style Guide
 */

import java.util.Random;
import java.util.Scanner;

/**
 * A class that allows a person to play the two dice version of the game of Pig
 * against the computer.
 */
public class GameOfPig {

  private static final Random NUM_GENERATOR = new Random(System.currentTimeMillis());
  private static final Scanner PLAYER_INPUT = new Scanner(System.in);
  private static final int NUM_DICE = 2;
  // Can modify the max score for the game and number of sides of the dice
  private static final int NUM_SIDES = 6;
  private static final int MAX_SUM = 100;

  /**
   * Displays the game instructions and starts the game method, looping to start new games if the
   * player wishes to play the game again.
   *
   * @param args The command line arguments (unused).
   */
  public static void main(String[] args) {
    printIntroAndRules();
    do {
      playGame();
    } while (getPlayerDecision("Play"));
    System.out.println("\nThanks for playing! Have a wonderful day!");
  }

  /**
   * Simulates playing the two-dice version of the game of Pig against the computer by looping
   * through turns, ending once either competitor scores above the maximum.
   */
  private static void playGame() {
    int playerSum = 0;
    int computerSum = 0;
    int turn = 1;
    playerSum = playTurns(playerSum, computerSum, turn);
    printWinner(playerSum);
  }

  /**
   * Carries out the main portion of the game through the turns between the Player and the Computer.
   *
   * @param playerSum   The player's game sum.
   * @param computerSum The computer's game sum.
   * @param turn        The starting turn number.
   *
   * @return The player's final game sum which will be used to decide who won.
   */
  private static int playTurns(int playerSum, int computerSum, int turn) {
    while (playerSum < MAX_SUM && computerSum < MAX_SUM) {
      // A round starts on every odd turn with the player going first
      askToStartNextTurn(turn);
      if (turn++ % 2 == 1) {
        System.out.println("Player's turn:\n");
        playerSum += playTurn("Player", playerSum);
      } else {
        System.out.println("\nComputer's turn:\n");
        computerSum += playTurn("Computer", computerSum);
      }
      reportGameSums(playerSum, computerSum);
    }
    return playerSum;
  }

  /**
   * Asks the player to start the next turn.
   *
   * @param turn The current turn number.
   */
  private static void askToStartNextTurn(int turn) {
    System.out.println(
        "\nPress <enter> to start round " + (turn + 1) / 2 + ", turn " + turn + " (" +
            (turn % 2 == 1 ? "Player's" : "Computer's") + " turn).");
    PLAYER_INPUT.nextLine();
  }

  /**
   * Simulates either the player's or computer's turn by looping through dice rolls and handling
   * the appropriate conditions.
   *
   * @param competitor The name of the game participant, either "Player" or "Computer".
   * @param gameSum    The competitor's game sum.
   *
   * @return The turn sum.
   */
  private static int playTurn(String competitor, int gameSum) {
    int[] dice = new int[NUM_DICE];
    int turnSum = 0;
    int rollSum;
    do {
      rollSum = getRollSum(competitor, dice);
      if (isTurnOver(rollSum)) {
        return 0;
      }
      turnSum += rollSum;
      reportTurnAndGameSums(competitor, turnSum, gameSum);
      if (dice[0] == dice[1]) {
        System.out.println(competitor + " must roll again!");
      }
      // Short circuit the conditional, asking the competitor to roll again when none of
      // the special conditions apply.
    } while (dice[0] == dice[1] || getDecision(competitor, gameSum, turnSum));
    return turnSum;
  }

  /**
   * Retrieves the roll sum after rolling the dice.
   *
   * @param competitor The name of the game participant, either "Player" or "Computer".
   * @param dice       The dice to roll.
   *
   * @return The roll sum.
   */
  private static int getRollSum(String competitor, int[] dice) {
    rollDice(dice);
    reportRoll(competitor, dice);
    return adjustRollSum(dice);
  }

  /**
   * Checks if the competitor's turn is over due to rolling a single 1.
   *
   * @param rollSum The competitor's roll sum.
   *
   * @return True if the competitor's roll sum is 0, false otherwise.
   */
  private static boolean isTurnOver(int rollSum) {
    return rollSum == 0;
  }

  /**
   * Simulates rolling two 6 sided dice by randomizing the values in a dice array of size 2.
   *
   * @param dice The dice that need to be rolled.
   */
  private static void rollDice(int[] dice) {
    dice[0] = NUM_GENERATOR.nextInt(NUM_SIDES) + 1;
    dice[1] = NUM_GENERATOR.nextInt(NUM_SIDES) + 1;
  }

  /**
   * Adjusts and returns the appropriate roll sum after checking and displaying if both dice are
   * double ones, doubles, or 1 of them is a one.
   *
   * @param dice The rolled dice.
   *
   * @return The adjusted roll sum.
   */
  private static int adjustRollSum(int[] dice) {
    if (dice[0] + dice[1] == 2) {
      System.out.println("DOUBLE ONES!");
      return 25;
    } else if (dice[0] == dice[1]) {
      System.out.println("DOUBLES!");
      return 2 * (dice[0] + dice[1]);
    } else if (dice[0] == 1 || dice[1] == 1) {
      System.out.println("TURN OVER! Turn sum is zero!");
      return 0;
    }
    return dice[0] + dice[1];
  }

  /**
   * Calls a competitor specific decision making method based on whose turn it currently is.
   *
   * @param competitor The name of the game participant, either "Player" or "Computer".
   * @param turnSum    The competitor's turn sum.
   * @param gameSum    The competitor's game sum.
   *
   * @return True if the competitor wants to roll again, false otherwise.
   */
  private static boolean getDecision(String competitor, int gameSum, int turnSum) {
    if (competitor.equals("Player")) {
      if (!getPlayerDecision("Roll")) {
        System.out.println("The " + competitor + " has decided to end their turn.");
        return false;
      }
      return true;
    }
    if (!getComputerDecision(gameSum, turnSum)) {
      System.out.println("The " + competitor + " has decided to end their turn.");
      return false;
    }
    return true;
  }

  /**
   * Prompts the player if they want to roll or play again and returns true or false based on the
   * input. Makes sure the player types the correct input by prompting them again after invalid
   * inputs.
   *
   * @param prompt The action to repeat, referring to if the player wants to "Roll" or "Play" again.
   *
   * @return True if the player wants to roll or play again, false otherwise.
   */
  private static boolean getPlayerDecision(String prompt) {
    String playerDecision;
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
   * @param computerSum The computer's game sum.
   * @param turnSum     The computer's current turn sum.
   *
   * @return True if the computer wants to roll again, false otherwise.
   */
  private static boolean getComputerDecision(int computerSum, int turnSum) {
    return (computerSum + turnSum) < MAX_SUM && turnSum < 40;
  }

  /**
   * Translates a roll number into a word.
   *
   * @param roll The roll number.
   *
   * @return The name of the dice of the roll.
   */
  private static String getRollName(int roll) {
    String[] numNames = {"one", "two", "three", "four", "five", "six"};
    return numNames[roll - 1];
  }

  /**
   * Reports the current dice roll for both dice and for either competitor.
   *
   * @param competitor The name of the game participant, either "Player" or "Computer".
   * @param dice       The rolled dice.
   */
  private static void reportRoll(String competitor, int[] dice) {
    System.out.println(
        competitor + " rolled " + getRollName(dice[0]) + " + " + getRollName(dice[1]));
  }

  /**
   * Reports the game sums for both players.
   *
   * @param playerSum   The player's game sum.
   * @param computerSum The computer's game sum.
   */
  private static void reportGameSums(int playerSum, int computerSum) {
    System.out.println("\nPlayer's sum is: " + playerSum +
        ", Computer's sum is: " + computerSum + ".");
  }

  /**
   * Reports the current turn sum as well as the potential game sum for either competitor.
   *
   * @param competitor The name of the game participant, either "Player" or "Computer".
   * @param turnSum    The competitor's current turn sum.
   * @param gameSum    The competitor's game sum.
   */
  private static void reportTurnAndGameSums(String competitor, int turnSum, int gameSum) {
    System.out.println(competitor + "\'s turn sum is: " + turnSum +
        " and game sum would be: " + (gameSum + turnSum) + ".");
  }

  /**
   * Displays the winner of the game.
   *
   * @param playerSum The player's game sum.
   */
  private static void printWinner(int playerSum) {
    if (playerSum >= MAX_SUM) {
      System.out.println("\n*****The Player wins!*****\n");
    } else {
      System.out.println("\n*****The Computer wins!*****\n");
    }
  }

  /**
   * Prints the intro and rules of the game to the console.
   */
  private static void printIntroAndRules() {
    System.out.println("\nHello player, and welcome to the 2 " + getRollName(NUM_SIDES) +
        "-sided dice version of the game of Pig!\n" +
        "You will be competing against the computer.\n\n" +
        "Here are the rules:\n\n" +
        "\t- The first player to accumulate a score of " + MAX_SUM + " or more wins.\n" +
        "\t- The human goes first.\n" +
        "\t- After one roll, a player has the choice to \"hold\" or to roll again.\n" +
        "\t- Two dice are rolled. Certain conditions apply:\n" +
        "\t- If both dice are ones, then you add 25 to your turn score, and you must roll again.\n"
        +
        "\t\t- If one dice is one, then your turn is over and your turn score is set to zero.\n" +
        "\t\t- If both dice match (\"doubles\"), other than ones, then you gain twice the sum of" +
        " the dice, and you must roll again.\n" +
        "\t\t  For example if you rolled 2 fours, you would gain 16 and then have to roll again.\n"
        +
        "\t\t- For any other dice combination, you just add the dice total to your turn score and"
        + " you have the choice of rolling again.\n" +
        "\t- When your turn is over, either through your choice or you rolled a one, then your"
        + " turn sum is added to your accumulated score.\n\n" +
        "Good luck!");
  }

}
