package de.ibo.day12;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Router {

	// map of y,x
	char[][] map;
	int dimX, dimY;

	int[][] mapDistance;

	List<Point> frontLine = new LinkedList<>();
	int stepMax = 10000;
	Point start;
	Point end;
	private int step;

	//	List<List<Point>> traces = new ArrayList<>();

	class Point {

		int x;
		int y;

		Point (int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Point [x=" + x + ", y=" + y + "]";
		}
	}

	void read(String filename) throws IOException {
		LineNumberReader r = new LineNumberReader(new FileReader(filename));

		String line = null;

		ArrayList<String> tmp = new ArrayList<>();

		while ( (line = r.readLine()) != null) {
			tmp.add(line.trim());
		}

		// map of y,x
		map = new char[tmp.size()][tmp.get(0).length()];
		int y=0;

		for (String entry : tmp) {
			for (int x = 0; x < entry.length(); x++) {
				map[y][x] = entry.charAt(x);
			}
			y++;
		}
	}

	void dump() {
		dumpMap();
	}

	void dumpMap() {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				System.out.print(map[y][x]);
			}
			System.out.println();
		}
	}

	void dumpMapDistance() {
		for (int y = 0; y < mapDistance.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				System.out.print(map[y][x]);
			}
			System.out.print(" ");
			for (int x = 0; x < mapDistance[y].length; x++) {
				if (start.x == x && start.y == y) System.out.print("S");
				else if (end.x == x && end.y == y) System.out.print("E");
				else if (mapDistance[y][x] == step - 1 ) {
					System.out.print("X");
				}
				else System.out.print(Integer.toHexString(mapDistance[y][x] % 16));
			}
			System.out.println();
		}
	}
	
	void dumpMixed() {
		for (int y = 0; y < mapDistance.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				System.out.print(map[y][x]);
			}
			System.out.print(" ");
			
			for (int x = 0; x < mapDistance[y].length; x++) {
				if (start.x == x && start.y == y) System.out.print("S");
				else if (end.x == x && end.y == y) System.out.print("E");
				else if ( mapDistance[y][x] == step - 1 ) {
					System.out.print("X");
				} 
				else if (step - mapDistance[y][x] <= 50 ) {
					System.out.print(map[y][x]);
				} 
				else if (mapDistance[y][x] == 0 ) {
					System.out.print(".");
				}
				else {
//					System.out.print(Integer.toHexString(mapDistance[y][x] % 16));
					System.out.print("*");
				}
			}
			System.out.println();
		}
	}

	int height(int x, int y) {
		return height(map[y][x]);
	}

	int height(char c) {
		if (c == 'S') c = 'a';
		else if (c == 'E') c = 'z';

		int offset = (int)'a';

		int height = (int)c - offset;

		return height;
	}

	boolean isAllowed(int x, int y, Point ref) {
		if ( isInArea(x, y) ) {
			return ( 
					isWithinHeight(x, y, ref) 
					&& isShortest(x, y)
					);
		}

		return false;

	}

	boolean isWithinHeight(int x, int y, Point ref) {
		return (height(x,y) - height(ref.x, ref.y)) <= 1;
	}

	boolean isInArea(int x, int y) {
		return (y >= 0 && x >= 0 && y < dimY && x < dimX);
	}

	boolean isShortest(int x, int y) {
		return mapDistance[y][x] == 0; // only if there is not prior entry in the distance map, no other trace has ended up here before
	}

	Point getStart() {
		return find('S');
	}

	Point getEnd() {
		return find('E');
	}

	Point find(char c) {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if (map[y][x] == c) return new Point(x,y);
			}
		}
		return null;
	}
	
	List<Point> findAll(char c) {
		List<Point> points = new LinkedList<>();
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if (map[y][x] == c) points.add(new Point(x,y));
			}
		}
		return points;
	}

	boolean findRoute(Point start) {
		frontLine.add(start);
		step = 1;

		int[][] searchPattern = { {-1 ,0}, {1 ,0}, {0, -1}, {0, 1} };
		
		while(step < stepMax) {

			List<Point> nexts = new LinkedList<>();

			for (Point origin : frontLine) {
				for (int[] offset : searchPattern) {
					int x = origin.x + offset[0];
					int y = origin.y + offset[1];
					if (isAllowed(x, y, origin)) {
						Point p = new Point(x, y);
						if (end.x == p.x && end.y == p.y) {
							System.out.println("FOUND, steps: "+step);
							return true;
						}
						mapDistance[p.y][p.x] = step;
						nexts.add(p);
					} 	
				}
			}
			if (nexts.size() == 0) {
				System.out.println("no possilbe steps, steps: "+step);
				return false;
			}
			frontLine = nexts;
			step++;
		}
		System.out.println("not found, steps: "+step);
		return false;
	}


	
	void solveSingle() throws IOException {
		
		mapDistance = new int[dimY][dimX];
		frontLine = new LinkedList<>();
		start = getStart();
		end = getEnd();
		
		findRoute(start);
		dumpMixed();
	}
	
	void solveAll_a() throws IOException {
		List<Point> starts = findAll('a');
		starts.add(getStart());
		
		System.out.println("# start points: "+starts.size());
		
		end = getEnd();
		
		int shortestSteps = Integer.MAX_VALUE;
		
		int count = 0;
		for (Point s : starts) {
			mapDistance = new int[dimY][dimX];
			frontLine = new LinkedList<>();
			
			start = s;
			boolean found = findRoute(start);
			if (found) {
				if (step < shortestSteps) shortestSteps = step;
				System.out.println("found: "+count);
//				dumpMixed();
			} else {
				System.out.println("NOT found: "+count);
			}
			
		}
		
		System.out.println("min steps of all a: "+shortestSteps);
		
		
	}
	
	void init() throws IOException {
		read("src/de/ibo/day12/input.txt");
		dimY = map.length;
		dimX = map[0].length;
	}

	public static void main(String[] args) throws IOException {
		Router r = new Router();
		long startMs= System.currentTimeMillis();
		r.init();
//		r.solveSingle();
		r.solveAll_a();

		//		System.out.println(r.getHeight('E'));
		System.out.println("time elapse: "+(System.currentTimeMillis()-startMs));
		System.out.println("done");

	}

}
