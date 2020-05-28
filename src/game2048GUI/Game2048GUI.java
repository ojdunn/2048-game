package game2048GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import game2048.*; // game logic package

/*********************************************************************
 * Game2048GUI class: This class creates a GUI for the GAME2048 class
 * and game2048 package it is a part of to handle all game logic.
 *
 * @author Owen Dunn
 * @version 3/21/2017, Project 3, CIS 163
 ********************************************************************/
public class Game2048GUI extends JPanel {
    /** 2048 game logic object using game2048 package */
    private game2048.Game2048 game;
    /** top-level frame object */
    private JFrame frame;
    /** panel for board */
    private JPanel boardPanel;
    /** Grid of JLabels for game board */
    private JLabel[][] gridLabel;
    /** Grid of values for game board */
    private int[][] gridValue;
    /** number of rows, columns for JFrames and values */
    private int rows, cols;
    /** standard size for rows and columns, standard winning value */
    private final static int DEFAULT_SIZE = 4, DEFAULT_WIN = 2048;
    /** stores if the user wants to play game again */
    private int playAgain;
    /** stores statistics of current game */
    private int moves;
    /** stores statistics for all games played */
    private int allMoves, highScore, numGames;
    /** panel for statistics */
    private JPanel statsPanel, movesPanel;
    /** statistics display labels for statistics panel */
    private JLabel movesLabel, allMovesLabel, highScoreLabel,
            numGamesLabel;
    /** panel for buttons */
    private JPanel buttonPanel;
    /** JButtons for some game actions: exit, reset, resize board */
    private JButton exitButton, resetButton, resizeButton, undoButton;
    /** listener for keyboard and JButton events */
    private Listener listener;

    /*****************************************************************
     * Constructor for the GUI object.
     ****************************************************************/
    public Game2048GUI() {
        game = new Game2048();
        setBoard(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_WIN);

        // create listener for keyboard and button events
        listener = new Listener();

        renderFrame();
        // add game board to center frame
        renderBoard();
        // set up key listener for board grid panel
        frame.addKeyListener(listener);
        // set up statistics panel
        renderStats();
        // set up the exit, reset, and resize buttons
        renderButtons();
    }

    /*****************************************************************
     * This method sets up the board for the GUI with the current set
     * rows and columns.
     *
     * @param row the rows in game
     * @param col the columns in game
     * @param winValue the winning game score
     * @return none
     ****************************************************************/
    private void setBoard(int row, int col, int winValue) {
        try {
            game.resizeBoard(row, col, winValue);
            gridValue = new int[row][col];
            rows = game.getRows();
            cols = game.getCols();
            game.placeRandomValue();
            game.placeRandomValue();
        }
        catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog
                    (frame, "Please enter reasonable row and column values.");
        }
    }

    /*****************************************************************
     * Uses JFrame and BorderLayout to create the top level for the
     * GUI. The North, East, South, West, and Center frames are left
     * to be used for different panels.
     *
     * @return none
     ****************************************************************/
    private void renderFrame() {
        // create top-level frame object
        frame = new JFrame("2048 Game");
        frame.setLayout(new BorderLayout());
        //frame.setBackground(Color.lightGray);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600,600));
        frame.pack();
        frame.setVisible(true);
    }

    /*****************************************************************
     * Use game logic to generate the board with a 2D grid of JFrame
     * objects. These JFrame objects will be updated as the game logic
     * decides.
     *
     * @return none
     ****************************************************************/
    private void renderBoard() {
        // reset all grid elements to zero
        for(int k = 0; k < gridValue.length; k++)
            for(int m = 0; m < gridValue[k].length; m++)
                gridValue[k][m] = 0;
        // update rows and columns from game logic
        rows = game.getRows();
        cols = game.getCols();

        // Create the board panel if not done so yet
        if(boardPanel == null) {
            //frame.remove(boardPanel);
            // set to grid layout before adding all JLabels
            boardPanel = new JPanel();
            // setting opaque
            boardPanel.setOpaque(true);
            boardPanel.setLayout(new GridLayout(rows, cols));
            // create 2D matrix of JLabels for board
            gridLabel = new JLabel[rows][cols];

            // initialize all JLabels (update values later)
            for(int i = 0; i < rows; i++)
                for(int j = 0; j < cols; j++) {
                    // Set JLabel text and alignment
                    gridLabel[i][j] =
                            new JLabel(" ", SwingConstants.CENTER);
                    gridLabel[i][j].setFont
                            (gridLabel[i][j].getFont().deriveFont
                                    (Font.BOLD, 40));
                    // must set JFrame to opaque to set color
                    gridLabel[i][j].setOpaque(true);
                    // based on the number in the space, set the color
                    // Color method produces darker colors closer to 0
                    gridLabel[i][j].setBackground
                            (new Color(255,255,255));
                    // set the border for each JLabel
                    gridLabel[i][j].setBorder
                            (BorderFactory.createRaisedBevelBorder());
                    boardPanel.add(gridLabel[i][j]);
                }
            // add board to center panel of BorderLayout
            frame.add(boardPanel, BorderLayout.CENTER);
        }
        else { // update existing grid of cells in GUI
            // update all non-empty tiles from board
            for(Cell c : game.getNonEmptyTiles()) {
                gridValue[c.row][c.column] = c.value;
            }

            // update all JLabels
            for(int i = 0; i < rows; i++)
                for(int j = 0; j < cols; j++) {
                    // Set JLabel text
                    if(gridValue[i][j] > 0) {
                        gridLabel[i][j].setText("" + gridValue[i][j]);

                        // update high score if made
                        if(gridValue[i][j] > highScore) {
                            highScore = gridValue[i][j];
                            highScoreLabel.setText
                                    ("High Score: " + highScore);
                        }
                    }
                    else {
                        gridLabel[i][j].setText(" ");
                    }
                }
        }

        frame.pack(); // display doesn't update well without
    }

    /*****************************************************************
     * Private method used to render the statistics panel for the GUI.
     *
     * @return none
     ****************************************************************/
    private void renderStats() {
        // create stats panel with border layout
        statsPanel = new JPanel();
        statsPanel.setLayout(new BorderLayout());

        // create a panel for moves to place within stats panel
        movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel,
                BoxLayout.Y_AXIS));
        // create moves frames for current and all games
        movesLabel = new JLabel("Moves: " + moves);
        allMovesLabel = new JLabel("All Moves: " + allMoves);
        // add moves labels to moves panel
        movesPanel.add(movesLabel);
        movesPanel.add(allMovesLabel);
        movesPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        movesPanel.setBackground(Color.lightGray);
        // add moves panel to stats panel west
        statsPanel.add(movesPanel, BorderLayout.WEST);
        //statsPanel.setBorder
        //(BorderFactory.createLineBorder(Color.BLACK));
        //statsPanel.setBackground(Color.lightGray);

        // create a label for high score
        highScoreLabel = new JLabel
                ("High Score: " + highScore, SwingConstants.CENTER);
        highScoreLabel.setBorder
                (BorderFactory.createLoweredBevelBorder());
        highScoreLabel.setOpaque(true);
        highScoreLabel.setBackground(Color.green);
        statsPanel.add(highScoreLabel, BorderLayout.CENTER);

        // create a label for number of games played
        numGamesLabel = new JLabel
                ("Number of Games Played: " + numGames);
        numGamesLabel.setBorder
                (BorderFactory.createLoweredBevelBorder());
        numGamesLabel.setOpaque(true);
        numGamesLabel.setBackground(Color.lightGray);
        statsPanel.add(numGamesLabel, BorderLayout.EAST);

        // add stats panel to south of GUI
        frame.add(statsPanel, BorderLayout.SOUTH);
        frame.pack();
    }

    /*****************************************************************
     * Private method used to render buttons for the game to allow the
     * user to exit the program, reset the game, or resize the game
     * board to a reasonable size.
     *
     * @return none
     ****************************************************************/
    private void renderButtons() {
        // create panel for all buttons
        buttonPanel = new JPanel();

        //undoButton
        undoButton = new JButton("Undo");
        undoButton.addActionListener(listener);
        buttonPanel.add(undoButton);

        //resetButton
        resetButton = new JButton("Reset");
        resetButton.addActionListener(listener);
        buttonPanel.add(resetButton);

        //resizeButton
        resizeButton = new JButton("Resize");
        resizeButton.addActionListener(listener);
        buttonPanel.add(resizeButton);

        // button to exit program
        exitButton = new JButton("Exit");
        exitButton.addActionListener(listener);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.NORTH);
    }

    /*****************************************************************
     * Private method used to reset the game with the current set rows
     * and columns.
     *
     * @return none
     ****************************************************************/
    private void resetGame() {
        // generate a new board and display it with current rows and
        // columns
        // reset game logic (places 2 random values) and GUI
        game.reset();
        renderBoard();
        // update moves label and reset count for next game
        moves = 0;
        movesLabel.setText("Game Moves: " + moves);
    }

    /*****************************************************************
     * Use game logic to generate the board with a 2D grid of JFrame
     * objects. These JFrame objects will be updated as the game logic
     * decides.
     *
     * @param
     * @return
     * @throws
     ****************************************************************/
    public void play() {
        renderBoard();
    }

    /*****************************************************************
     * This represents a listener for all keyboard pressing events and
     * on screen GUI button pressing events. Arrow key presses
     * cause the game to respond to user slide direction input. When
     * a key action event is detected, the 2048 game logic methods are
     * run and the GUI is updated. In addition, the exit, reset,
     * resize, and undo actions are performed when the respective
     * buttons are pressed.
     ****************************************************************/
    private class Listener implements KeyListener, ActionListener {
        /*************************************************************
         * This method activates whenever a key is pressed down. The
         * arrow keys from a keyboard are used to control the slide
         * direction for the game. When one of the direction keys is
         * pressed, the board will slide and update the GUI.
         * @Override
         ************************************************************/
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                //non-numpad up, down, left, right arrow key pressed cases
                case KeyEvent.VK_UP:
                    game.slide(game2048.SlideDirection.UP);
                    moves++;
                    allMoves++;
                    break;
                case KeyEvent.VK_DOWN:
                    game.slide(game2048.SlideDirection.DOWN);
                    moves++;
                    allMoves++;
                    break;
                case KeyEvent.VK_LEFT:
                    game.slide(game2048.SlideDirection.LEFT);
                    moves++;
                    allMoves++;
                    break;
                case KeyEvent.VK_RIGHT:
                    game.slide(game2048.SlideDirection.RIGHT);
                    moves++;
                    allMoves++;
                    break;
            }
            // update number of moves GUI labels
            movesLabel.setText("Game Moves: " + moves);
            allMovesLabel.setText("Total Moves: " + allMoves);
            // update GUI
            renderBoard();
            // find if status has changed: will check if win, lose,
            // or in progress
            game.updateStatus();

            //check game status: display dialog box and update
            // statistics
            if (game.getStatus() != GameStatus.IN_PROGRESS) {
                if(game.getStatus() == GameStatus.USER_LOST) {
                    playAgain = JOptionPane.showConfirmDialog
                            (frame, "You lost. :( Try again?");
                }
                else if(game.getStatus() == GameStatus.USER_WON) {
                    playAgain = JOptionPane.showConfirmDialog
                            (frame, "You won! :) Play again?");
                }

                // based on user choice, reset for new game,
                // update stats,...
                if(playAgain == JOptionPane.NO_OPTION) {
                    // update statistics only
                    //updateStats();
                    // exit frame?
                    frame.dispatchEvent
                            (new WindowEvent
                                    (frame, WindowEvent.WINDOW_CLOSING));
                }
                else if(playAgain == JOptionPane.YES_OPTION) {
                    // update stats and start a new game with same
                    // dimensions
                    resetGame();
                }
                numGames++;
                numGamesLabel.setText
                        ("Number of Games Played: " + numGames);
            }
        }
        public void keyReleased(KeyEvent e2) {}
        public void keyTyped(KeyEvent e3) {}

        /*************************************************************
         * This method activates whenever a key is pressed down. The
         * arrow keys from a keyboard are used to control the slide
         * direction for the game. When one of the direction keys is
         * pressed, the board will slide and update the GUI.
         * @Override
         ************************************************************/
        public void actionPerformed(ActionEvent e) {
            // local variables to store resized board dimensions
            // before confirming or denying them
            int tempRow, tempCol;

            if(e.getSource() == undoButton) {
                // undo a move
                try {
                    game.undo();

                    // update move counts
                    moves--;
                    allMoves--;
                }
                catch(IllegalStateException ex) {
                    // let user know no more undo's left
                    JOptionPane.showMessageDialog
                            (frame, "Game at start already!");
                }
                // update statistics and GUI for undo action
                renderBoard();
                movesLabel.setText("Game Moves: " + moves);
                allMovesLabel.setText("Total Moves: " + allMoves);
            }

            if(e.getSource() == resetButton) {
                // reset game with current dimensions
                resetGame();
            }

            if(e.getSource() == resizeButton) {
                // resize game dimensions and start new game
                tempRow = Integer.parseInt
                        (JOptionPane.showInputDialog
                                (frame, "Enter rows:"));
                tempCol = Integer.parseInt
                        (JOptionPane.showInputDialog
                                (frame, "Enter columns:"));
                setBoard(tempRow, tempCol, DEFAULT_WIN);
                // delete old board and create new
                frame.remove(boardPanel);
                boardPanel.removeAll();
                boardPanel = null;
                // create rows and cols JFrames
                renderBoard();
                renderBoard();
                moves = 0;
                movesLabel.setText("Game Moves: " + moves);
            }

            if(e.getSource() == exitButton) {
                // exit program
                frame.dispatchEvent
                        (new WindowEvent(frame,
                                WindowEvent.WINDOW_CLOSING));
            }

            // allow keyboard input to be taken after button press
            frame.requestFocus();
        }
    }

    public static void main(String[] arg) {
        Game2048GUI g = new Game2048GUI();
        g.play();
    }
}