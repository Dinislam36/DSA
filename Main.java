//package com.company;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.HashSet;



public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); //Input

        int n = sc.nextInt(); // Input # of vertices
        int m = sc.nextInt(); // Input # of edges
        Graph<Integer,Integer> graph = new Graph<>(); // Graph initialize

        for(int i = 1; i < n+1; i++){
            graph.addVertex(i); // Add vertices
        }
        for(int i = 0; i < m; i++){
            int t1 = sc.nextInt();
            int t2 = sc.nextInt(); // Input edge
            int t3 = sc.nextInt(); // Input weight (for 3rd)
            graph.addEdge(t1,t2,t3); // Add edges
        }
        //graph.analyzeConnectivity(); // 1st task
        //graph.vertexComponents(); // 2nd task
        graph.minimumSpanningForest(); // 3rd task
    }
}



class Graph<T extends Comparable<T>, E extends Comparable<E>>{
    class Edge implements Comparable<Edge>{ // Every edge contain weight and 2 vertices
        T vertex1;
        T vertex2;
        E weight;

        Edge(T v1, T v2, E w){ // O(1)
            vertex1 = v1;
            vertex2 = v2;
            weight = w;
        }

        @Override
        public int compareTo(Edge o) { // Comparing for sort in 3rd task O(1)
            int i = this.weight.compareTo(o.weight);
            return i > 0 ? 1: i == 0 ? 0 : -1;
        }
    }

    HashMap<T,HashMap<T,Edge>> adjList; // Adjacency list


    Graph(){ // O(1)
        adjList = new HashMap<T,HashMap<T, Edge>>();
    }

    public void addVertex(T elem){ // O(1)
        adjList.put(elem,new HashMap<>());
    }

    public void addEdge(T vertex1, T vertex2, E weight){ // O(1)

        Edge e = new Edge(vertex1,vertex2,weight);
        adjList.get(vertex1).put(vertex2,e); // We put both vertex1 and 2 because graph is undirected
        adjList.get(vertex2).put(vertex1,e);
    }


        ArrayDeque<T> bfsQueue; // DS for bfs and 3rd task
        HashMap<T,Boolean> visited; // DS for bfs and 3rd task

    private void bfs(T init){ // Graph traverse O(|E| + |V|) = O(|V|^2) in worst case
        visited = new HashMap<>();
        bfsQueue = new ArrayDeque<>(); // initializing additional ds

        for(T i: adjList.keySet()){
            visited.put(i,false); // mark all vertices unvisited O(|V|)
        }

        visited.replace(init,true); // Take some of vertices as start
        T temp;
        bfsQueue.push(init); // Queue start

        while (!bfsQueue.isEmpty()){ // bfs body
            temp = bfsQueue.pop();
            for(T e:adjList.get(temp).keySet()){
                if(!visited.get(e)){
                    visited.replace(e,true); // Mark visited node
                    bfsQueue.push(e);

                }
            }
        }
    }


    public void analyzeConnectivity(){ // O(|V|+|E|) same as bfs
        T init;

        for(T i:adjList.keySet()){ // Feature to take random vertex
            init = i;
            bfs(init); // do bfs
            boolean connected = true;
            T u;
            for (T elem : visited.keySet()) { // Check that we visited all nodes
                if (!visited.get(elem)) { // If we  not visited one of the nodes
                    connected = false;
                    u = elem;
                    System.out.println("VERTICES " + init + " AND " + u + " ARE NOT CONNECTED BY A PATH");
                    break;
                }
            }
            if (connected) { // If visited all nodes
                System.out.println("GRAPH IS CONNECTED");
            }
            break;
        }
    }



    public void vertexComponents(){ // O(|V|+|E|) same as bfs because we need to visit all nodes
        visited = new HashMap<>();
        for(T i: adjList.keySet()){
            visited.put(i,false); // Mark nodes as not visited
        }

        HashMap<T,Integer> dict = new HashMap<>(); // ds for output | vertex -> set to it belong
        int set = 0; // Marking sets

        for(T e:visited.keySet()){
            if(!visited.get(e)){ // If node is not visited, do bfs and mark all visited as belongs to same set
                set++;

                bfsQueue = new ArrayDeque<>(); // Just bfs
                visited.replace(e,true);
                dict.put(e,set);
                T temp;
                bfsQueue.push(e);
                while (!bfsQueue.isEmpty()){
                    temp = bfsQueue.pop();

                    for(T t:adjList.get(temp).keySet()){
                        if(!visited.get(t)){
                            dict.put(t,set);
                            visited.replace(t,true);
                            bfsQueue.push(t);

                        }
                    }
                }
            }
        }

        TreeMap<T,Integer> sorted = new TreeMap<T,Integer>(dict); // Sort by vertices
        int g  =0;
        for(Integer e:sorted.values()){ // output
            if(g!=0){
                System.out.print(" ");
            }
            System.out.print(e);
            g++;
        }
    }


    public void minimumSpanningForest(){ // O(|E|log|V| + |V|log|V|)
        // Same as vertex components, but instead of bfs do Prim's algorithm
        // Prims algorithm  the same as bfs, but uses priority queue
        // Multiply by log|V| because priority queue has O(log|V|) add element; queue O(1)
        visited = new HashMap<>();
        for(T i: adjList.keySet()){
            visited.put(i,false);
        }

        LinkedList<LinkedList<String>> spanningTrees = new LinkedList<LinkedList<String>>(); // Storing trees; spanningTree[i] contains list of all edges belongs to tree i in string format
        int total_trees = 0;
        for(T e:visited.keySet()){ // Go through all vertices
            if(!visited.get(e)){ // If not visited, build a tree with e as initial vertex
                total_trees++;
                LinkedList<String> currentTreeStr = new LinkedList<String>();
                HashSet<T> currentTree = new HashSet<>();
                Queue<Edge> priorityQueue = new PriorityQueue<>(); // instead of queue in bfs, use priority queue
                visited.replace(e,true);
                currentTree.add(e);
                for(T i:adjList.get(e).keySet()){
                    priorityQueue.add(adjList.get(e).get(i)); // O(log|V|)
                }
                while (!priorityQueue.isEmpty()){
                    Edge temp = priorityQueue.remove();
                    if(currentTree.contains(temp.vertex1) ^ currentTree.contains(temp.vertex2)){ // Contains exactly 1 of vertices
                        currentTreeStr.add(temp.vertex1 + " " + temp.vertex2 + " " + temp.weight); // Add edge to this tree
                        T vert1 = currentTree.contains(temp.vertex1) == true ? temp.vertex2 : temp.vertex1; // Opposite vertex in edge temp;
                        visited.replace(vert1,true);
                        currentTree.add(vert1);
                        for(T i:adjList.get(vert1).keySet()){
                            priorityQueue.add(adjList.get(vert1).get(i)); // O(log|V|)
                        }

                    }
                }
                currentTreeStr.add(0,currentTree.size() + " " + e); // add to start information about tree (num of vertices + any vertex)
                spanningTrees.add(currentTreeStr);
            }
        }
        // Output
        System.out.println(total_trees);
        for(LinkedList<String> i:spanningTrees){
            for(String s:i){
                System.out.println(s);
            }
        }
    }
}

class Scanner {
    InputStream in;
    char c;
    Scanner(InputStream in) {
        this.in = in;
        nextChar();
    }

    void asserT(boolean e) {
        if (!e) {
            throw new Error();
        }
    }

    void nextChar() {
        try {
            c = (char)in.read();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    long nextLong() {
        while (true) {
            if ('0' <= c && c <= '9' || c == '-') {
                break;
            }
            asserT(c != -1);
            nextChar();
        }
        long sign=1;
        if(c == '-'){
            sign=-1;
            nextChar();
        }
        long value = c - '0';
        nextChar();
        while ('0' <= c && c <= '9') {
            value *= 10;
            value += c - '0';
            nextChar();
        }
        value*=sign;
        return value;
    }

    int nextInt() {
        long longValue = nextLong();
        int intValue = (int)longValue;
        asserT(intValue == longValue);
        return intValue;
    }
}