package game2048;

import java.util.ArrayList;
import java.util.Scanner;

public class TextUI {
    private NumberSlider game;
    private int[][] grid;
    private static int CELL_WIDTH = 3;
    private static String NUM_FORMAT, BLANK_FORMAT;
    private Scanner inp;

    public TextUI() {
        game = new Game2048();

        if (game == null) {
            System.err.println ("*---------------------------------------------*");
            System.err.println ("| You must first modify the UI program.       |");
            System.err.println ("| Look for the first TODO item in TextUI.java |");
            System.err.println ("*---------------------------------------------*");
            System.exit(0xE0);
        }
        // set board size and winning value here -> set low win score to test full game length
        game.resizeBoard(4, 4, 64);
        grid = new int[4][4];

        /* Set the string to %4d */
        NUM_FORMAT = String.format("%%%dd", CELL_WIDTH + 1);

        /* Set the string to %4s, but without using String.format() */
        BLANK_FORMAT = "%" + (CELL_WIDTH + 1) + "s";
        inp = new Scanner(System.in);
    }

    private void renderBoard() {
        /* reset all the 2D array elements to ZERO */
        for (int k = 0; k < grid.length; k++)
            for (int m = 0; m < grid[k].length; m++)
                grid[k][m] = 0;
        /* fill in the 2D array using information for non-empty tiles */
        for (Cell c : game.getNonEmptyTiles())
            grid[c.row][c.column] = c.value;

        /* Print the 2D array using dots and numbers */
        for (int k = 0; k < grid.length; k++) {
            for (int m = 0; m < grid[k].length; m++)
                if (grid[k][m] == 0)
                    System.out.printf (BLANK_FORMAT, ".");
                else
                    System.out.printf (NUM_FORMAT, grid[k][m]);
            System.out.println();
        }
    }

    /**
     * The main loop for playing a SINGLE game session. Notice that
     * the following method contains NO GAME LOGIC! Its main task is
     * to accept user input and invoke the appropriate methods in the
     * game engine.
     */
    public void playLoop() {
        /* Place the first two random tiles */
        game.placeRandomValue();
        game.placeRandomValue();
        renderBoard();

        /* To keep the right margin within 75 columns, we split the
           following long string literal into two lines
         */
        System.out.print ("Slide direction (W, A, S, D), " +
                "[U]ndo or [Q]uit? ");
        String resp = inp.next().trim().toUpperCase();
        while (!resp.equals("Q")) {
            switch (resp) {
                case "W": // up
                    game.slide(SlideDirection.UP);
                    renderBoard();
                    break;
                case "D": // right
                    game.slide(SlideDirection.RIGHT);
                    renderBoard();
                    break;
                case "S": // down
                    game.slide(SlideDirection.DOWN);
                    renderBoard();
                    break;
                case "A": // left
                    game.slide(SlideDirection.LEFT);
                    renderBoard();
                    break;
                case "U": // undo
                    try {
                        game.undo();
                        renderBoard();
                    } catch (IllegalStateException exp) {
                        System.err.println ("Can't undo that far");
                    }
            }
            if (game.getStatus() != GameStatus.IN_PROGRESS)
                break;
            System.out.print ("Slide direction (W, A, S, D), [U]ndo or [Q]uit? ");
            resp = inp.next().trim().toUpperCase();
        }

        switch (game.getStatus()) {
            case IN_PROGRESS:
                System.out.println ("Game in progress");
                break;
            case USER_WON:
                System.out.println ("You Win! :)");
                break;
            case USER_LOST:
                System.out.println ("You Lose! :( try again?");
                break;
        }
    }

    public static void main(String[] arg) {
        TextUI t = new TextUI();
        t.playLoop();
    }
}