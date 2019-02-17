package EIGHT_PUZZLE;

public class BOARD {
    Block[][] board;
    BOARD(Block[][] b){
        this.board = new Block[b.length][b[0].length];
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[i].length;j++){
                board[i][j] = new Block();
                board[i][j].value = b[i][j].value;
            }
        }
    }
}
