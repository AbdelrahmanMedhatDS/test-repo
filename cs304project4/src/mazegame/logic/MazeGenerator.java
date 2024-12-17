package mazegame.logic;

import java.util.Random;

public class MazeGenerator {
    private int[][] maze;
    private final int rows, cols;
    private Random rand;

    // Player and exit position attributes
    private int player1X, player1Y;
    private int player2X, player2Y;
    private int exitX, exitY;

    public MazeGenerator(int rows, int cols) {
        // Ensure odd dimensions for proper maze generation
        this.rows = rows % 2 == 0 ? rows + 1 : rows;
        this.cols = cols % 2 == 0 ? cols + 1 : cols;

        maze = new int[this.rows][this.cols];
        rand = new Random();
    }

    public int[][] generate() {
        // Regenerate maze until it's solvable for both players
        do {
            // Initialize maze with walls
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    maze[i][j] = 1;
                }
            }

            // Start carving from the top-left corner
            recursiveMazeGeneration(1, 1);

            // Place random entrances and exit
            placePlayerEntrancesAndExit();
        } while (!isSolvable());

        return maze;
    }

    private void placePlayerEntrancesAndExit() {
        // Array to track used edges to prevent overlap
        boolean[] usedEdges = new boolean[4];

        // Place first player entrance
        int[] entrance1 = getUniqueEdgePosition(usedEdges);
        player1X = entrance1[0];
        player1Y = entrance1[1];
        maze[player1Y][player1X] = 0;

        // Place second player entrance
        int[] entrance2 = getUniqueEdgePosition(usedEdges);
        player2X = entrance2[0];
        player2Y = entrance2[1];
        maze[player2Y][player2X] = 0;

        // Place exit
        int[] exitPosition = getUniqueEdgePosition(usedEdges);
        exitX = exitPosition[0];
        exitY = exitPosition[1];
        maze[exitY][exitX] = 0;
    }

    private int[] getUniqueEdgePosition(boolean[] usedEdges) {
        int edge;
        do {
            edge = rand.nextInt(4);
        } while (usedEdges[edge]);

        usedEdges[edge] = true;

        int x = 0, y = 0;
        switch (edge) {
            case 0: // Top edge
                x = rand.nextInt((cols - 2) / 2) * 2 + 1;
                y = 0;
                break;
            case 1: // Right edge
                x = cols - 1;
                y = rand.nextInt((rows - 2) / 2) * 2 + 1;
                break;
            case 2: // Bottom edge
                x = rand.nextInt((cols - 2) / 2) * 2 + 1;
                y = rows - 1;
                break;
            case 3: // Left edge
                x = 0;
                y = rand.nextInt((rows - 2) / 2) * 2 + 1;
                break;
        }

        return new int[]{x, y};
    }

    private void recursiveMazeGeneration(int x, int y) {
        // Mark current cell as path
        maze[y][x] = 0;

        // Randomize direction order
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        shuffleArray(directions);

        // Explore in each direction
        for (int[] dir : directions) {
            int nx = x + dir[0] * 2;
            int ny = y + dir[1] * 2;

            // Check if new position is within maze and not carved
            if (ny > 0 && ny < rows - 1 && nx > 0 && nx < cols - 1 && maze[ny][nx] == 1) {
                // Carve through the wall
                maze[y + dir[1]][x + dir[0]] = 0;
                recursiveMazeGeneration(nx, ny);
            }
        }
    }

    private void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private boolean isSolvable() {
        // Check if there's a path for both players to the exit
        PathFinder pathFinder = new PathFinder(maze);

        // First player path to exit
        boolean player1ToExit = pathFinder.isPathExist(player1Y, player1X, exitY, exitX);

        // Second player path to exit
        boolean player2ToExit = pathFinder.isPathExist(player2Y, player2X, exitY, exitX);

        return player1ToExit && player2ToExit;

    }

    // Getters for player and exit positions.
    // there is no setters as it is constructed from the maze generation.
    public int getPlayer1X() { return player1X; }
    public int getPlayer1Y() { return player1Y; }
    public int getPlayer2X() { return player2X; }
    public int getPlayer2Y() { return player2Y; }
    public int getExitX() { return exitX; }
    public int getExitY() { return exitY; }

    // Optional: Method to print maze for debugging
    public void printMaze() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x == player1X && y == player1Y) {
                    System.out.print("P1");
                } else if (x == player2X && y == player2Y) {
                    System.out.print("P2");
                } else if (x == exitX && y == exitY) {
                    System.out.print("EX");
                } else {
                    System.out.print(maze[y][x] == 1 ? "█" : "  ");
                }
            }
            System.out.println();
        }
    }
/*
Cartesian (x,y):    Matrix [y][x]:
(0,0) →x            [0][0] →column
  ↓                   ↓
  y                  row

Example:-

  int[][] matrix = new int[5][5];
// Cartesian point (3,2) translates to matrix[2][3]
matrix[2][3] = 1;  // Go down 2 rows, then 3 columns right

To make it more consistent, you could:
    Always use [y][x] in your code
    Use helper methods to translate between systems


 */
}
