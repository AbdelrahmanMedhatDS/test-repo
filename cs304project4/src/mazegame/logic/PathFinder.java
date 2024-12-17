package mazegame.logic;

import java.util.*;

public class PathFinder {
    private final int[][] maze;
    private final int rows, cols;

    public PathFinder(int[][] maze) {
        this.maze = maze;
        this.rows = maze.length;
        this.cols = maze[0].length;
    }

    // to find and store the path
    public List<int[]> findPath(int startY, int startX, int endY, int endX) {
        // 2D array to store the previous cell for each visited cell
        int[][] previous = new int[rows][cols];
        for (int[] row : previous) {
            Arrays.fill(row, -1);  // Initialize with -1
        }

        // Queue for breadth-first search
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];

        // Add start position to queue
        queue.add(new int[]{startY, startX});
        visited[startY][startX] = true;

        // Possible movement directions: up, down, right, left
        int[] dy = {-1, 1, 0, 0};
        int[] dx = {0, 0, 1, -1};

        boolean pathFound = false;

        // Find path
        while (!queue.isEmpty() && !pathFound) {
            int[] current = queue.poll();
            int y = current[0];
            int x = current[1];

            // If exit is reached
            if (y == endY && x == endX) {
                pathFound = true;
                break;
            }

            // Explore neighboring cells
            for (int i = 0; i < 4; i++) {
                int newY = y + dy[i];
                int newX = x + dx[i];

                // Check if new position is valid
                if (newY >= 0 && newY < rows && newX >= 0 && newX < cols
                        && maze[newY][newX] == 0 && !visited[newY][newX]) {
                    queue.add(new int[]{newY, newX});
                    visited[newY][newX] = true;
                    previous[newY][newX] = y * cols + x;  // Store previous cell's coordinates
                }
            }
        }

        // If path is found, backtrack and create path list
        List<int[]> path = new ArrayList<>();
        if (pathFound) {
            int y = endY;
            int x = endX;

            while (y != startY || x != startX) {
                path.add(new int[]{x, y});  // Add current cell to path

                // Move to previous cell
                int prevIndex = previous[y][x];
                int prevY = prevIndex / cols;
                int prevX = prevIndex % cols;

                y = prevY;
                x = prevX;
            }

            // Add start cell
            path.add(new int[]{startX, startY});

            // Reverse the path to go from start to end
            Collections.reverse(path);
        }

        return path;
    }

    // to check if path exists
    public boolean isPathExist(int startY, int startX, int endY, int endX) {
        return !findPath(startY, startX, endY, endX).isEmpty();
    }

    // to print path (for debugging)
    public void printPath(int startY, int startX, int endY, int endX) {
        List<int[]> path = findPath(startY, startX, endY, endX);

        if (path.isEmpty()) {
            System.out.println("Path from (" + startY + "," + startX + ") to (" +
                    endY + "," + endX + "): NOT FOUND");
        } else {
            System.out.println("Path from (" + startY + "," + startX + ") to (" +
                    endY + "," + endX + "): EXISTS");

            System.out.println("Path cells:");
            for (int[] cell : path) {
                System.out.println("(" + cell[1] + "," + cell[0] + ")");
            }
        }
    }
}