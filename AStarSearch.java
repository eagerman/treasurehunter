import java.awt.Point;
import java.util.*;

/**
 * 
 * @author Khaled El Majzoub
 *
 * A* search algorithm with the Manhattan heuristic.
 */
public class AStarSearch {
   
   private Map<Point, Character> world;
   
   private Point start;
   private Point goal;
   private Map<Point, Integer> fCost;
   private Map<Point, Integer> gCost;
   private Map<Point, Point> srcPath;

   private boolean cutTree;
   
   public AStarSearch(Map<Point,Character> world, Point start, Point goal) {
      this.world = world;
      this.start = start;
      this.goal = goal;
      this.fCost = new HashMap<Point, Integer>();
      this.gCost = new HashMap<Point, Integer>();
      this.srcPath = new HashMap<Point, Point>();
      this.cutTree = false;
   }
   private class FCostComparator implements Comparator<Point>{
      @Override
      public int compare(Point a, Point b) {
         return fCost.get(a) - fCost.get(b);
      }
   }

   // this method has to be called after contructing the class
   public void aStar(boolean gotAxe, boolean gotKey, boolean gotRaft){
      PriorityQueue<Point> pq = new PriorityQueue<Point>(11, new FCostComparator());
      
      Set<Point> visited = new HashSet<Point>();
      
      // initialize the map to high cost
      for(int x = -World.X; x <= World.X; x++ ) {
         for(int y = -World.Y; y <= World.Y; y++) {
            fCost.put(new Point(x,y), 99999999);
            gCost.put(new Point(x,y), 99999999);
         }
      }
      
      gCost.put(start, 0);
      fCost.put(start, manhattanDistance(start,goal));
      
      pq.add(start);
      
      while(pq.size() != 0) {
         Point currentPoint = pq.poll();
         if(currentPoint.equals(goal)) {
            return;
         }
         visited.add(currentPoint);
         for (int i = 0; i < 4; i++) {
            int x = (int)currentPoint.getX();
            int y = (int)currentPoint.getY();
            switch(i) {
            case World.UP:
               y++;
               break;
            case World.RIGHT:
               x++;
               break;
            case World.DOWN:
               y--;
               break;
            case World.LEFT:
               x--;
               break;
            }
            Point nextTile = new Point(x,y);
            if(visited.contains(nextTile))
               continue;

            if(world.get(start) == World.WATER &&
            		world.get(goal) == World.WATER && 
            		world.get(nextTile) != World.WATER)
            {
               continue;
            }
            
            //Try to find a path without cutting down a tree first.
            if(cutTree) {
               if (!World.isClearWithTools(world.get(nextTile),
            		   gotAxe, gotKey, gotRaft )) {
                  continue;             
               }
            }
            else {
               if (!World.isClearWithTools(world.get(nextTile),
            		   false, gotKey, gotRaft )) {
                  continue;             
               }
            }
            int tentative_gCost = gCost.get(currentPoint) + 1;
            if (tentative_gCost >= gCost.get(nextTile)) {
               continue;
            }
            srcPath.put(nextTile, currentPoint);
            gCost.put(nextTile, tentative_gCost);
            fCost.put(nextTile, tentative_gCost + manhattanDistance(nextTile, goal));
            
            if(!pq.contains(nextTile)) {
               pq.add(nextTile);
            }
         }
      }
      if(!isGoalReachable()  && !cutTree) {
         cutTree = true;
         aStar(gotAxe, gotKey, gotRaft);
      }
   }
   
   public boolean isGoalReachable() {
	      return (srcPath.get(goal) != null);
   }
   

   public LinkedList<Point> buildPath(){
      LinkedList<Point> path = new LinkedList<Point>();
      Point curr = goal;
      while(srcPath.get(curr) != null) {
         path.addFirst(curr);
         curr = srcPath.get(curr);
      }
      return path;
   }

   private int manhattanDistance(Point start, Point goal) {
      return Math.abs((int)start.getX() - (int)goal.getX()) 
    		  + Math.abs((int)start.getY() - (int)goal.getY());
   }
}
