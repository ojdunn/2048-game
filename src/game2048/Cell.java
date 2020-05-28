package game2048;
/**
 * Cell class: stores the location and value of a cell on a 2D array.
 * Created by Hans Dulimarta (Summer 2014)
 * Edited by Owen Dunn (2/21/17)
 */
public class Cell implements Comparable<Cell> {
    public int row, column, value;

    public Cell()
    {
        this(0,0,0);
    }
    public Cell (int r, int c, int v)
    {
        row = r;
        column = c;
        value = v;
    }

    /* must override equals to ensure List::contains() works
     * correctly
     */
    @Override
    public boolean equals(Object c) {
        if(compareTo((Cell)c) == 0)
            return true;
        return false;
    }

    @Override
    public int compareTo (Cell other) {
        if (this.row < other.row) return -1;
        if (this.row > other.row) return +1;

        /* break the tie using column */
        if (this.column < other.column) return -1;
        if (this.column > other.column) return +1;

        return this.value - other.value;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
