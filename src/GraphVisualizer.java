import GRAPH.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class GraphVisualizer extends Frame implements GLEventListener,MouseListener{
    long INTERVAL = 500;
    boolean EDGE_SELECTION_MODE = false;
    boolean ESM_1 = false;
    LinkedList<Coordinates> esmbuffer = new LinkedList<Coordinates>();
    LinkedList<Coordinates> edgeSelectionBuffer = new LinkedList<Coordinates>();
    boolean VERTEX_SELECTION_MODE = false;
    boolean VSM_1 = false;
    LinkedList<Coordinates> result;
    LinkedList<Coordinates> VertexBuffer = new LinkedList<Coordinates>();
    LinkedList<Edge> edgeBuffer = new LinkedList<Edge>();
    LinkedList<Coordinates> points;
    Graph graph;
    Label message;
    static GL gl;
    static GLCanvas canvas;
    static GLCapabilities capabilities;
    static GLProfile profile;
    static FPSAnimator animator;
    Button b1;
    void refreshVisited(){
        for(int i=0;i<graph.nodes.size();i++){
            graph.nodes.get(i).visited = false;
        }
    }

    void refreshEdges(){
        for(int i=0;i<graph.nodes.size();i++){
            graph.nodes.get(i).list = new LinkedList<>();
        }
    }
    int k=0;
    GraphVisualizer(){
        message = new Label("messages");
        result = new LinkedList<>();
        points = new LinkedList<Coordinates>();
        graph = new Graph();
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

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        animator = new FPSAnimator(canvas,300,true);

        Button b = new Button("Completely Connect Graph");
        b.setBackground(new Color(0,255,0));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshEdges();
                for(int i=0;i<graph.nodes.size();i++){
                    for(int j=0;j<graph.nodes.size() && j!=i;j++){
                        graph.nodes.get(i).addEdge(graph.nodes.get(j));
                        graph.nodes.get(j).addEdge(graph.nodes.get(i));
                    }
                }
            }
        });
        b.setBounds(0,40,200,30);
        this.add(b);

        b1 = new Button("START/END");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                b1.setBackground(new Color(255,0,0));
                VertexBuffer.clear();
                VERTEX_SELECTION_MODE = true;
            }
        });
        b1.setBounds(0,120,200,30);
        this.add(b1);

        Button b2 = new Button("Depth First Search");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(VertexBuffer.size()==0){
                    return;
                }
                refreshVisited();
                b2.setBackground(new Color(255,0,0));
                edgeBuffer = new LinkedList<Edge>();
                result = new LinkedList<Coordinates>();
                dfs(VertexBuffer.get(0),VertexBuffer.get(1));
                b2.setBackground(new Color(0,255,0));
            }
        });
        b2.setBounds(0,160,200,30);
        this.add(b2);
        Button b3 = new Button("Breath First Search");
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(VertexBuffer.size()==0){
                    return;
                }
                b3.setBackground(new Color(255,0,0));
                edgeBuffer = new LinkedList<Edge>();
                result = new LinkedList<Coordinates>();
                refreshVisited();
                bfs(VertexBuffer.get(0),VertexBuffer.get(1));
                b3.setBackground(new Color(0,255,00));
            }
        });
        b3.setBounds(0,200,200,30);
        this.add(b3);

        Button b4 = new Button("Clear");
        b4.setBackground(new Color(255,255,0));
        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                k = 0;
                EDGE_SELECTION_MODE = false;
                ESM_1 = false;
                VERTEX_SELECTION_MODE = false;
                VSM_1 = false;
                edgeBuffer = new LinkedList<Edge>();
                result = new LinkedList<Coordinates>();
                VertexBuffer.clear();
                refreshVisited();
                points.clear();
                graph.clear();
                System.gc();
            }
        });
        b4.setBounds(0,240,200,30);
        this.add(b4);

        TextField t = new TextField("enter depth");
        Button b5 = new Button("Depth Limited Search");
        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(VertexBuffer.size()==0){
                    return;
                }
                refreshVisited();
                b5.setBackground(new Color(255,0,0));
                edgeBuffer = new LinkedList<Edge>();
                int x = Integer.parseInt(t.getText());
                result = new LinkedList<Coordinates>();
                dfs(VertexBuffer.get(0),VertexBuffer.get(1),x);
                b5.setBackground(new Color(0,255,0));
            }
        });
        b5.setBounds(0,280,200,30);
        this.add(b5);
        t.setBounds(0,320,200,30);
        this.add(t);

        Button b6 = new Button("Iterative Depth Limited Search");
        b6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(VertexBuffer.size()==0){
                    return;
                }
                b6.setBackground(new Color(255,0,0));
                int x=1;
                while(true){
                    edgeBuffer = new LinkedList<Edge>();
                    result = new LinkedList<Coordinates>();
                    refreshVisited();
                    if(dfs(VertexBuffer.get(0),VertexBuffer.get(1),x)){
                        break;
                    }else{
                        x++;
                    }
                    boolean check = false;
                    for(int i=0;i<points.size();i++){
                        if(points.get(i).visited==false){
                            check = true;
                            break;
                        }
                    }
                    if(!check){
                        break;
                    }
                }
                b6.setBackground(new Color(0,255,0));
            }
        });
        b6.setBounds(0,360,200,30);
        this.add(b6);

        Button b0 = new Button("Edge");
        b0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(EDGE_SELECTION_MODE){
                    EDGE_SELECTION_MODE = false;
                    b.setEnabled(true);
                    b1.setEnabled(true);
                    b2.setEnabled(true);
                    b3.setEnabled(true);
                    b4.setEnabled(true);
                    b5.setEnabled(true);
                    b0.setBackground(new Color(0,255,0));
                }else{
                    EDGE_SELECTION_MODE = true;
                    b.setEnabled(false);
                    b1.setEnabled(false);
                    b2.setEnabled(false);
                    b3.setEnabled(false);
                    b4.setEnabled(false);
                    b5.setEnabled(false);
                    b0.setBackground(new Color(255,0,0));
                }
            }
        });
        b0.setBounds(0,80,200,30);
        this.add(b0);

        canvas.addMouseListener(this);
        this.add(canvas);

        this.setSize(d);
        this.setVisible(true);
        animator.start();
    }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            double H = canvas.getHeight()/2;
            double W = canvas.getWidth()/2;
            double x = mouseEvent.getX();
            double delx = 10/W;
            double dely = 10/H;
            double y = mouseEvent.getY();
            if(EDGE_SELECTION_MODE){
                if(ESM_1){
                    for(int i=0;i<graph.nodes.size();i++){
                        Coordinates c= graph.nodes.get(i);
                        if(((x-W)/W)+delx>c.x && ((x-W)/W)-delx<c.x && ((H-y)/H)+dely>c.y && ((H-y)/H)-dely<c.y){
                            esmbuffer.add(c);
                            graph.addEdge(esmbuffer.get(0),esmbuffer.get(1));
                            ESM_1 = false;
                            return;
                        }
                    }
                }else {
                    for(int i=0;i<graph.nodes.size();i++){
                        Coordinates c= graph.nodes.get(i);
                        if(((x-W)/W)+delx>c.x && ((x-W)/W)-delx<c.x && ((H-y)/H)+dely>c.y && ((H-y)/H)-dely<c.y){
                            esmbuffer = new LinkedList<>();
                            esmbuffer.add(c);
                            ESM_1 = true;
                            return;
                        }
                    }
                }
                return;
            }
            if(VERTEX_SELECTION_MODE){
                if(VSM_1){
                    for(int i=0;i<graph.nodes.size();i++){
                        Coordinates c= graph.nodes.get(i);
                        if(((x-W)/W)+delx>c.x && ((x-W)/W)-delx<c.x && ((H-y)/H)+dely>c.y && ((H-y)/H)-dely<c.y){
                            VertexBuffer.add(c);
                            VSM_1 = false;
                            VERTEX_SELECTION_MODE = false;
                            this.b1.setBackground(new Color(255,255,0));
                            return;
                        }
                    }
                }else {
                    for(int i=0;i<graph.nodes.size();i++){
                        Coordinates c= graph.nodes.get(i);
                        if(((x-W)/W)+delx>c.x && ((x-W)/W)-delx<c.x && ((H-y)/H)+dely>c.y && ((H-y)/H)-dely<c.y){
                            VertexBuffer.add(c);
                            VSM_1 = true;
                            return;
                        }
                    }
                }
                return;
            }
            Coordinates c = new Coordinates((x-W)/W,(H-y)/H,0);
            c.index = ++k;
            points.add(c);
            graph.addVertex(c);
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
        gl.glPointSize(10);
        gl.glLoadIdentity();

        gl.glColor3d(1,1,1);
        gl.glBegin( GL2.GL_LINES);
        for(int i=0;i<graph.nodes.size();i++){
            for(int j=0;j<graph.nodes.get(i).list.size();j++){
                Coordinates c1 = graph.nodes.get(i);
                Coordinates c2 = graph.nodes.get(i).list.get(j);
                gl.glVertex3d(c1.x,c1.y,c1.z);
                gl.glVertex3d(c2.x,c2.y,c2.z);
            }
        }
        gl.glEnd();

        gl.glColor3d(1,0,0);
        gl.glBegin( GL2.GL_POINTS);
        for(int i=0;i<points.size();i++){
            Coordinates c = points.get(i);
            gl.glVertex3d(c.x,c.y,c.z);
        }
        gl.glEnd();

        gl.glColor3d(0,1,0);
        gl.glBegin( GL2.GL_POINTS);
        for(int i=0;i<VertexBuffer.size();i++){
            Coordinates c = VertexBuffer.get(i);
            gl.glVertex3d(c.x,c.y,c.z);
        }
        gl.glEnd();

        gl.glColor3d(0,0,1);
        gl.glBegin( GL2.GL_LINES);
        for(int i=0;i<edgeBuffer.size();i++){
            Coordinates c1 = edgeBuffer.get(i).c1;
            Coordinates c2 = edgeBuffer.get(i).c2;
            gl.glVertex3d(c1.x,c1.y,c1.z);
            gl.glVertex3d(c2.x,c2.y,c2.z);
        }
        gl.glEnd();

        gl.glColor3d(0,1,0);
        gl.glBegin( GL2.GL_LINES);
        for(int i=0;i<result.size()-1;i++){
            Coordinates c1 = result.get(i);
            Coordinates c2 = result.get(i+1);
            gl.glVertex3d(c1.x,c1.y,c1.z);
            if(c2!=null)
            gl.glVertex3d(c2.x,c2.y,c2.z);
        }
        gl.glEnd();

        if(EDGE_SELECTION_MODE && ESM_1){
            gl.glColor3d(0,1,1);
            gl.glBegin( GL2.GL_POINTS);
            Coordinates c1 = esmbuffer.get(0);
            gl.glVertex3d(c1.x, c1.y, c1.z);
            if(esmbuffer.size()>1) {
                Coordinates c2 = esmbuffer.get(1);
                gl.glVertex3d(c2.x, c2.y, c2.z);
            }gl.glEnd();
        }

        renderer.beginRendering(canvas.getWidth(),canvas.getHeight());
        renderer.setColor(0f,1f,0.7f,1f);
        int W = canvas.getWidth()/2;
        int H = canvas.getHeight()/2;
        for(int i=0;i<points.size();i++){
            Coordinates c = points.get(i);
            try {
                renderer.draw("" + c.index,abs((int)(c.x*W+W-10)),(int)(H+c.y*H+10));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        renderer.endRendering();

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

    boolean dfs(Coordinates root,Coordinates search){
        if(root.equals(search)){
            result.push(root);
            return true;
        }else {
            root.visited = true;
            for(int i=0;i<root.list.size();i++) {
                if(!root.list.get(i).visited){
                    edgeBuffer.push(new Edge(root,root.list.get(i)));
                    try{
                        canvas.display();
                        Thread.sleep(INTERVAL);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(dfs(root.list.get(i),search)){
                        result.push(root);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    boolean dfs(Coordinates root,Coordinates search,int depth){
        if(root.equals(search)){
            result.push(root);
            return true;
        }else if(depth <=0) {
            return false;
        }else {
                root.visited = true;
                for(int i=0;i<root.list.size();i++) {
                    if(!root.list.get(i).visited){
                        edgeBuffer.push(new Edge(root,root.list.get(i)));
                        try{
                            canvas.display();
                            Thread.sleep(INTERVAL);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(dfs(root.list.get(i),search,depth-1)){
                            result.push(root);
                            return true;
                        }
                    }
                }
                return false;

        }
    }

    boolean bfs(Coordinates root,Coordinates search) {
        LinkedList<Coordinates> l = new LinkedList<Coordinates>();
        l.add(root);
        Map map = new Map();
        while(l.size()>0){
            Coordinates c = l.removeFirst();
            if(c.equals(search)){
                l.clear();
                break;
            }
            for(int i=0;i<c.list.size();i++){
                if(!c.list.get(i).visited){
                    try {
                        canvas.display();
                        Thread.sleep(INTERVAL);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    edgeBuffer.push(new Edge(c,c.list.get(i)));
                    l.add(c.list.get(i));
                    map.add(c.list.get(i),c);
                    c.list.get(i).visited = true;
                }
            }
            c.visited = true;
        }
        Coordinates c = search;
        result.add(c);
        while(c!=null){
            result.add(map.parent(c));
            c = map.parent(c);
        }
        return true;
    }
    class Map{
        LinkedList<Edge> l;
        Map(){
            l = new LinkedList<>();
        }
        void add(Coordinates beta,Coordinates papa){
            l.add(new Edge(beta,papa));
        }
        Coordinates parent(Coordinates child){
            for(int i=0;i<l.size();i++){
                if(l.get(i).c1.equals(child)){
                    return l.get(i).c2;
                }
            }
            return null;
        }
    }
}
