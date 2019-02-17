package EIGHT_PUZZLE;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.event.*;
import java.awt.*;
import java.util.LinkedList;

public class Eight_Puzzle_Problem extends Frame implements GLEventListener {

    enum Moves{
        LEFT,RIGHT,UP,DOWN
    }
    public enum Heuristics{
        MANHATTAN,MISPLACED_TILES
    }
    boolean solved = false;
    Heuristics h;
    Button b = new Button("start");
    LinkedList<BOARD> visited;
    int INTERVAL = 200;
    int SIZE;
    LinkedList<Moves> result;
    int WIDTH = 500;
    int HEIGHT = 500;
    Block[][] GoalBoard;
    Block[][] Board;
    static GL gl;
    static GLCanvas canvas;
    static GLCapabilities capabilities;
    static GLProfile profile;
    static FPSAnimator animator;
    int row=0,col=0;

    void initializePlayBoard(int array[]){
        result = new LinkedList<Moves>();
        Board = new Block[SIZE][SIZE];
        GoalBoard = new Block[SIZE][SIZE];
        int x=0,y=1;
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                Board[i][j] = new Block();
                GoalBoard[i][j] = new Block();
                Board[i][j].x = 2*(2*j+1)/((double)(SIZE*2))-(double)(1);
                Board[i][j].y = (double)(1)-2*(2*i+1)/((double)(SIZE*2));
                if(array[x]==Block.EMPTY){
                    Board[i][j].colorx = 0;
                    Board[i][j].colory = 0;
                    Board[i][j].colorz = 0;

                }else {
                    Board[i][j].colorx = Math.random();
                    Board[i][j].colory = Math.random();
                    Board[i][j].colorz = Math.random();
                }
                Board[i][j].value = array[x++];
                if(Board[i][j].value == Block.EMPTY){
                    row=i;
                    col=j;
                }
                if(i==0 && j==0){
                    GoalBoard[i][j].value = Block.EMPTY;
                    continue;
                }
                GoalBoard[i][j].value = y++;
                GoalBoard[i][j].x = Board[i][j].x;
                GoalBoard[i][j].y = Board[i][j].y;
            }
        }
    }

    public Eight_Puzzle_Problem(int[] array,Heuristics h){
        this.h = h;
        double x = Math.sqrt((double)(array.length));
        if(x-Math.floor(x)!=0){
            System.out.println("invalid array");
            return;
        }
        visited = new LinkedList<>();
        int inversion = 0;
        int r=0,c=0;
        for(int i=0;i<array.length;i++){
            if(array[i]!=Block.EMPTY){
                for(int j=i+1;j<array.length;j++){
                    if(array[j]!=Block.EMPTY){
                        if(array[i]>array[j])
                            inversion++;
                    }
                }
            }
        }
        SIZE = (int)x;
        if(inversion%2!=0){
            System.out.println("*****Puzzle is not Solvable*****");
            return;
        }
        initializePlayBoard(array);
        profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        gl = canvas.getGL();

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                animator.stop();
                System.exit(0);
            }
        });
        b.setBounds(0,40,60,30);
        this.add(b);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                bestfirstsearch(Board,row,col);
                showResult();
                solved = true;
            }
        });

        this.add(canvas);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(d);
        this.setVisible(true);
        animator = new FPSAnimator(canvas,300,true);
        animator.start();
    }

    boolean isVisited(Block[][] board){
        BOARD t;
        if(visited.size()==0){
            return false;
        }
        boolean check = true;
        for(int i=0;i<visited.size();i++){
            t = visited.get(i);
            check = true;
            for(int j=0;j<SIZE;j++){
                for(int k=0;k<SIZE;k++){
                    if(board[j][k].value != t.board[j][k].value){
                        check = false;
                        break;
                    }
                }
                if(!check){
                    break;
                }
            }
            if(check)
                break;
        }
        return check;
    }

    void showResult(){
        int i=0;
        int j=result.size()-1;
        Moves temp;
        while (i<j){
            temp = result.get(i);
            result.set(i,result.get(j));
            result.set(j,temp);
            i++;j--;
        }
        int r=row,c=col;
        int tempBlock;
        try{
            Thread.sleep(INTERVAL);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(i=0;i<result.size();i++){
            canvas.display();
            System.out.println(result.get(i));
            if(result.get(i) == Moves.LEFT){
                leftvalue(Board,r,c);
                c--;
            }else if(result.get(i) == Moves.RIGHT){
                rightvalue(Board,r,c);
                c++;
            }else if(result.get(i) == Moves.UP){
                upvalue(Board,r,c);
                r--;
            }else if(result.get(i) == Moves.DOWN){
                downvalue(Board,r,c);
                r++;
            }
            try{
                Thread.sleep(INTERVAL);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Number of steps: "+result.size());
    }

    TextRenderer renderer;
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 50));
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }
    int frame=0;
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(gl.GL_DEPTH_BUFFER_BIT|gl.GL_COLOR_BUFFER_BIT);
        gl.glPointSize(20);
        gl.glLoadIdentity();

        double x = 1+Board[0][0].x;

        gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(1,1,1);
        int m = (int)Math.floor(1/x);
        for(int i=0;i<m-1;i++){
            double y = (i+1)*2*x;

            gl.glVertex3d(-1,1-y,0);
            gl.glVertex3d(1,1-y,0);

            gl.glVertex3d(y-1,-1,0);
            gl.glVertex3d(y-1,1,0);
        }
        gl.glEnd();
        x=Board[0][0].x/2;
        int W = canvas.getWidth()/2;
        int H = canvas.getHeight()/2;
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                //if(Board[i][j].value!=Block.EMPTY) {
                    //System.out.println(frame++);
                    gl.glBegin(GL2.GL_TRIANGLES);
                    gl.glColor3d(Board[i][j].colorx, Board[i][j].colory, Board[i][j].colorz);
                    if(solved){
                        gl.glColor3d(0,1,0);
                    }
                    gl.glVertex3d(Board[i][j].x - x, Board[i][j].y - x, 0);
                    gl.glVertex3d(Board[i][j].x - x, Board[i][j].y + x, 0);
                    gl.glVertex3d(Board[i][j].x + x, Board[i][j].y - x, 0);
                    gl.glVertex3d(Board[i][j].x + x, Board[i][j].y + x, 0);
                    gl.glVertex3d(Board[i][j].x - x, Board[i][j].y + x, 0);
                    gl.glVertex3d(Board[i][j].x + x, Board[i][j].y - x, 0);
                    gl.glEnd();
                //}
            }
        }
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                if(Board[i][j].value!=Block.EMPTY){
                    //System.out.println(frame++);
                    renderer.setColor(new Color(0,0,0));
                    renderer.beginRendering(canvas.getWidth(),canvas.getHeight());
                    try {
                        renderer.draw("" + Board[i][j].value, abs((int) ((Board[i][j].x * W) + W)), abs((int) (H + (Board[i][j].y * H))));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    renderer.endRendering();
                }
            }
        }
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    int abs(int x){
        if(x<0)
            return -x;
        else return x;
    }

    int heuristic(Block[][] tempBoard){
        if(h==Heuristics.MANHATTAN) {
            int x = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    boolean check = false;
                    for (int k = 0; k < SIZE; k++) {
                        for (int l = 0; l < SIZE; l++) {
                            if (tempBoard[i][j].value == GoalBoard[k][l].value) {
                                x = x + abs(k - i) + abs(l - j);
                                check = true;
                                break;
                            }
                        }
                        if (check) {
                            break;
                        }
                    }
                }
            }
            return x;
        }else {
            int x = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if(tempBoard[i][j].value!=GoalBoard[i][j].value){
                        x++;
                    }
                }
            }
            return x;
        }
    }

    int left(Block[][] tempBoard,int r,int c){
        Block temp;
        temp = tempBoard[r][c];
        tempBoard[r][c] = tempBoard[r][c-1];
        tempBoard[r][c-1] = temp;
        return heuristic(tempBoard);
    }

    void leftvalue(Block[][] tempBoard,int r,int c){
        int temp;
        double t;
        temp = tempBoard[r][c].value;
        tempBoard[r][c].value = tempBoard[r][c-1].value;
        tempBoard[r][c-1].value = temp;
        t = tempBoard[r][c].colorx;
        tempBoard[r][c].colorx = tempBoard[r][c-1].colorx;
        tempBoard[r][c-1].colorx = t;
        t = tempBoard[r][c].colory;
        tempBoard[r][c].colory = tempBoard[r][c-1].colory;
        tempBoard[r][c-1].colory = t;
        t = tempBoard[r][c].colorz;
        tempBoard[r][c].colorz = tempBoard[r][c-1].colorz;
        tempBoard[r][c-1].colorz = t;

    }

    int right(Block[][] tempBoard,int r,int c){
        Block temp;
        temp = tempBoard[r][c];
        tempBoard[r][c] = tempBoard[r][c+1];
        tempBoard[r][c+1] = temp;
        return heuristic(tempBoard);
    }

    void rightvalue(Block[][] tempBoard,int r,int c){
        int temp;
        double t;
        temp = tempBoard[r][c].value;
        tempBoard[r][c].value = tempBoard[r][c+1].value;
        tempBoard[r][c+1].value = temp;
        t = tempBoard[r][c].colorx;
        tempBoard[r][c].colorx = tempBoard[r][c+1].colorx;
        tempBoard[r][c+1].colorx = t;
        t = tempBoard[r][c].colory;
        tempBoard[r][c].colory = tempBoard[r][c+1].colory;
        tempBoard[r][c+1].colory = t;
        t = tempBoard[r][c].colorz;
        tempBoard[r][c].colorz = tempBoard[r][c+1].colorz;
        tempBoard[r][c+1].colorz = t;
    }

    int up(Block[][] tempBoard,int r,int c){
        Block temp;
        temp = tempBoard[r][c];
        tempBoard[r][c] = tempBoard[r-1][c];
        tempBoard[r-1][c] = temp;
        return heuristic(tempBoard);
    }

    void upvalue(Block[][] tempBoard,int r,int c){
        int temp;
        double t;
        temp = tempBoard[r][c].value;
        tempBoard[r][c].value = tempBoard[r-1][c].value;
        tempBoard[r-1][c].value = temp;
        t = tempBoard[r][c].colorx;
        tempBoard[r][c].colorx = tempBoard[r-1][c].colorx;
        tempBoard[r-1][c].colorx = t;
        t = tempBoard[r][c].colory;
        tempBoard[r][c].colory = tempBoard[r-1][c].colory;
        tempBoard[r-1][c].colory = t;
        t = tempBoard[r][c].colorz;
        tempBoard[r][c].colorz = tempBoard[r-1][c].colorz;
        tempBoard[r-1][c].colorz = t;
    }

    int down(Block[][] tempBoard,int r,int c){
        Block temp;
        temp = tempBoard[r][c];
        tempBoard[r][c] = tempBoard[r+1][c];
        tempBoard[r+1][c] = temp;
        return heuristic(tempBoard);
    }

    void downvalue(Block[][] tempBoard,int r,int c){
        int temp;
        double t;
        temp = tempBoard[r][c].value;
        tempBoard[r][c].value = tempBoard[r+1][c].value;
        tempBoard[r+1][c].value = temp;
        t = tempBoard[r][c].colorx;
        tempBoard[r][c].colorx = tempBoard[r+1][c].colorx;
        tempBoard[r+1][c].colorx = t;
        t = tempBoard[r][c].colory;
        tempBoard[r][c].colory = tempBoard[r+1][c].colory;
        tempBoard[r+1][c].colory = t;
        t = tempBoard[r][c].colorz;
        tempBoard[r][c].colorz = tempBoard[r+1][c].colorz;
        tempBoard[r+1][c].colorz = t;
    }

    int cccccc=0;

    boolean bestfirstsearch(Block[][] Board,int r,int c){
        if(isVisited(Board)){
            return false;
        }
        if(heuristic(Board) == 0){
            System.out.println("Found Solution");
            return true;
        }
        Block[][] tempBoard = new Block[SIZE][SIZE];
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                tempBoard[i][j] = new Block();
                tempBoard[i][j].value = Board[i][j].value;
            }
        }
        visited.add(new BOARD(tempBoard));
        Path[] moves = new Path[4];
        if(c>0){
            moves[0] = new Path(Moves.LEFT,left(tempBoard,r,c));
            left(tempBoard,r,c);
        }
        if(r>0){
            moves[1] = new Path(Moves.UP,up(tempBoard,r,c));
            up(tempBoard,r,c);
        }
        if(c<SIZE-1){
            moves[2] = new Path(Moves.RIGHT,right(tempBoard,r,c));
            right(tempBoard,r,c);
        }
        if(r<SIZE-1){
            moves[3] = new Path(Moves.DOWN,down(tempBoard,r,c));
            down(tempBoard,r,c);
        }

        Path temp;
        for(int i=0;i<4;i++){
            if(moves[i]!=null){
                for(int j=i+1;j<4;j++){
                    if(moves[j]!=null){
                        if(moves[i].cost>=moves[j].cost){
                            temp = moves[i];
                            moves[i] = moves[j];
                            moves[j] = temp;
                        }
                    }
                }
            }
        }

        for(int i=0;i<4;i++){
            System.out.println("Running...."+cccccc++);
            if(moves[i]!=null){
                if(moves[i].move == Moves.LEFT){
                    System.out.println("left");
                    leftvalue(tempBoard,r,c);
                    if(bestfirstsearch(tempBoard,r,c-1)){
                        result.add(Moves.LEFT);
                        return true;
                    }
                    leftvalue(tempBoard,r,c);
                }else if(moves[i].move == Moves.RIGHT){
                    System.out.println("right");
                    rightvalue(tempBoard,r,c);
                    if(bestfirstsearch(tempBoard,r,c+1)){
                        result.add(Moves.RIGHT);
                        return true;
                    }
                    rightvalue(tempBoard,r,c);
                }else if(moves[i].move == Moves.UP){
                    System.out.println("up");
                    upvalue(tempBoard,r,c);
                    if(bestfirstsearch(tempBoard,r-1,c)){
                        result.add(Moves.UP);
                        return true;
                    }
                    upvalue(tempBoard,r,c);
                }else if(moves[i].move == Moves.DOWN){
                    System.out.println("down");
                    downvalue(tempBoard,r,c);
                    if(bestfirstsearch(tempBoard,r+1,c)){
                        result.add(Moves.DOWN);
                        return true;
                    }
                    downvalue(tempBoard,r,c);
                }
            }
            System.out.println("backtracked");
        }
        return false;
    }
}
