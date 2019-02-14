package GRAPH;

import java.util.LinkedList;

public class Graph {
    public LinkedList<Coordinates> nodes;

    public void clear(){
        nodes = new LinkedList<>();
    }

    public Graph(){
        nodes = new LinkedList<>();
    }

    public Graph(Graph g){
        this.nodes = g.nodes;
    }

    public void addVertex(double x,double y,double z){
        nodes.add(new Coordinates(x,y,z));
    }

    public void addVertex(Coordinates c){
        nodes.add(c);
    }

    public void addEdge(Coordinates c1,Coordinates c2){
        for(int i=0;i<nodes.size();i++){
            if(c1.equals(nodes.get(i))){
                for(int j=0;j<nodes.size();j++){
                    if(c2.equals(nodes.get(j))){
                        c1.list.add(c2);
                    }
                }
            }
        }
    }

    public Coordinates get(Coordinates c){
        for(int i=0;i<nodes.size();i++){
            if(c.equals(nodes.get(i))){
                return nodes.get(i);
            }
        }
        return null;
    }

    public Object clone() throws CloneNotSupportedException {
        return new Graph( this );
    }

}
