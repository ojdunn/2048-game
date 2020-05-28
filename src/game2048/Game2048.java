package game2048;

import java.util.*;
/***********************************************************************
 * Game2048 class: This class implements the NumberSlider interface.
 * Additional methods were added to help test the methods and to
 * provide help.
 *
 * Note: Cell class was changed. Must use to pass all JUnit tests.
 *
 * @author Owen Dunn
 * @version 1.0
 **********************************************************************/
public class Game2048 implements NumberSlider {
    /** The number of rows (height) and columns (width). */
    private int rows, cols;
    /** The winning game value. */
    private int winningValue;
    /** The winning game value. */
    private int[][] board;
    /** This object is used to generate random values. */
    private static Random gen = new Random();
    /** Enumerated variable to keep track of game status. */
    private GameStatus status = GameStatus.IN_PROGRESS;
    /** A list of all the non-empty tiles on the board */
    private ArrayList<Cell> nonEmpty;
    /** Stack to store all the states of the board to allow undo */
    private Stack<ArrayList<Cell>> st = new Stack<ArrayList<Cell>>();

    /******************************************************************
     * Reset the game logic to handle a board of a given dimension
     *
     * @param height the number of rows in the board
     * @param width the number of columns in the board
     * @param winningValue the value that must appear on the board to
     * win the game
     * @return none
     * @throws IllegalArgumentException when the winning value is not
     * power of two or negative
     * @Override
     *****************************************************************/
    public void resizeBoard(int height, int width, int winningValue) {
        // Make sure win value a power of 2, row/col valid values.
        // -want at least a 2x2 board
        // set to 16 max as display difficult beyond that with JPanel GUI
        // I removed this limit to allow a Unit test to pass, so keep this is mind.
        if( height > 1 && width > 1 && isBase2(winningValue)
               /*&& height <= 16 && width <= 16*/) {
            rows = height;
            cols = width;
            this.winningValue = winningValue;

            // create a blank 2D array (all zeros) for board
            board = new int[rows][cols];
            status = GameStatus.IN_PROGRESS;
        }
        else
            throw new IllegalArgumentException();
    }

    /*****************************************************************
     * Remove all numbered tiles from the board and place
     * TWO non-zero values at random location
     *
     * @return none
     * @Override
     ****************************************************************/
    public void reset() {
        // number of values to start with randomly on new game
        int nValues = 2;
        board = new int[rows][cols]; // all zeros again

        // place 2 nonzero values at 2 random locations
        for(int i = 0; i < nValues; i++)
            placeRandomValue();

        // game returned to in progress state
        status = GameStatus.IN_PROGRESS;
    }

    /*****************************************************************
     * Set the game board to the desired values given in the 2D array.
     * This method should use nested loops to copy each element from the
     * provided array to your own internal array. Do not just assign the
     * entire array object to your internal array object. Otherwise, your
     * internal array may get corrupted by the array used in the JUnit
     * test file. This method is mainly used by the JUnit tester.
     *
     * @param ref
     * @return none
     * @Override
     ****************************************************************/
    public void setValues(int[][] ref) {
        // include check for equal size arrays?
        // copy ref array into board array
        for(int i = 0; i < ref.length; i++)
            for(int j = 0; j < ref[i].length; j++)
                board[i][j] = ref[i][j];

        getNonEmptyTiles();
        // update game status
        updateStatus();
    }

    /****************************************************************
     * Insert one random tile into an empty spot on the board.
     *
     * @return a Cell object with its row, column, and value attributes
     *  initialized properly
     * @throws IllegalStateException when the board has no empty cell
     * @Override
     ***************************************************************/
    public Cell placeRandomValue() {
        int tempR, tempC; // used to find empty spot

        // find an empty spot on the board (make own method?)
        // check for full board (game over, infinite loop)
        // -this is a game over condition
        if( isFullBoard() ) {
            status = GameStatus.USER_LOST;
            // return a blank cell (as must)
            return new Cell();
        }
        else {
            do {
                tempR = Game2048.gen.nextInt(rows);
                tempC = Game2048.gen.nextInt(cols);
            }while(board[tempR][tempC] != 0);

            // place cell on the found empty spot
            board[tempR][tempC] = genBase2();

            // return the new cell generated
            return new Cell(tempR, tempC, genBase2());
        }
    }

    /*******************************************************************
     * This method determines if the board is full. When the board is
     * full before a piece would otherwise be added, it is a game over.
     *
     * @return boolean
     ******************************************************************/
    private boolean isFullBoard() {
        // test game over: add this where needed to account for end of game
        getNonEmptyTiles(); // find all non-empty tiles
        if( nonEmpty.size() == (rows * cols) ) {
            return true;
        }
        return false;
    }

    /*******************************************************************
     * This method determines if the board has a winning condition.
     *
     * @return boolean
     ******************************************************************/
    private boolean isWin() {
        getNonEmptyTiles(); // find all non-empty tiles
        // Go through each non-empty cell to check for winning value
        for(Cell c : nonEmpty) {
            // shouldn't be possible to have greater
            if(c.value >= winningValue) {
                // update win status here?
                //status = GameStatus.USER_WON;
                return true;
            }
        }
        return false;
    }

    /*******************************************************************
     * This method determines the current game status based on the 2D
     * board.
     *
     * @return none
     ******************************************************************/
    public void updateStatus() {
        if( isFullBoard() && !movePossible() )
            status = GameStatus.USER_LOST;
        else if( isWin() )
            status = GameStatus.USER_WON;
        else
            status = GameStatus.IN_PROGRESS;
    }

    /*******************************************************************
     * This method determines if a move if possible.
     *
     * @return boolean
     ******************************************************************/
    private boolean movePossible() {
        // check if space available
        getNonEmptyTiles(); // find all non-empty tiles
        if( nonEmpty.size() == (rows * cols) ) {
            // check if 2 adjacent cells have the same value if full
            for(int i = 0; i < rows; i++)
                for(int j = 0; j < cols; j++) {
                    if(i < rows - 1 && j < cols - 1) {
                        if(board[i][j] == board[i][j + 1] ||
                                board[i][j] == board[i + 1][j]) {
                            //status = GameStatus.IN_PROGRESS;
                            return true;
                        }
                    }
                    else if(i == rows - 1 && j < cols - 1) {
                        if(board[i][j] == board[i][j + 1]) {
                            //status = GameStatus.IN_PROGRESS;
                            return true;
                        }
                    }
                }
            status = GameStatus.USER_LOST;
            return false;
        }
        status = GameStatus.IN_PROGRESS; // put here?
        return true;
    }

    /*****************************************************************
     * Slide all the tiles in the board in the requested direction
     *
     * @param dir move direction of the tiles
     * @return true when the board changes
     * @Override
     ****************************************************************/
    public boolean slide(SlideDirection dir) {
        // Store current state of the board to see if changed after
        // slide.
        int[][] temp = new int[rows][cols];
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                temp[i][j] = board[i][j];

        // Store prior to updating game board
        st.push(getNonEmptyTiles());

        // check slide direction
        if(dir == SlideDirection.LEFT || dir == SlideDirection.RIGHT) {
            slideHorizontal(dir);
        }
        else if(dir == SlideDirection.UP ||
                dir == SlideDirection.DOWN) {
            slideVertical(dir);
        }
        else
            throw new IllegalArgumentException();

        // check if won game after slide
        if( isWin() )
            status = GameStatus.USER_WON;

        // determine if board changed: return true and place a new
        // random cell if it has
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                if(temp[i][j] != board[i][j]) {
                    // add a random cell to board after sliding
                    // -check board for status first, after?
                    placeRandomValue();
                    return true;
                }

        // if no movement made, remove latest state (a repeat)
        st.pop();
        return false;
    }

    /*******************************************************************
     * This method makes a horizontal move on the game board (left or
     * right).
     *
     * @param dir left or right
     * @return none
     ******************************************************************/
    private void slideHorizontal(SlideDirection dir) {
        // used to store one row at a time temporarily
        int[] array = new int[cols];
        ArrayList<Cell> oneRow = new ArrayList<Cell>();
        int start; // used in row scanning process
        // used to go left/right in scan process
        // -1 for right to left; +1 for right
        int factor;
        // Used to slide elements in temp array
        //  0 for left; 1 for right
        int place;
        // used to align cells after sliding
        boolean complete = false;
        boolean moved = false;
        int index, i, j;

        // find direction to guide slide logic
        if(dir == SlideDirection.LEFT) {
            factor = 1;
            place = 0;
            start = 0;
        }
        else if(dir == SlideDirection.RIGHT) {
            factor = -1;
            place = 1;
            start = cols - 1; // last cell of row
        }
        else
            throw new IllegalArgumentException();

        // store one row at a time to find final result before writing
        // to the board: only nonzero values
        getNonEmptyTiles(); // update board for ArrayList
        for(i = 0; i < rows; i++) { // go though each row
            // store non-zero for each row
            for(Cell c : nonEmpty) {
                if(c.row == i) {
                    oneRow.add(c);
                }
            }

            // leave row unchanged on board if empty
            if(oneRow.isEmpty()) {
                continue; // analyze next row
            }

            // use single row array list to create array of elements
            // with all valued cells next to each other
            // -aligned to left or right side
            index = start;
            for(Cell c : oneRow) { //
                array[index - place*(oneRow.size() - 1)] = c.value;
                index += 1; // update scan placement
                //System.out.print(c.value + " "); //test
            }
            //System.out.println("\n"); //test

            //System.out.print("array: "); //test
            //printArray(array); //test

            // reset loop start index variable
            index = start;

            // Combine all first, then fill all gaps once done (two loops)
            // -ternary operator used for loop condition
            for(j = start;
                start == 0 ? j < cols - 1 : j > 0;
                j += factor) {
                if(array[j] != 0 && array[j] == array[j + factor]) {
                    array[j] *= 2; // double value (combined two cells)
                    array[j + factor] = 0; // clear slid over value
                }
            }
            // remove all zero gaps
            // start at cell "cols - 2" for right, 1 for left
            while(!complete) {
                for(j = factor + place*start;
                    start == 0 ? j < cols : j >= 0;
                    j += factor) {
                    if(array[j] != 0 && array[j - factor] == 0) {
                        array[j - factor] = array[j];
                        array[j] = 0;
                        moved = true;
                    }
                }
                if(!moved)
                    complete = true;

                moved = false; // reset for next loop
            }
            complete = false; // reset for next loop

            // assign to board (done for this row)
            for(j = 0; j < cols; j++) {
                board[i][j] = array[j];
            }

            // clear list and set values to initial for next iteration
            oneRow.clear(); // clear nonzero element storing ArrayList
            array = new int[cols]; // reset to all zeros
        } // go to next row
        getNonEmptyTiles(); // update the board Cell objects
    }

    /*******************************************************************
     * This method makes a vertical move on the game board (up or
     * down).
     *
     * @param dir up or down
     * @return none
     ******************************************************************/
    private void slideVertical(SlideDirection dir) {
        // used to store one col at a time temporarily
        int[] array = new int[rows];
        ArrayList<Cell> oneCol = new ArrayList<Cell>();
        int start; // used in row scanning process
        // used to go left/right in scan process
        // -1 for right to left; +1 for right
        int factor;
        // Used to slide elements in temp array
        //  0 for left; 1 for right
        int place;
        // used to align cells after sliding
        boolean complete = false;
        boolean moved = false;
        int index, i, j;

        // find direction to guide slide logic
        if(dir == SlideDirection.UP) {
            factor = 1;
            place = 0;
            start = 0;
        }
        else if(dir == SlideDirection.DOWN) {
            factor = -1;
            place = 1;
            start = rows - 1; // last cell of row
        }
        else
            throw new IllegalArgumentException();

        // store one row at a time to find final result before writing
        // to the board: only nonzero values
        getNonEmptyTiles(); // update board for ArrayList
        for(i = 0; i < cols; i++) { // go though each row
            // store non-zero for each col
            for(Cell c : nonEmpty) {
                if(c.column == i) {
                    oneCol.add(c);
                }
            }

            // leave row unchanged on board if empty
            if(oneCol.isEmpty()) {
                continue; // analyze next column
            }

            // use single row array list to create array of elements
            // with all valued cells next to each other
            index = start;
            for(Cell c : oneCol) { //
                array[index - place*(oneCol.size() - 1)] = c.value;
                index += 1; // update scan placement
                //System.out.print(c.value + " "); //test
            }

            index = start;

            for(j = start;
                start == 0 ? j < rows - 1 : j > 0;
                j += factor) {
                if(array[j] != 0 && array[j] == array[j + factor]) {
                    array[j] *= 2; // double value (combined two cells)
                    array[j + factor] = 0; // clear slid over value
                }
            }
            // remove all zero gaps
            while(!complete) {
                for(j = factor + place*start;
                    start == 0 ? j < rows : j >= 0;
                    j += factor) {
                    if(array[j] != 0 && array[j - factor] == 0) {
                        array[j - factor] = array[j];
                        array[j] = 0;
                        moved = true;
                    }
                }
                if(!moved)
                    complete = true;

                moved = false; // reset for next loop
            }
            complete = false; // reset for next loop

            // assign to board (done for this row)
            for(j = 0; j < rows; j++) {
                board[j][i] = array[j];
            }

            // clear list and set values to initial for next iteration
            oneCol.clear(); // clear nonzero element storing ArrayList
            array = new int[rows]; // reset to all zeros
        } // go to next row
        getNonEmptyTiles(); // update the board Cell objects
    }

    /**********************************************************************
     * This method finds all the non-empty cells and stores them in an
     * ArrayList.
     *
     * @return an ArrayList of Cells. Each cell holds the (row,column) and
     * value of a tile
     * @Override
     *********************************************************************/
    public ArrayList<Cell> getNonEmptyTiles() {
        // create a new ArrayList to store nonempty cells
        nonEmpty = new ArrayList<Cell>();

        // search through 2D array to find non-empty cells
        // store each non-empty cell in the ArrayList
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                // create a cell for each non-empty location on board
                // to store within the ArrayList
                if(board[i][j] != 0) {
                    // assign the row, col of non-empty cell in cell object
                    //c = new Cell(i, j, board[i][j]);
                    // add this cell to the list
                    nonEmpty.add(new Cell(i, j, board[i][j]));
                }
            }
        }
        return nonEmpty;
    }

    /*******************************************************************
     * A method used to get the number of rows.
     *
     * @return rows the number of rows
     ******************************************************************/
    public int getRows() {
        return rows;
    }

    /*******************************************************************
     * A method used to get the number of columns.
     *
     * @return cols the number of columns
     ******************************************************************/
    public int getCols() {
        return cols;
    }

    /*******************************************************************
     * A method used to get the winning value for the game.
     *
     * @return winningValue must be base 2 number
     ******************************************************************/
    public int getWinningValue() {
        return winningValue;
    }

    /*******************************************************************
     * A method used to set the winning value for the game.
     *
     * @param winningValue an integer that must be base 2
     * @return none
     ******************************************************************/
    public void setWinningValue(int winningValue) {
        this.winningValue = winningValue;
    }

    /*******************************************************************
     * Return the current state of the game
     *
     * @return one of the possible values of GameStatus enum
     * @Override
     ******************************************************************/
    public GameStatus getStatus() {
        return status;
    }

    /*******************************************************************
     * Undo the most recent action, i.e. restore the board to its previous
     * state. Calling this method multiple times will ultimately restore
     * the game to the very first initial state of the board holding two
     * random values. Further attempt to undo beyond this state will throw
     * an IllegalStateException.
     *
     * @throws IllegalStateException when undo is not possible
     * @Override
     ******************************************************************/
    public void undo() {
        // remove top board state from stack and assign to board
        // add error check for empty stack
        if(!st.empty()) {
            // return 2d array object from top of stack, assign to board
            nonEmpty = st.pop();
            board = new int[rows][cols]; // clear old board first
            if(!nonEmpty.isEmpty()) {
                for(Cell c : nonEmpty) {
                    board[c.row][c.column] = c.value;
                }
            }
        }
        else
            throw new IllegalStateException();
    }

    /*******************************************************************
     * Determines if a number is base 2. Based on code
     * found: http://www.skorks.com/2010/10/write-a-function-to-
     * determine-if-a-number-is-a-power-of-2/
     *
     * @param number int
     * @return boolean
     ******************************************************************/
    private boolean isBase2(int number) {
        return (number != 0 && (number & (number - 1)) == 0);
    }

    /*******************************************************************
     * A method used to test the Game2048 class by printing the current
     * state of the board.
     *
     * @return none
     ******************************************************************/
    private void printBoard() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++)
                System.out.print(board[i][j] + "\t");
            System.out.println();
        }
        System.out.println("\n");
    }

    /*******************************************************************
     * A method used to test the Game2048 class by printing an array.
     *
     * @return none
     ******************************************************************/
    private void printArray(int[] a) {
        for(int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "\t");
        }
        System.out.println("\n");
    }

    /*******************************************************************
     * A method used to randomly generate a base 2 number up to 8.
     * This includes 1, 2, 4, 8.
     *
     * @return number: An integer of base 2 from 1 to 8
     ******************************************************************/
    private int genBase2() {
        return (int)Math.pow(2, Game2048.gen.nextInt(4));
    }

    /*******************************************************************
     * The main method used to test the Game2048 class.
     *
     * @return none
     ******************************************************************/
    public static void main(String[] args) {
        // Test basic game functionality: use TextUI class and
        // TenTwentyFourTester class JUnit for full testing
        Game2048 game = new Game2048();

        game.resizeBoard(4, 4, 128);
        for(int i=0;i<8;i++)
            game.placeRandomValue();
        game.printBoard();

        game.slide(SlideDirection.LEFT);
        game.printBoard();

        game.slide(SlideDirection.RIGHT);
        game.printBoard();
    }
}
