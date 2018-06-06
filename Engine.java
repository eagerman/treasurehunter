import java.awt.Point;
import java.util.*;


/**
 * @author khaled
 * 
  When the agent is asked to make a move, the engine class gets called to make a decision
  on the next move, which is done using the greedy algorithm with a priority list as the following:
 1. If we got the treasure we shal go back to base
 2. create a path to the treasure if it is in sight
 3. if we are on water explore as much as possible
 4. if we face doors and have keys unlock the doors
 5. Pick up any tools if they are in reach
 6. Explore as much of the land as possible
 7. If we have a raft we shall go to water
 
 The method makePath is used to find the shortest path from the start to the goal 
	using A* search algorithm with the Manhattan heuristic.
	We then add actions to a Queue while iterating through the path
 */

public class Engine {
	private Queue<Character> queue;
	private World world;

	public Engine() {
		this.queue = new LinkedList<Character>();
		this.world = new World();
	}

	// get the next move
	public char getMove(char view[][]) {
		world.update(view);
		char move = 'r';

		while (queue.isEmpty()) {
			// Highest priority when we find the gold
			if (world.gotTreasure()) {
				if (makePath(world.getLoc(), new Point(0, 0))) {
					break;
				}
			}
			// gold is visible, go to pick it up
			if (world.treasureVisible()) {
				if (makePath(world.getLoc(), world.getTreasureLoc())) {
					break;
				}
			}
			// search water before returning
			if (world.getCurrentTerrain() == World.WATER) {
				
				Point toExplore = world.nextWaterToExplore(world.getLoc());
				if (toExplore != null) {
					if (makePath(world.getLoc(), toExplore)) {
						break;
					}
				}
			}
			// Unlock doors
			if ((world.gotKey()) && (!world.getDoorLocs().isEmpty())) {
				if (makePath(world.getLoc(), world.getDoorLocs().peek())) {
					world.getDoorLocs().poll();
					break;
				}
			}
			// Pick up axes
			if (((!world.gotAxe()) && (!world.getAxeLocs().isEmpty()))) {
				if (makePath(world.getLoc(), world.getAxeLocs().peek())) {
					world.getAxeLocs().poll();
					break;
				}
			}
			// Pick up keys
			if (((!world.gotKey()) && (!world.getKeyLocs().isEmpty()))) {
				if (makePath(world.getLoc(), world.getKeyLocs().peek())) {
					world.getKeyLocs().poll();
					break;
				}
			}

			// Explore
			Point toExplore = world.nextToExplore(world.getLoc());
			if (toExplore != null) {
				if (makePath(world.getLoc(), toExplore)) {
					break;
				}
			}
			
			// least priority is for cutting trees to be able to go back home
			if (((!world.gotRaft()) && (!world.getTreeLocs().isEmpty()))) {
				if (makePath(world.getLoc(), world.getTreeLocs().peek())) {
					world.getTreeLocs().poll();
					queue.add(World.CUT_TREE);
					break;
				}
			}
			
			// go back via water
			if (world.gotRaft()) {
				toExplore = world.nextWaterToExplore(world.getLoc());
				if (toExplore != null) {
					if (makePath(world.getLoc(), toExplore))
						break;
				}
			}
			
		}
		move = queue.poll();
		world.updateMove(move);
		return move;
	}

	// creates a path from one point to another, adding the required moves to the queue
	private boolean makePath(Point src, Point dest) {
		AStarSearch a = new AStarSearch(world.getWorld(), src, dest);
		a.aStar(world.gotAxe(), world.gotKey(), world.gotRaft());
		boolean done = false;
		if (a.isGoalReachable()) {
			LinkedList<Point> path = a.buildPath();
			path.addFirst(src);
			int currDirection = world.getDirection();
			while (path.size() > 1) {
				int nextDirection = getDirection(path.poll(), path.peek());
				queue.addAll(getTurningsList(currDirection, nextDirection));
				currDirection = nextDirection;
				
				char head = world.getWorld().get(path.peek());
				if ( head == World.TREE) {
					queue.add(World.CUT_TREE);
				} else if ( head == World.DOOR) {
					queue.add(World.UNLOCK_DOOR);
				}
				queue.add(World.MOVE_FORWARD);
			}
			done = true;
		}
		return done;
	}

	// get list of turns to do
	private LinkedList<Character> getTurningsList(
			int currD, int nextD) {
		int leftTurns = 0;
		int rightTurns = 0;
		
		LinkedList<Character> turns = new LinkedList<Character>();
		if (currD == nextD)
			return turns;
	
		if (nextD < currD) {
			if (currD - nextD == 3) {
				rightTurns = 1;
			} else {
				leftTurns = currD - nextD;
			}
		} else {
			if (nextD - currD == 3) {
				leftTurns = 1;
			} else {
				rightTurns = nextD - currD;
			}
		}
		if (leftTurns > 0) {
			for (int i = 0; i < leftTurns; i++) {
				turns.add(World.TURN_LEFT);
			}
		} else {
			for (int i = 0; i < rightTurns; i++) {
				turns.add(World.TURN_RIGHT);
			}
		}
		return turns;
	}

	// get our heading
	private int getDirection(Point curr, Point next) {
		int x = (int) (next.getX() - curr.getX());
		int y = (int) (next.getY() - curr.getY());
		int d = 0;
		if (x != 0) {
			if (x < 0) {
				d = World.LEFT;
			} else {
				d = World.RIGHT;
			}
		} else {
			if (y < 0) {
				d = World.DOWN;
			} else {
				d = World.UP;
			}
		}
		return d;
	}
}
