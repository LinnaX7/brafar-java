package variables;

import java.util.ArrayList;
import java.util.List;

public class GraphAdjacencyMatrix {
    int vertex;
    int[][] matrix;
    int[][] rmatrix;
    public GraphAdjacencyMatrix(int vertex) {
        this.vertex = vertex;
        matrix = new int[vertex][vertex];
        rmatrix=new int[vertex][vertex];
    }

    public void addEdge(int source, int destination) {
        //add edge
        if(source!=destination){
            matrix[source][destination]=1;
            rmatrix[destination][source]=1;
        }
    }
    public List<Integer> getRely(int n){
        List<Integer> list=new ArrayList<>();
        for(int i=0;i<vertex;i++){
            if(matrix[n][i]!=0){
                list.add(i);
            }
        }
        return  list;
    }
    public List<Integer> getBeRelied(int n){
        List<Integer> list=new ArrayList<>();
        for(int i=0;i<vertex;i++){
            if(rmatrix[n][i]!=0&&i!=n){
                list.add(i);
            }
        }
        return list;
    }
    public void printGraph() {
        System.out.println("Graph: (Adjacency Matrix)");
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j <vertex ; j++) {
                System.out.print(matrix[i][j]+ " ");
            }
            System.out.println();
        }
        for (int i = 0; i < vertex; i++) {
            System.out.print("Vertex " + i + " is connected to:");
            for (int j = 0; j <vertex ; j++) {
                if(matrix[i][j]==1){
                    System.out.print(j + " ");
                }
            }
            System.out.println();
        }
    }
}