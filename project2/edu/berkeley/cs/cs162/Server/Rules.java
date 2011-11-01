package edu.berkeley.cs.cs162.Server;

import java.util.Vector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
   Helpers for game rules.
 */
public class Rules {

    /**
       Get the set of stones which would be captured if a stone of the specified
       color was placed at the specified location
    */
    public static Vector<Location> getCapturedStones(Board board, StoneColor color, Location location) {
	Helper helper = new Helper(board);
	helper.connectedComponents();

	HashSet<Integer> threatened = new HashSet<Integer>();
	boolean selfIsSafe = false;
	boolean bordersSelf = false;
	if (location.getX() > 0) {
	    int group = helper.labels[location.getY()][location.getX()-1];
	    if (helper.colorMap.get(group) != StoneColor.NONE) {
		threatened.add(group);
	    } else {
		selfIsSafe = true;
	    }
	}
	if (location.getY() > 0) {
	    int group = helper.labels[location.getY()-1][location.getX()];
	    if (helper.colorMap.get(group) != StoneColor.NONE) {
		threatened.add(group);
	    } else {
		selfIsSafe = true;
	    }
	}
	if (location.getX() < board.getSize() - 1) {
	    int group = helper.labels[location.getY()][location.getX()+1];
	    if (helper.colorMap.get(group) != StoneColor.NONE) {
		threatened.add(group);
	    } else {
		selfIsSafe = true;
	    }
	}
	if (location.getY() < board.getSize() - 1) {
	    int group = helper.labels[location.getY()+1][location.getX()];
	    if (helper.colorMap.get(group) != StoneColor.NONE) {
		threatened.add(group);
	    } else {
		selfIsSafe = true;
	    }
	}

	Vector<Integer> capturedOpponents = new Vector<Integer>();
	Vector<Integer> capturedSelf = new Vector<Integer>();
	Iterator<Integer> iter = threatened.iterator();
	while (iter.hasNext()) {
	    // did we kill this group?
	    int i = (Integer)iter.next();
	    if (helper.colorMap.get(i) == color) {
		bordersSelf = true;
	    }
	    boolean foundLiberty = false;
	    Vector<Location> locations = helper.neighbourMap.get(i);
	    for (int j = 0; j < locations.size() && !foundLiberty; j++) {
		Location l = (Location)locations.get(j);
		if (!l.equals(location) &&
		    board.getAtLocation(l) == StoneColor.NONE) {
		    foundLiberty = true;
		}
	    }
	    if (!foundLiberty) {
		// dead.
		if (helper.colorMap.get(i) != color) {
		    capturedOpponents.add(i);
		} else {
		    capturedSelf.add(i);
		}
	    }
	}
	Vector<Location> result = new Vector<Location>();
	if (!capturedOpponents.isEmpty()) {
	    for (int i = 0; i < capturedOpponents.size(); i++) {
		helper.addGroupLocations(capturedOpponents.get(i), result);
	    }
	} else if (!capturedSelf.isEmpty() && !selfIsSafe) {
	    for (int i = 0; i < capturedSelf.size(); i++) {
		helper.addGroupLocations(capturedSelf.get(i), result);
	    }
	    // the newly placed stone also dies
	    result.add(location);
	} else if (!selfIsSafe && !bordersSelf) {
	    // we killed only ourselves
	    result.add(location);
	}
	    
	return result;
    }
    
    public static int countOwnedTerritory(Board board, StoneColor color) {
	int result = 0;
	
	Helper helper = new Helper(board);
	helper.connectedComponents();

	Iterator<Integer> iter = helper.colorMap.keySet().iterator();
	while (iter.hasNext()) {
	    int i = (Integer)iter.next();
	    if (helper.colorMap.get(i) == StoneColor.NONE &&
		helper.countMap.keySet().contains(i)) {
		// may count.
		boolean touchesPlayer = false, touchesOpponent = false;
		Vector<Location> locations = helper.neighbourMap.get(i);
		for (int j = 0; j < locations.size(); j++) {
		    if (board.getAtLocation(locations.get(j)) == color) {
			touchesPlayer = true;
		    } else if (board.getAtLocation(locations.get(j)) != color) {
			touchesOpponent = true;
		    }
		}
		if (touchesPlayer && !touchesOpponent) {
		    result += helper.countMap.get(i);
		}
	    }
	}
	return result;
    }

    private static class Helper {
	public Board board;
	public int[][] labels;
	public HashMap<Integer, StoneColor> colorMap;
	public HashMap<Integer, Integer> countMap;
	public HashMap<Integer, Vector<Location> > neighbourMap;

	public Helper(Board board) {
	    this.board = board;
	}

	public void addGroupLocations(int label, Vector<Location> result) {
	    for (int y = 0; y < board.getSize(); y++) {
		for (int x = 0; x < board.getSize(); x++) {
		    if (labels[y][x] == label) {
			result.add(new Location(x, y));
		    }
		}
	    }
	}

	public void connectedComponents() {
	    HashMap<Integer, HashSet<Integer> > equiv =
		new HashMap<Integer, HashSet<Integer> >();
	    int nextLabel = 1;
	    labels = new int[board.getSize()][board.getSize()];
	    colorMap = new HashMap<Integer, StoneColor>();
	    HashMap<Integer, HashSet<Location> > neighbours =
		new HashMap<Integer, HashSet<Location> >();
	// first pass
	for (int y = 0; y < board.getSize(); y++) {
	    for (int x = 0; x < board.getSize(); x++) {
		StoneColor c = board.getAtLocation(new Location(x, y));
		boolean isWestEqual =
		    (x > 0 && c == board.getAtLocation(new Location(x-1,y)));
		boolean isNorthEqual =
		    (y > 0 && c == board.getAtLocation(new Location(x,y-1)));

		if (isWestEqual && isNorthEqual) {
		    labels[y][x] = Math.min(labels[y-1][x], labels[y][x-1]);
		    HashSet<Integer> s = new HashSet<Integer>();
		    s.addAll(equiv.get(labels[y-1][x]));
		    s.addAll(equiv.get(labels[y][x-1]));
		    Iterator<Integer> iter = s.iterator();
		    while (iter.hasNext()) {
			equiv.get(iter.next()).addAll(s);
		    }
		} else if (isWestEqual) {
		    labels[y][x] = labels[y][x-1];
		} else if (isNorthEqual) {
		    labels[y][x] = labels[y-1][x];
		} else {
		    labels[y][x] = nextLabel++;
		    equiv.put(labels[y][x], new HashSet<Integer>());
		    equiv.get(labels[y][x]).add(labels[y][x]);
		    colorMap.put(labels[y][x], c);
		    neighbours.put(labels[y][x], new HashSet<Location>());
		}

		if (x > 0 && !isWestEqual) {
		    neighbours.get(labels[y][x]).add(new Location(x-1, y));
		    neighbours.get(labels[y][x-1]).add(new Location(x, y));
		}
		if (y > 0 && !isNorthEqual) {
		    neighbours.get(labels[y][x]).add(new Location(x, y-1));
		    neighbours.get(labels[y-1][x]).add(new Location(x, y));
		}
	    }
	}
	
	// find the mapping
	HashMap<Integer, Integer> minMap = new HashMap<Integer, Integer>();
	Iterator<Integer> iter = equiv.keySet().iterator();
	while (iter.hasNext()) {
	    Integer i = (Integer)iter.next();
	    Integer minValue = i;
	    Iterator<Integer> iter2 = equiv.get(i).iterator();
	    while (iter2.hasNext()) {
		minValue = Math.min(minValue, (Integer)iter2.next());
	    }
	    minMap.put(i, minValue);
	}

	// second pass, translate.
	int[] counts = new int[nextLabel];
	for (int y = 0; y < board.getSize(); y++) {
	    for (int x = 0; x < board.getSize(); x++) {
		labels[y][x] = minMap.get(labels[y][x]);
		counts[labels[y][x]]++;
	    }
	}
	countMap = new HashMap<Integer, Integer>();
	neighbourMap = new HashMap<Integer, Vector<Location> >();
	for (int i = 0; i < nextLabel; i++) {
	    if (counts[i] > 0) {
		countMap.put(i, counts[i]);
		HashSet<Location> locations = new HashSet<Location>();
		HashSet<Integer> e = equiv.get(i);
		iter = e.iterator();
		while (iter.hasNext()) {
		    locations.addAll(neighbours.get(iter.next()));
		}
		Vector<Location> vl = new Vector<Location>();
		vl.addAll(locations);
		neighbourMap.put(i, vl);
	    }
	}
	}
    }
}
