/******************************************************************************
  *  Name:    Greg Umali
  * 
  *  Description:  Brute-force implementation of a symbol table that associates
  *                Point2D objects with a user-specified value. Also includes
  *                functionalities to return points within a given range and 
  *                the nearest point to a specified arbitrary point p.
  * 
  ******************************************************************************/

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Queue;
import java.lang.NullPointerException;

public class KdTreeST<Value> {
    private Node root;
    private int size;
    
    
    private static final boolean VERTICAL = false;
    private static final boolean HORIZONTAL = true;
    
    private static final double XMIN = Double.MIN_VALUE;
    private static final double YMIN = Double.MIN_VALUE;
    private static final double XMAX = Double.MAX_VALUE;
    private static final double YMAX = Double.MAX_VALUE;
    
    private class Node {
        private Point2D point;      // the point
        private Value value;    // the symbol table maps the point to this value
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        // private boolean orientation; // horizontal = true, vertical = false
        
        public Node(Point2D p, Value val, RectHV rect) {
            this.point = p;
            this.value = val;
            this.rect = rect;
            //this.orientation = orientation;
        }
    }
    
    // construct an empty symbol table of points  
    public KdTreeST() {
        size = 0;
        root = null;
    }
    
    // is the symbol table empty? 
    public boolean isEmpty() {
        return size == 0;
    }
    
    // number of points 
    public int size() {
        return size;
    }
    
    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) {
            throw new NullPointerException();
        }
        
        
        root = put(root, p, val, new RectHV(XMIN, YMIN, XMAX, YMAX), VERTICAL);
        
    }
    
    // taken and modified from BST.java
    private Node put(Node x, Point2D key, Value val, RectHV rect, 
                     boolean orientation) {
        
         
        
        if (x == null) {
            size++;
            return new Node(key, val, rect);
        }
        
        if (x.point.equals(key)) {
            x.value = val;
            return x;
        }
        
        double cmp;
        RectHV newRect;
        
        if (orientation == VERTICAL) {
            cmp = key.x() - x.point.x();
            
     
            
            if (cmp < 0) {
                newRect = new RectHV(rect.xmin(), rect.ymin(), x.point.x(),
                                   rect.ymax());
                x.lb  = put(x.lb, key, val, newRect, !orientation);
            }
            else { // (cmp >= 0)
                newRect = new RectHV(x.point.x(), rect.ymin(), rect.xmax(),
                                   rect.ymax());
                x.rt = put(x.rt, key, val, newRect, !orientation);
            }
            
            return x;
        }
        else {
            cmp = key.y() - x.point.y();
            if      (cmp < 0) {
                newRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(),
                                   x.point.y());
                x.lb  = put(x.lb, key, val, newRect, !orientation);
            }
            else { // (cmp >= 0) 
                newRect = new RectHV(rect.xmin(), x.point.y(), rect.xmax(),
                                   rect.ymax());
                x.rt = put(x.rt, key, val, newRect, !orientation);
            }
            
            return x;
        }
    }
    
    // value associated with point p 
    public Value get(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        return get(root, p, VERTICAL);
    }
    
    private Value get(Node x, Point2D key, boolean orientation) {
        
        if (x == null) return null;

        if (x.point.equals(key)) {
            return x.value;
        }
        
        double cmp;
        
        if (orientation == VERTICAL) {
            cmp = key.x() - x.point.x();
        } else {
            cmp = key.y() - x.point.y();
        }
        
        if (cmp < 0) {
            return get(x.lb, key, !orientation);
        } else { // (cmp >= 0)
            return get(x.rt, key, !orientation);
        } 
    }
    
    // does the symbol table contain point p? 
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        
        return (get(p) != null);
    }
    
    // all points in the symbol table 
    public Iterable<Point2D> points() {
        Queue<Point2D> pointQueue = new Queue<Point2D>();
        keys(root, pointQueue);
        return pointQueue;
    }
    
    private void keys(Node x, Queue<Point2D> queue) {
        if (x == null) return;
        queue.enqueue(x.point);
        
        keys(x.lb, queue);
        keys(x.rt, queue);
        
        /* public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
        if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");

        Queue<Key> queue = new Queue<Key>();
        keys(root, queue, lo, hi);
        return queue;
    } 

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) { 
        if (x == null) return; 
        int cmplo = lo.compareTo(x.key); 
        int cmphi = hi.compareTo(x.key); 
        if (cmplo < 0) keys(x.left, queue, lo, hi); 
        if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key); 
        if (cmphi > 0) keys(x.right, queue, lo, hi); 
    } 
         * 
         */
        
    }
    // all points that are inside the rectangle 
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }
        
        Queue<Point2D> pointQueue = new Queue<Point2D>();
        range(pointQueue, root, rect);
            return pointQueue; 
    }
    
    private void range(Queue<Point2D> queue, Node x, RectHV rect) {
        if (x == null || !x.rect.intersects(rect)) return; // !!
        
        if (rect.contains(x.point)) {
            queue.enqueue(x.point);
        }
        
        range(queue, x.lb, rect);
        range(queue, x.rt, rect);
    }
    
    // a nearest neighbor to point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new NullPointerException();
        return nearest(root, p, root.point.distanceSquaredTo(p), VERTICAL);
    }
    
    private Point2D nearest(Node x, Point2D currentP, double nearestKnown, 
                            boolean orientation) {
        
        if (x == null) return new Point2D(XMAX, YMAX); 
        
        if (x.rect.distanceTo(currentP) > nearestKnown) {
            return x.point;
        }
            
        double nearestDist = nearestKnown;
        Point2D nearestPoint = x.point;
        double cmp;
        Point2D nearestLB = null;
        Point2D nearestRT = null;
        
        if (orientation == VERTICAL) {
            cmp = currentP.x() - x.point.x();
        } else {
            cmp = currentP.y() - x.point.y();
        }
        
        if (cmp >= 0) {
            nearestRT = nearest(x.rt, currentP, nearestDist, !orientation);
            if (nearestDist > nearestRT.distanceSquaredTo(currentP)) {
                nearestPoint = nearestRT;
                nearestDist = nearestPoint.distanceSquaredTo(currentP);
            }
            
            nearestLB = nearest(x.lb, currentP, nearestDist, !orientation);
            
            if (nearestDist > nearestLB.distanceSquaredTo(currentP)) {
                nearestDist = nearestLB.distanceSquaredTo(currentP);
                nearestPoint = nearestLB;
            }
            
        } else {
            nearestLB = nearest(x.lb, currentP, nearestDist, !orientation);
            if (nearestDist > nearestLB.distanceSquaredTo(currentP)) {
                nearestDist = nearestLB.distanceSquaredTo(currentP);
                nearestPoint = nearestLB;
            } 
            
            nearestRT = nearest(x.rt, currentP, nearestDist, !orientation);
            if (nearestDist > nearestRT.distanceSquaredTo(currentP)) {
                nearestDist = nearestRT.distanceSquaredTo(currentP);
                nearestPoint = nearestRT;
            }
        }   
        
        return nearestPoint;
    }
    
    
    
    // unit testing (required) 
    public static void main(String[] args) {
        PointST<String> test = new PointST<String>();
        
        System.out.println("Creating new symbol table...");
        System.out.println("Size of symbol table: " + test.size());
        System.out.println("This symbol table is empty: " + test.isEmpty());
        
        System.out.println("\nInserting three elements...");
        test.put(new Point2D(0.0, 0.0), "A");
        test.put(new Point2D(1.0, 1.0), "B");
        test.put(new Point2D(-1.0, -1.0), "C");
        
        System.out.println();
        
        for (Point2D p : test.points()) {
            System.out.println("Point " + test.get(p) + ": " + p);
        }
        
        System.out.println("Size of symbol table: " + test.size());
        System.out.println("This symbol table is empty: " + test.isEmpty());
        System.out.println("This symbol contains (0,0): " + 
                           test.contains(new Point2D(0.0, 0.0)));
        System.out.println("This symbol contains (3,3): " + 
                           test.contains(new Point2D(3.0, 3.0)));
        
        
        RectHV allPoints = new RectHV(Double.NEGATIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY,
                                      Double.POSITIVE_INFINITY,
                                      Double.POSITIVE_INFINITY);
        
        RectHV twoPoints = new RectHV(0.0, 0.0, 1.1, 1.1);
        
        System.out.println("\nShould contain all points");
        for (Point2D p : test.range(allPoints)) {
            System.out.println("Point " + test.get(p) + ": " + p);
        }
        
        System.out.println("Should contain two points");
        for (Point2D p : test.range(twoPoints)) {
            System.out.println("Point " + test.get(p) + ": " + p);
        }
        
        
        System.out.println("\nNearest neighbor of point (0.1, 0.1)");
        System.out.println(test.nearest(new Point2D(0.1, 0.1)));
         System.out.println("\nNearest neighbor of point (-100, -100)");
        System.out.println(test.nearest(new Point2D(-100.0, -100.0)));
        
    }
    
}
