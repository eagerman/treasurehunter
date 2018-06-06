import java.awt.Point;
import java.util.*;


/**
 * @author Khaled El Majzoub
 *
 *  All our data are stored in the World class as Point data structures. 
    The world class store the locations of the agent and all the tools and terrains on the map
	using linked lists
	It has booleans to indicate if you have got a specific tool
	and it also contains some methods to get the next point to explore on land or water
 */

public class World {
   final static int VIEW = 5;
   final static int X = 80;
   final static int Y = 80;
   
   final static int UP = 0;
   final static int RIGHT = 1;
   final static int DOWN = 2;
   final static int LEFT = 3;

   final static char BLANK = ' ';
   final static char DOOR = '-';
   final static char TREE = 'T';
   final static char WALL = '*';
   final static char WATER = '~';
   
   final static char KEY = 'k';
   final static char AXE = 'a';
   final static char TREASURE = '$';
   
   final static char UNDISCOVERED = 'x';
   
   final static char TURN_LEFT = 'L';
   final static char TURN_RIGHT = 'R';
   final static char MOVE_FORWARD = 'F';
   final static char CUT_TREE = 'C';
   final static char UNLOCK_DOOR = 'U';
   
   private boolean gotKey;
   private boolean gotAxe;
   private boolean gotRaft;
   private boolean gotTreasure;
   private boolean treasureVisible; 
   
   private int aX;
   private int aY;
   private int heading; 
   
   private Map<Point, Character> worldMap;
   private Set<Point> visited;
   private char currLocType;
   
   private Point treasureLoc;   
   private LinkedList<Point> keys;
   private LinkedList<Point> axes;
   private LinkedList<Point> trees;
   private LinkedList<Point> doors;
   
   public World() {
      aX = 0;
      aY = 0;
      heading = DOWN;
      
      gotKey = false;
      gotAxe = false;
      gotRaft = false;
      gotTreasure = false;
      treasureVisible = false;
      
      visited = new HashSet<Point>();
      worldMap = new HashMap<>();
      currLocType = ' ';
      
      keys = new LinkedList<Point>();
      axes = new LinkedList<Point>();
      trees = new LinkedList<Point>();
      doors = new LinkedList<Point>();
      
      for(int x = -X; x <= X; x++) {
         for(int y = -Y; y <= Y; y++) {
            //Pre-fill the world with UNDISCOVERED;
            worldMap.put(new Point(x,y), UNDISCOVERED);
         }
      }
   }

   public boolean gotKey() {
	      return gotKey;
   }
   
   public boolean gotAxe() {
      return gotAxe;
   }

   public boolean gotRaft() {
      return gotRaft;
   }
   
   public boolean gotTreasure() {
      return gotTreasure;
   }
   
   public Map<Point, Character> getWorld() {
      return worldMap;
   }
   
   public Point getLoc() {
      return new Point(aX, aY);
   }
   
   public int getDirection() {
      return heading;
   }
   
   public Point getTreasureLoc() {
      return treasureLoc;
   }
   
   public LinkedList<Point> getKeyLocs(){
	      return keys;
   }
   
   public LinkedList<Point> getAxeLocs(){
      return axes;
   }
   
   public LinkedList<Point> getTreeLocs(){
      return trees;
   }
   
   public LinkedList<Point> getDoorLocs(){
      return doors;
   }
   
   public boolean treasureVisible() {
      return treasureVisible;
   }
   
   public char getCurrentTerrain() {
      return currLocType;
   }

   public void update(char view[][]) {
      int requiredRotations = 0;
      
      switch(heading) {
         case UP:
            break;
         case RIGHT:
            requiredRotations = 1;
            break;
         case DOWN:
            requiredRotations = 2;
            break;
         case LEFT:
            requiredRotations = 3;
            break;

      }
      for(int i = 0; i < requiredRotations; i++)
         view = rotateMap(view);
      
      for(int i = 0; i < VIEW; i++) {
         for(int j = 0; j < VIEW; j++) {
            int currX = aX + (j-2);
            int currY = aY + (2-i);
            char currTile = view[i][j];

            Point tile = new Point(currX, currY);
            
            switch(currTile) {
            case KEY:
                if(!keys.contains(tile))
                   keys.add(tile);
                break;
                
               case AXE:
                  if(!axes.contains(tile))
                     axes.add(tile);
                  break;
               
               case TREASURE:
                  treasureVisible = true;
                  treasureLoc = tile;
                  break;

               case TREE:
                  if(!trees.contains(tile))
                     trees.add(tile);
                  break;

               case DOOR:
                  if(!doors.contains(tile)) 
                     doors.add(tile);
                  break;
            }
            worldMap.put(tile, currTile);
            visited.add(getLoc());
         }
      }
      worldMap.put(getLoc(), currLocType);
   }

   private static char[][] rotateMap(char[][] model){
      char[][] rotatedMap = new char[model[0].length][model.length];
      for(int i = 0; i < model.length; i++) {
         for(int j = 0; j < model[0].length; j++) {
            rotatedMap[j][model.length - 1 - i] = model[i][j];
         }
      }
      return rotatedMap;
   }

   public void updateMove(char move) {
      Point frontTile = getFrontTile(new Point(aX, aY));
      char frontTileChar = worldMap.get(frontTile);
      switch(move) {
         //Left Turn
         case 'L':
            switch (heading) {
               case UP:
                  heading = LEFT;
                  break;
                  
               case RIGHT:
                  heading = UP;
                  break;
                  
               case DOWN:
                  heading = RIGHT;
                  break;
                  
               case LEFT:
                  heading = DOWN;
                  break;
            }
            break;
            
          //Right turn
         case 'R':
            switch (heading) {
               case UP:
                  heading = RIGHT;
                  break;
                  
               case RIGHT:
                  heading = DOWN;
                  break;
                  
               case DOWN:
                  heading = LEFT;
                  break;
                  
               case LEFT:
                  heading = UP;
                  break;
            }
            break;
         // Forward
         case 'F':
           if((frontTileChar == WALL) || (frontTileChar == DOOR) || (frontTileChar == TREE)) {
              break;
           }
           if((currLocType == WATER) && (isClearWay(frontTileChar))){
              gotRaft = false;
           }
           if (frontTileChar == AXE) {
              gotAxe = true;
           }
           else if (frontTileChar == KEY) {
              gotKey = true;
           }
           else if (frontTileChar == TREASURE) {
              gotTreasure = true;
           }
           
           switch(heading) {
              case UP:
                 aY++;
                 break;
              case DOWN:
                 aY--;
                 break;
              case RIGHT:
                  aX++;
                  break;
              case LEFT:
                 aX--;
                 break;
           }
           currLocType = worldMap.get(getLoc());

         // unlock a door
        case 'U':
           doors.remove(frontTile);
           break;
           
         // cut a tree
         case 'C':
            if(frontTileChar == TREE) {
               trees.remove(frontTile);
               gotRaft = true;
            }
            break;
      }
   }
   
   // Get the tile in front of a given tile
   private Point getFrontTile(Point tile) {
      int x = (int) tile.getX();
      int y = (int) tile.getY();
      
      switch(heading) {
         case UP:
            y++;
            break;
         case DOWN:
             y--;
             break;
         case RIGHT:
            x++;
            break;
         case LEFT:
            x--;
            break;
      }
      return new Point(x,y);
   }

   public static boolean isClearWay(char tile) {
      if (
    		 (tile == BLANK) ||
             (tile == KEY) ||
             (tile == AXE) ||
             (tile == TREASURE) 
            ) return true;
      
      return false;
   }

   public static boolean
   isClearWithTools(char tile, boolean gotAxe, boolean gotKey, boolean gotRaft) {
      if (
    		 (tile == BLANK) ||
	         (tile == KEY) ||
			 (tile == AXE) ||
             (tile == TREASURE) ||
             (tile == DOOR && gotKey) ||
             (tile == TREE && gotAxe) ||
             (tile == WATER && gotRaft)
            ) return true;
      return false;
   }


   public Point nextToExplore(Point curr) {
      for(Point p : worldMap.keySet()) {
    	 char pointType = worldMap.get(p);
         if(pointType != UNDISCOVERED && !visited.contains(p)
        		 && isClearWithTools(pointType, gotAxe, gotKey, gotRaft)) {
     	 
        	 if (reachablePoint(p, curr)) return p;
         }
      }
      return null;
   }


   public Point nextWaterToExplore(Point curr) {
      for(Point p : worldMap.keySet()) {
     	 char pointType = worldMap.get(p);
         if(pointType == WATER && !visited.contains(p)) {
        	 if (reachablePoint(p, curr)) return p;
         }
      }
      return null;
   }

   private boolean reachablePoint(Point p, Point curr){
	   AStarSearch a = new AStarSearch(worldMap, curr, p);
	   a.aStar(gotAxe, gotKey, gotRaft);
	   
	   return a.isGoalReachable();
   }
}

