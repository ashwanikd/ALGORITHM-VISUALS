package EIGHTQUEEN;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.event.*;
import java.awt.*;

public class EightQueenProblem extends Frame implements GLEventListener,MouseListener{
    final int SIZE = 8;
    final int INTERVAL = 5000;
    final int STEP_INTERVAL = 10;
    int k=0;
    final int HEIGHT = 500;
    final int WIDTH = 500;
    Block[][] Board;
    boolean STOP = false;
    static GL gl;
    static GLCanvas canvas;
    static GLCapabilities capabilities;
    static GLProfile profile;
    static FPSAnimator animator;

    void initializePlayBoard(){
        Board = new Block[SIZE][SIZE];
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                Board[i][j] = new Block();
                Board[i][j].x = 2*(2*j+1)/((double)(SIZE*2))-(double)(1);
                Board[i][j].y = (double)(1)-2*(2*i+1)/((double)(SIZE*2));
            }
        }
    }

    void placeQueen(int r,int c){
        Board[r][c].OCCUPIED = true;
        for(int i=0;i<SIZE;i++){
            Board[i][c].ALLOWED = false;
            Board[r][i].ALLOWED = false;
        }
        int t1 = r,t2 = c;
        while (t1>-1 && t2>-1){
            Board[t1][t2].ALLOWED = false;
            t1--;t2--;
        }
        t1 = r;t2 = c;
        while (t1<SIZE && t2>-1){
            Board[t1][t2].ALLOWED = false;
            t1++;t2--;
        }
        t1 = r;t2 = c;
        while (t1>-1 && t2<SIZE){
            Board[t1][t2].ALLOWED = false;
            t1--;t2++;
        }
        t1 = r;t2 = c;
        while (t1<SIZE && t2<SIZE){
            Board[t1][t2].ALLOWED = false;
            t1++;t2++;
        }
    }

    public EightQueenProblem(){
        Board = new Block[SIZE][SIZE];
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                Board[i][j] = new Block();
                Board[i][j].x = 2*(2*j+1)/((double)(SIZE*2))-(double)(1);
                Board[i][j].y = (double)(1)-2*(2*i+1)/((double)(SIZE*2));
            }
        }
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

        animator = new FPSAnimator(canvas,300,true);

        canvas.addMouseListener(this);
        this.add(canvas);

        this.setSize(WIDTH,HEIGHT);
        this.setVisible(true);
        animator.start();
        dfs(0);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        STOP = false;
    }
    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    TextRenderer renderer;
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 20));
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(gl.GL_DEPTH_BUFFER_BIT|gl.GL_COLOR_BUFFER_BIT);
        gl.glPointSize(20);
        gl.glLoadIdentity();
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                if(Board[i][j].OCCUPIED){
                    gl.glColor3d(1,0,0);
                    gl.glBegin(GL2.GL_POINTS);
                    gl.glVertex3d(Board[i][j].x,Board[i][j].y,0);
                    gl.glEnd();
                }else {
                    gl.glColor3d(1, 1, 1);
                    gl.glBegin(GL2.GL_POINTS);
                    gl.glVertex3d(Board[i][j].x, Board[i][j].y, 0);
                    gl.glEnd();
                }
            }
        }
        gl.glFlush();
    }

    int abs(int x){
        if(x<0){
            return -1*x;
        }else return x;
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    void dfs(int r){
        Block[][] tempBoard = new Block[SIZE][SIZE];
        for(int j=0;j<SIZE;j++){
            for(int k=0;k<SIZE;k++){
                tempBoard[j][k] = new Block();
                tempBoard[j][k].OCCUPIED = Board[j][k].OCCUPIED;
                tempBoard[j][k].ALLOWED = Board[j][k].ALLOWED;
            }
        }
        if(r==SIZE-1){
            boolean check = false;
            for(int i=0;i<SIZE;i++){
                if(Board[r][i].ALLOWED){
                    placeQueen(r,i);
                    check = true;
                }
            }
            if(check){
                STOP = true;
                try {
                    System.out.println(++k);
                    Thread.sleep(INTERVAL);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return;
            }
            return;
        }
        for(int i=0;i<SIZE;i++){
            if(Board[r][i].ALLOWED){
                STOP = true;
                try {
                    Thread.sleep(STEP_INTERVAL);
                }catch (Exception e){
                    e.printStackTrace();
                }
                placeQueen(r,i);
                dfs(r+1);
                for(int j=0;j<SIZE;j++){
                    for(int k=0;k<SIZE;k++){
                        Board[j][k].OCCUPIED = tempBoard[j][k].OCCUPIED;
                        Board[j][k].ALLOWED = tempBoard[j][k].ALLOWED;
                    }
                }
            }
        }
        for(int j=0;j<SIZE;j++){
            for(int k=0;k<SIZE;k++){
                Board[j][k].OCCUPIED = tempBoard[j][k].OCCUPIED;
                Board[j][k].ALLOWED = tempBoard[j][k].ALLOWED;
            }
        }
        return;
    }

}
