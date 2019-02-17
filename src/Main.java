import EIGHT_PUZZLE.*;
public class Main {
    public static void main(String args[]){
        int[] x = {Block.EMPTY,1,2,4,3,7,6,5,8};
        new Eight_Puzzle_Problem(x,Eight_Puzzle_Problem.Heuristics.MANHATTAN);
    }
}
