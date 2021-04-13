package com.company;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.Math;
// Dinislam Gabitov B19-05
// https://codeforces.com/group/3ZU2JJw8vQ/contest/272963/submission/74192581
// Sweep line algorithm is very important, for example if we need to
// construct metro, railroads, roads etc. we need minimum of intersections.
public class Main {

    public static boolean vert_intersection(double[] line_1, double[] line_2){
        // Additional method for vertical line case
        // Line_1 always vertical
        double k2 = (line_2[3] - line_2[1])/(line_2[2] - line_2[0]);
        double c2 = line_2[1] - k2*line_2[0];
        // Find coefficients for equation (y = k*x + c;
        double xp = line_1[0];
        double yp = k2*xp + c2;
        // Find intersection between lines
        if(xp >= min(line_2[0],line_2[2]) && xp <= max(line_2[0],line_2[2]) && yp >= min(line_1[1],line_1[3]) && yp <= max(line_1[1],line_1[3])){
            // Check if intersection point lies on a both segments (including endpoints)
            return true;
        }
        return false;
    }

    public static boolean seg_intersection(double[] line_1, double[] line_2){
        // Method for checking if lines intersects
        if(line_1[0] == line_1[2]){ // Checking vertical line case
            return vert_intersection(line_1,line_2);
        } else if(line_2[0] == line_2[2]){
            return vert_intersection(line_2,line_1);
        }

        double k1 = (line_1[3] - line_1[1])/(line_1[2] - line_1[0]);
        double c1 = line_1[1] - k1*line_1[0];
        double k2 = (line_2[3] - line_2[1])/(line_2[2] - line_2[0]);
        double c2 = line_2[1] - k2*line_2[0];
        // Find coefficients for both lines
        double xp = (c2-c1)/(k1-k2);
        // Find x coordinate of point of intersection
        // If lines are parallel or coincident, then k1 = k2, xp = Infinite and comprising
        // statement will never hold
        // Check if intersection point lies on a both segments (including endpoints)
        return xp >= min(line_1[0], line_1[2]) && xp >= min(line_2[0], line_2[2]) && xp <= max(line_2[0], line_2[2]) && xp <= max(line_1[0], line_1[2]);
    }

    private static double min(double v, double v1) {
        if(v < v1){
            return v;
        } else {
            return v1;
        }
    }
    private static double max( double v, double v1){
        if(v > v1){
            return v;
        } else {
            return v1;
        }
    }

    public static <T extends Comparable<? super T>> void merge(T[] arr, int l, int m, int r){
        // Merge sort implementation
        int len = r-l+1;
        T[] temp = (T[]) Array.newInstance(arr.getClass().getComponentType(), len);
        T[] temp_left = (T[]) Array.newInstance(arr.getClass().getComponentType(), m-l+1);
        T[] temp_right = (T[]) Array.newInstance(arr.getClass().getComponentType(), r-m);
        // Additional arrays
        for(int i = 0; i < m-l+1;i++){
            temp_left[i] = arr[i+l];
        }
        for(int i = 0; i < r-m;i++){
            temp_right[i] = arr[i+m+1];
        }
        // Splitting into 2 parts
        int p1 = 0;
        int p2 = 0;
        int p3 = 0;
        // p1,p2,p3 - Pointrs

        for(int i = 0; i < len; i ++){ // Merge
            if(p1 == m - l + 1){
                temp[p3] = temp_right[p2];
                p3++;
                p2++;
            } else if(p2 == r - m){
                temp[p3] = temp_left[p1];
                p1++;
                p3++;
            } else if(temp_left[p1].compareTo(temp_right[p2]) <= 0){ // p1<p2
                temp[p3] = temp_left[p1];
                p1++;
                p3++;
            } else {
                temp[p3] = temp_right[p2];
                p2++;
                p3++;
            }
        }
        // Writing to main array
        for(int i = 0; i < r - l + 1; i ++){
            arr[l+i] = temp[i];
        }
    }
    public static <T extends Comparable<? super T>> void mergeSort(T[] arr, int l, int r){
        // O(n*logn), out-of-place, stable
        if(l < r) { // Merge sort implementation
            int m = (l + r) / 2;
            mergeSort(arr,l,m);
            mergeSort(arr,m+1,r);
            merge(arr,l,m,r);

        }
    }



    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        // Input number of segments
        Segment[] seg = new Segment[n];
        Endpoint[] endp = new Endpoint[2*n];
        // Making an array of endpoints
        // Every endpoint have an index for segment array to which segment it depends
        // and position (0 - start point, 1 - endpoint)
        for(int i = 0; i < n; i++) {
            seg[i] = new Segment(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt());
            if (seg[i].xs == seg[i].xe){
                // If segment is vertical, then sort by y-coordinate
                endp[2*i] = new Endpoint(seg[i].xs,Math.max(seg[i].ys,seg[i].ye),i,1);
                endp[2*i+1] = new Endpoint(seg[i].xe,Math.min(seg[i].ys,seg[i].ye),i,0);
            }else if(seg[i].xs < seg[i].xe){
                endp[2*i] = new Endpoint(seg[i].xs,seg[i].ys,i,0);
                endp[2*i+1] = new Endpoint(seg[i].xe,seg[i].ye,i,1);
            } else if(seg[i].xs > seg[i].xe){
                endp[2*i] = new Endpoint(seg[i].xs,seg[i].ys,i,1);
                endp[2*i+1] = new Endpoint(seg[i].xe,seg[i].ye,i,0);
            }
        }

        mergeSort(endp,0,endp.length - 1);
        // Sorting array of endpoints

        AVLTree<Segment> T = new AVLTree<Segment>();
        boolean no_intersections = true;
        for(int i = 0; i < endp.length - 1;i++){
            if(endp[i].pos == 0){ // if start of segment
                Segment temp = seg[endp[i].num];

                T.add(temp);

                Segment temp1 = T.above(temp);

                Segment temp2 = T.below(temp);

                double[] d1 = new double[]{(double) temp.xs,(double) temp.ys,(double) temp.xe,(double) temp.ye};
                // d1 - current segment
                if(temp1 != null){ // if above exists
                    // d2 - above segment
                    double[] d2 = new double[]{(double) temp1.xs,(double) temp1.ys,(double) temp1.xe,(double) temp1.ye};
                    if(seg_intersection(d1,d2)){
                        // If they intersect
                        no_intersections = false;
                        System.out.println("INTERSECTION");
                        temp.print();
                        temp1.print();
                        break;
                    }
                } else if(temp2 != null){ // if below exists
                    // d2 - below line
                    double[] d2 = new double[]{(double) temp2.xs,(double) temp2.ys,(double) temp2.xe,(double) temp2.ye};
                    if(seg_intersection(d1,d2)){
                        // If they intersects
                        no_intersections = false;
                        System.out.println("INTERSECTION");
                        temp.print();
                        temp2.print();
                        break;
                    }
                }
            } else if(endp[i].pos == 1){ // if end of segment
                Segment temp = seg[endp[i].num];
                Segment temp1 = T.above(temp);
                Segment temp2 = T.below(temp);
                // If both above and below exists, check them for intersection
                if(temp1 != null && temp2 != null){
                    double[] d1 = new double[]{(double) temp1.xs,(double) temp1.ys,(double) temp1.xe,(double) temp1.ye};
                    double[] d2 = new double[]{(double) temp2.xs,(double) temp2.ys,(double) temp2.xe,(double) temp2.ye};
                    // d1 - above segment
                    // d2 - below segment
                    if(seg_intersection(d1,d2)){
                        // If d1 and d2 intersects
                        no_intersections = false;
                        System.out.println("INTERSECTION");
                        temp.print();
                        temp2.print();
                    }
                }
                // Remove segment from tree at endpoint
                T.remove(seg[endp[i].num]);
            }
        }
        // If no intersections
        if(no_intersections){
            System.out.println("NO INTERSECTIONS");
        }
    }
}

class Endpoint implements Comparable<Endpoint>{
    int x,y; // coordinates
    int num; // to which line it belongs
    int pos; // 0 - start point, 1 - end point

    Endpoint(int x, int y, int num, int pos){
        // Constructor
        this.x = x;
        this.y = y;
        this.num = num;
        this.pos = pos;
    }

    public int compareTo(Endpoint e){
        //if(this.x == e.x && this.y == e.y && this.num == e.num){
        //    return this.pos - e.pos;
        //}
        if(this.x == e.x && this.y == e.y){
            // If two points have a same coordinates, sort by position (first startpoint, then endpoint)
            return this.pos - e.pos;
        }
        if(this.x == e.x){
            return this.y - e.y;
        } else {
            return this.x - e.x;
        }
    }

    public void print(){
        // Print point
        System.out.println(x + " " + y + " " + num + " " + pos);
    }
}

class Segment implements Comparable<Segment>{
    // Segment class implementation
    int xs,ys,xe,ye; //point of a segment
    Segment(int xs, int ys, int xe, int ye){
        // Constructor
        this.xs = xs;
        this.ys = ys;
        this.ye = ye;
        this.xe = xe;
    }

    public int compareTo(Segment seg){
        // Check if segments are same
        if((this.xs == seg.xs && this.ys == seg.ys)&&(this.xe == seg.xe && this.ye == seg.ye)){
            return 0;
        }

        // Compare segments by left y-coordinate
        int ys1,ys2;
        if(Math.min(this.xs,this.xe) == this.xs){
            if(Math.min(seg.xs,seg.xe) == seg.xs){
                ys1 = this.ys;
                ys2 = seg.ys;
            } else {
                ys1 = this.ys;
                ys2 = seg.ye;
            }
        } else {
            if(Math.min(seg.xs,seg.xe) == seg.xs){
                ys1 = this.ye;
                ys2 = seg.ys;
            } else {
                ys1 = this.ye;
                ys2 = seg.ye;
            }
        }
        return ys1 - ys2;
    }

    public void print(){
        System.out.println(xs + " " + ys + " " + xe + " " + ye);
    }
}

class AVLTree<T extends Comparable<? super T>>{
    // AVL tree implementation
    class Node{
        // Node class
        T element;
        int height;

        Node parent;
        Node left_child;
        Node right_child;
        //Node constructor
        Node(T element){
            this.element = element;
            this.left_child = null;
            this.right_child = null;
            this.height = 0;
            this.parent = null;
        }
    }

    private Node root;
    //AVL tree constructor
    AVLTree(){
        root = new Node(null);
    } // O(1)

    //Print tree method
    public void printTree(){ // O(n)
        Node temp = root;
        System.out.println("ROOT: " + root.element);
        if(temp!= null) {

            printTree(temp.left_child);
            System.out.print(temp.element + " ");
            printTree(temp.right_child);

        }
    }
    public void printTree(Node n){ // O(n)
        if(n != null){
            printTree(n.left_child);
            System.out.print(n.element + " ");
            printTree(n.right_child);

        }
    }


    private Node balance(Node node){ // O(logn) - in worst case we go from leaf to root
        // Balancing tree
        Node temp = node;
        temp.height = max(getH(temp.left_child),getH(temp.right_child))+1;
        // Changing height value
        while (temp.parent != null){
            // Go through tree to root node and change their height
            temp = temp.parent;
            temp.height = max(getH(temp.left_child),getH(temp.right_child))+1;

            if(abs(getH(temp.right_child) - getH(temp.left_child)) == 2){
                // If height difference of childs == 2 then rotate tree
                temp = rotate(temp);
            }
        }
        // Return temp (root)
        return temp;
    }

    public void remove(T element){// O(logn) - because we need to search element
        // Remove from tree by element
        Node temp1 = search(element);
        Node temp2 = successor(temp1);
        Node temp3 = predecessor(temp1);
        // Find successor and predecessor
        if(temp2 == null && temp3 == null){
            // If both successor and predecessor doesn't exists -> need to remove leaf node

            if(temp1.parent != null){
                // If parent exists - delete child
                if(temp1.parent.element.compareTo(temp1.element) > 0){//if parent > element
                    temp1.parent.left_child = null;
                    root = balance(temp1);
                } else {
                    temp1.parent.right_child = null;
                    root = balance(temp1);
                }
            } else {
                // If parent doesn't exists - root node;
                temp1.element = null;
            }
        } else if(temp2 == null){
            // Predecessor exists
            temp1.element = temp3.element;
            // Swapping node element with predecessor and delete predecessor
            if(temp3.element == temp1.left_child.element){
                // if predecessor is left child
                if(temp3.left_child != null){
                    temp1.left_child = temp3.left_child;
                    temp3.left_child.parent = temp1;
                    root = balance(temp3.left_child);
                } else {
                    temp1.left_child = null;
                    root = balance(temp1);
                }
            } else{
                // Find predecessor and swap
                if(temp3.left_child != null){
                    temp3.left_child.parent = temp3.parent;
                    temp3.parent.right_child = temp3.left_child;
                    root = balance(temp3.left_child);
                } else {
                    temp3.parent.right_child = null;
                    root = balance(temp3);
                }
            }
        } else if(temp3 == null){
            // If successor exists
            temp1.element = temp2.element;

            if(temp2.element == temp1.right_child.element){
                // If successor just a right child
                if(temp2.right_child != null){
                    temp1.right_child = temp2.right_child;
                    temp2.right_child.parent = temp1;
                    root = balance(temp2.right_child);
                } else {
                    temp1.right_child = null;
                    root = balance(temp1);
                }
            } else {
                // Find successor and swap
                if(temp2.right_child != null){
                    temp2.right_child.parent = temp2.parent;
                    temp2.parent.left_child = temp2.right_child;
                    root = balance((temp2.right_child));
                } else {
                    temp2.parent.left_child = null;
                    root = balance(temp2);
                }
            }
        }
    }

    public Node search(T element){ // O(logn) - in worst case we need to go from root to leaf, which exactly logn
        // Search an element
        Node temp = root;
        if(temp.element == null){
            return null;
        }
        while (true){
            if(element.compareTo(temp.element) == 0){
                // If found element
                return temp;
                // Go through tree to find element
            } else if(element.compareTo(temp.element) < 0) {
                if(temp.left_child != null) {
                    temp = temp.left_child;
                } else {
                    return null;
                }
            } else if(element.compareTo(temp.element) > 0){
                if(temp.right_child != null){
                    temp = temp.right_child;
                } else {
                    return null;
                }
            }
        }
    }

    public void add(T element){// O(logn) - we need to go from root to leaf
        // Add element
        Node temp = root;
        if(temp.element == null){
            // If tree is empty
            temp.element = element;
        } else {
            while (true){
                if(temp.element.compareTo(element) < 0){ //Element in tree < element
                    if(temp.right_child == null){
                        // If found a leaf where element should be
                        Node t = new Node(element);
                        t.parent = temp;
                        temp.right_child = t;
                        // Add element to tree
                        temp.right_child.height = 1;
                        root = balance(temp);
                        break;
                    } else {
                        // Go through tree
                        temp = temp.right_child;
                    }
                } else if(temp.element.compareTo(element) > 0){ // Element in tree > element
                    if(temp.left_child == null){
                        // If found a leaf where element should be
                        Node t = new Node(element);
                        t.parent = temp;
                        temp.left_child = t;
                        // Add element to tree
                        temp.left_child.height = 1;
                        root = balance(temp);
                        break;
                    } else {
                        // Go through tree
                        temp = temp.left_child;
                    }
                } else if(temp.element.compareTo(element) == 0){
                    // If element already in tree (cannot be more than 1 same element in tree)
                    break;
                }
            }
        }

    }

    private int max(int a, int b) {
        if(a > b){
            return a;
        } else{
            return b;
        }
    }

    private int abs(int a){
        if(a > 0){
            return a;
        } else {
            return -a;
        }
    }

    private Node leftRotate(Node node){ // O(1)
        // Left rotation
        Node ch = node.right_child;
        ch.parent = node.parent;
        if(ch.parent != null){
            if(ch.element.compareTo(ch.parent.element) < 0){
                ch.parent.left_child = ch;
            } else if(ch.element.compareTo(ch.parent.element) > 0)
                ch.parent.right_child = ch;
        }
        Node b  = ch.left_child;
        if(b!=null) {
            b.parent = node;
        }
        ch.left_child = node;
        node.right_child = b;
        node.parent = ch;
        node.height = max(getH(node.right_child),getH(node.left_child))+1;
        ch.height = max(getH(ch.left_child),getH(ch.right_child))+1;
        return ch;
    }

    private Node rightRotate(Node node){ // O(1)
        // Right rotation
        Node ch = node.left_child;
        ch.parent = node.parent;
        if(ch.parent != null){
            if(ch.element.compareTo(ch.parent.element) < 0){
                ch.parent.left_child = ch;
            } else if(ch.element.compareTo(ch.parent.element) > 0)
                ch.parent.right_child = ch;
        }
        Node b = ch.right_child;
        if(b!=null) {
            b.parent = node;
        }
        ch.right_child = node;
        node.left_child = b;
        node.parent = ch;

        node.height = max(getH(node.right_child),getH(node.left_child))+1;
        ch.height = max(getH(ch.left_child),getH(ch.right_child))+1;

        return ch;
    }

    private Node rotate(Node node){ //O(1)
        // Rotate
        if(getH(node.right_child) - getH(node.left_child) == 2){ //Right
            if(getH(node.right_child.left_child) > getH(node.right_child.right_child)){ // Right-Left case
                node.right_child = rightRotate(node.right_child);
                node = leftRotate(node);
            } else { //Right-Right case
                node = leftRotate(node);
            }
        } else if(getH(node.right_child) - getH(node.left_child) == -2){ //Left
            if(getH(node.left_child.right_child) > getH(node.left_child.left_child)){ //Left-Right case
                node.left_child = leftRotate(node.left_child);
                node = rightRotate(node);
            } else { //Left-Left case
                node = rightRotate(node);
            }
        }
        return node;
    }

    private Node successor(Node node){ // O(logn)
        // Find successor
        if (node == null){
            return null;
        }
        if(node.right_child == null){
            // Successor doesn't exists if no right subtree
            return null;
        } else {
            node = node.right_child;
            while (node.left_child != null){
                node = node.left_child;
            }
            return node;
        }
    }

    private Node predecessor(Node node){ // O(logn)

        if (node == null){
            return null;
        }
        if(node.left_child == null){
            // Predecessor doesn't exists if no left subtree
            return null;
        } else {
            node = node.left_child;
            while (node.right_child != null){
                node = node.right_child;
            }
            return node;
        }
    }

    public T above(T element){ // O(logn)
        // Find above element
        Node temp = search(element);
        Node temp1 = successor(temp);
        // Above element is successor
        // If successor == null, above is parent, if parent > element
        if(temp1 == null && temp.parent != null && temp.parent.element.compareTo(element) > 0){
            return temp.parent.element;
        } else if(temp1 == null) {
            return null;
        } else {
            return temp1.element;
        }
    }

    public T below(T element){ // O(logn)
        // Find below element
        Node temp = search(element);
        Node temp1 = predecessor(temp);
        // Below element is predecessor
        // If predecessor == null, below is parent, if parent < element
        if(temp1 == null && temp.parent != null && temp.parent.element.compareTo(element) < 0){
            return temp.parent.element;
        } else if(temp1 == null) {
            return null;
        } else {
            return temp1.element;
        }
    }

    private int getH(Node n){ // O(1)
        if( n == null){
            return 0;
        } else {
            return n.height;
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
 