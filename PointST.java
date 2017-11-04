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

public class PointST<Value> {
    RedBlackBST<Point2D, Value> points;

   // construct an empty symbol table of points  
    public PointST() {
        points = new RedBlackBST<Point2D,Value>();
    }
            
   // is the symbol table empty? 
    public boolean isEmpty() {
        return (points.size() == 0);
    }
    
   // number of points 
    public int size() {
       return (points.size());
    }
   
   // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) {
            throw new NullPointerException();
        }
        points.put(p, val);
    }
       
   // value associated with point p 
    public Value get(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        return points.get(p);
    }
   

   // does the symbol table contain point p? 
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        
        return points.contains(p);
    }
    
  // all points in the symbol table 
    public Iterable<Point2D> points() {
        return points.keys();
    }
   // all points that are inside the rectangle 
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }
        
        Queue<Point2D> pointQueue = new Queue<Point2D>();
        for (Point2D current : this.points()) {
            if (rect.contains(current)) {
                pointQueue.enqueue(current);
            }
        }
        
        return pointQueue;
        
    }
   // a nearest neighbor to point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        Point2D minDistP = null;
        double minDistance = Double.MAX_VALUE;
        for (Point2D current : this.points()) {
            double distanceToP = current.distanceSquaredTo(p);
            
            if (distanceToP < minDistance) {
                minDistP = current;
                minDistance = distanceToP;
            }
        }
        
        return minDistP;
        
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
        
        System.out.println("\nNearest neighbor of point (0.5, 0.5)");
        System.out.println(test.nearest(new Point2D(0.5, 0.5)));
    }
   
}
