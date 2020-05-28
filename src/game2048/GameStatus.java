package game2048;

public enum GameStatus {
    IN_PROGRESS, /* game is still in progress */
    USER_WON,    /* the player is able to add the numbers to the goal value */
    USER_LOST    /* no more move possible and no tiles with the goal value on the board */
}
