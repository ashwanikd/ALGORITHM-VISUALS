package EIGHT_PUZZLE;

public class Path {
    Eight_Puzzle_Problem.Moves move;
    int cost;
    Path(Eight_Puzzle_Problem.Moves move,int cost){
        this.move = move;
        this.cost = cost;
    }
}
