package GRAPH;

import java.util.LinkedList;

public class Coordinates {
    public double x;
    public double y;
    public double z;
    public int index;
    public LinkedList<Coordinates> list;
    public boolean visited;

    public Coordinates(double x,double y,double z){
        this.x = x;
        this.y = y;
        this.z = z;
        visited = false;
        list = new LinkedList<Coordinates>();
    }

    public void addEdge(Coordinates c){
        list.add(c);
    }
}
