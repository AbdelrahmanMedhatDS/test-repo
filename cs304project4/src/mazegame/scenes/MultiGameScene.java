/*
the first thing to be called is the constructor
then, the start method which has the construction of
- JFrame
- GLCanvas
- and add the listeners

which led us to call the init once
then call the display at least once if no animator
the reshape method is called when we're resizing the JFrame manually i.e. maximize etc...
 */

package mazegame.scenes;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;
import mazegame.logic.MazeGenerator;
import mazegame.logic.PathFinder;
import mazegame.logic.Timer;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class MultiGameScene implements GLEventListener, KeyListener {

    private GLU glu;
    private int[][] maze;
    private MazeGenerator mazeGenerator;

    private int player1X, player1Y;
    private int player2X, player2Y;

    // column must be = rows + 20 && both are odd.
    private int rows = 31; // easy -->11 // medium --> 31 // hard --> 51 // solve if you can -->71
    private int cols = 51; // easy -->31  // medium --> 51 // hard --> 71 // solve if you can -->91

    // Time limit in seconds for each player
    private int gameTime = 90;
    private Timer player1Timer;
    private Timer player2Timer;


    private boolean gameOver = false;
    private boolean takeHint = false;
    private boolean isP1 = false;
    private boolean isP2 = false;

    private int whoTakeTheHint;
    private int penalty = 20;
    private boolean gamePaused = false;


    public MultiGameScene() {
    } // default constructor

    public MultiGameScene(int levelSelection) {
        // used constructor to handle the level selection feature
        if (levelSelection == 1) {
            this.rows = 11;
            this.cols = 31;
            gameTime = 120;
            penalty = 10;

        }
        if (levelSelection == 2) {
            this.rows = 21;
            this.cols = 41;
            gameTime = 100;
            penalty = 20;

        }
        if (levelSelection == 3) {
            this.rows = 31;
            this.cols = 51;
            gameTime = 90;
            penalty = 20;

        }
    }

    JFrame frame;

    public void start() {
        System.out.println("game scene ....");

        frame = new JFrame("Maze Game - Playing");
        GLCanvas canvas = new GLCanvas();
        frame.add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();

    }


    @Override
    public void init(GLAutoDrawable drawable) { // starting point of execution for GameScene after start
        System.out.println("init");
        GL gl = drawable.getGL();
        glu = new GLU();
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);

        // Enable blending for transparency
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        generateMaze();
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        drawMaze(gl);
        drawPlayer(gl, player1X, player1Y, 1);
        drawPlayer(gl, player2X, player2Y, 2);

        if (gamePaused) {
            drawPauseOverlay(gl);
        }



        // Display player timers
        displayTimers(gl);

        // Only update and check timers if not paused
        if (!gamePaused) {
            checkGameOver();

        }

    }


    private void generateMaze() {
        mazeGenerator = new MazeGenerator(rows, cols);
        maze = mazeGenerator.generate();

        // Set player start positions
        player1X = mazeGenerator.getPlayer1X();
        player1Y = mazeGenerator.getPlayer1Y();
        player2X = mazeGenerator.getPlayer2X();
        player2Y = mazeGenerator.getPlayer2Y();

        //Create and start player timers
        player1Timer = new Timer(gameTime);
        player2Timer = new Timer(gameTime);

    }

    private void drawMaze(GL gl) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (maze[row][col] == 1) {
                    drawSquare(gl, col, row, 0.5f, 0.5f, 0.5f);
                }
            }
        }
    }

    private void drawPlayer(GL gl, int x, int y, int playerNumber) {
        float r = (playerNumber == 1) ? 0.0f : 1.0f;
        float g = (playerNumber == 1) ? 1.0f : 0.0f;
        float b = (playerNumber == 1) ? 0.0f : 0.0f;
        drawSquare(gl, x, y, r, g, b);
    }

    private void drawPauseOverlay(GL gl) {
        // Set the transparency level (alpha)
        float alpha = 0.7f;  // 70% transparent

        // Draw a semi-transparent BLACK overlay for the whole screen
        // total width is  " col +  2 extra space = 2*3 "
        // total height is " row +  2 extra space = 2*3 "
        gl.glColor4f(0.0f, 0.0f, 0.0f, alpha); // Black with 70% transparency
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(-3, -3);  // Bottom-left of screen (with extra space included)
        gl.glVertex2f(cols + 3, -3);  // Bottom-right
        gl.glVertex2f(cols + 3, rows + 3);  // Top-right
        gl.glVertex2f(-3, rows + 3);  // Top-left
        gl.glEnd();

        // Draw the pause symbol (two vertical bars)
        float barWidth = 1f;  // Width of each pause bar
        float barHeight = 5f;  // Height of the pause bar
        float barSpacing = 0.5f; // Space between the two bars

        // roughly, Center of the screen
        float centerX = cols / 2.0f;
        float centerY = rows / 2.0f;

        // First bar (left bar)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);  // White, no transparency
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(centerX - barWidth - barSpacing, centerY - barHeight / 2);
        gl.glVertex2f(centerX - barSpacing, centerY - barHeight / 2);
        gl.glVertex2f(centerX - barSpacing, centerY + barHeight / 2);
        gl.glVertex2f(centerX - barWidth - barSpacing, centerY + barHeight / 2);
        gl.glEnd();

        // Second bar (right bar)
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(centerX + barSpacing, centerY - barHeight / 2);
        gl.glVertex2f(centerX + barWidth + barSpacing, centerY - barHeight / 2);
        gl.glVertex2f(centerX + barWidth + barSpacing, centerY + barHeight / 2);
        gl.glVertex2f(centerX + barSpacing, centerY + barHeight / 2);
        gl.glEnd();
    }

    private void displayTimers(GL gl) {
        GLUT glut = new GLUT();

        // Player 1 Timer - Top Left Corner
        gl.glColor3f(0.0f, 1.0f, 0.0f); // green color
        gl.glRasterPos2f(-2, rows + 2); // Positioned just above the maze on the left side
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "P1 Time: " + player1Timer.getTimeLeft() + "s");

        // Player 2 Timer - Top Right Corner
        gl.glColor3f(1.0f, 0.0f, 0.0f); // red color
        gl.glRasterPos2f(cols - 6, rows + 2); // Positioned just above the maze on the right side
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "P2 Time: " + player2Timer.getTimeLeft() + "s");
    }

    private void drawSinglePathHighlight(GL gl, int startY, int startX, int endY, int endX, float[] color) {
        PathFinder pathFinder = new PathFinder(maze);

        // Find the path
        List<int[]> path = pathFinder.findPath(startY, startX, endY, endX);

        // Draw the path
        for (int[] cell : path) {
            int x = cell[0];
            int y = cell[1];

            gl.glColor4f(color[0], color[1], color[2], color[3]);
            gl.glBegin(GL.GL_QUADS);
            gl.glVertex2f(x, y);
            gl.glVertex2f(x + 1, y);
            gl.glVertex2f(x + 1, y + 1);
            gl.glVertex2f(x, y + 1);
            gl.glEnd();
        }
    }


    private void checkGameOver() {
        if (player1Timer.hasExpired()) {
            System.out.println("Player 1 ran out of time!");
            gameOver = true;

//            GameOverScene gameoverScene = new GameOverScene();
//            frame.dispose();
//            gameoverScene.start();

        }

        else if (player2Timer.hasExpired()) {
            System.out.println("Player 2 ran out of time!");
            gameOver = true;
//
//            GameOverScene gameoverScene = new GameOverScene();
//            frame.dispose();
//            gameoverScene.start();
        }

        else if (player2Timer.hasExpired() && player1Timer.hasExpired()) {
            System.out.println("Both Players, ran out of time!");
            System.out.println("DRAW");
        }
    }

    private void Penalty() {
        if (whoTakeTheHint == 1) player1Timer.reduceTime(penalty);
        else if (whoTakeTheHint == 2) player2Timer.reduceTime(penalty);
        whoTakeTheHint = 0;
    }

    private void resetPlayer1() {
        // Reset the init values
        player1X = mazeGenerator.getPlayer1X();
        player1Y = mazeGenerator.getPlayer1Y();
    }

    private void resetPlayer2() {
        // Reset the init values
        player2X = mazeGenerator.getPlayer2X();
        player2Y = mazeGenerator.getPlayer2Y();
    }

    private void drawSquare(GL gl, int x, int y, float r, float g, float b) {
        gl.glColor3f(r, g, b);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + 1, y);
        gl.glVertex2f(x + 1, y + 1);
        gl.glVertex2f(x, y + 1);
        gl.glEnd();
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("reshape");

        GL gl = drawable.getGL();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        /*
        - Set the coordinate system to match your maze size
          glu.gluOrtho2D(0.0, cols , 0.0, rows);
        - OR Calculate the extra space around the maze
         */

        int extraSpace = 3; // Adjust this value as needed

        // Set the coordinate system to include extra space
        glu.gluOrtho2D(-extraSpace, cols + extraSpace, -extraSpace, rows + extraSpace);

        gl.glViewport(0, 0, width, height); // Set the viewport
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        try {

            int key = e.getKeyCode();

            if (!gamePaused) {
                // player_1 Moving
                if (key == KeyEvent.VK_UP && maze[player1Y + 1][player1X] == 0) player1Y++;
                else if (key == KeyEvent.VK_DOWN && maze[player1Y - 1][player1X] == 0) player1Y--;
                else if (key == KeyEvent.VK_LEFT && maze[player1Y][player1X - 1] == 0) player1X--;
                else if (key == KeyEvent.VK_RIGHT && maze[player1Y][player1X + 1] == 0) player1X++;


                // player_2 Moving
                if (key == KeyEvent.VK_W && maze[player2Y + 1][player2X] == 0) player2Y++;
                else if (key == KeyEvent.VK_S && maze[player2Y - 1][player2X] == 0) player2Y--;
                else if (key == KeyEvent.VK_A && maze[player2Y][player2X - 1] == 0) player2X--;
                else if (key == KeyEvent.VK_D && maze[player2Y][player2X + 1] == 0) player2X++;

                // reload the game
                if (key == KeyEvent.VK_R) {
                    generateMaze();
                }

                // re-set the places
                if (key == KeyEvent.VK_0 || key == KeyEvent.VK_NUMPAD0) {
                    resetPlayer1();
                }
                if (key == KeyEvent.VK_SPACE) {
                    resetPlayer2();
                }

                if (key == KeyEvent.VK_H) {
                    takeHint = true;
                }

                if (key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) {
                    isP1 = true;
                    whoTakeTheHint = 1;
                }

                if (key == KeyEvent.VK_2 || key == KeyEvent.VK_NUMPAD2) {
                    isP2 = true;
                    whoTakeTheHint = 2;
                }
            }

            // Minimize and Maximize the JFrame
            if (key == KeyEvent.VK_F) frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            else if (key == KeyEvent.VK_ESCAPE && frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                frame.setExtendedState(JFrame.NORMAL);
            }

            // back to the MainMenu


            // the Pause Feature Handling
            if (key == KeyEvent.VK_P) {
                gamePaused = !gamePaused;

                if (gamePaused) {
                    // Pause timers
                    player1Timer.pause();
                    player2Timer.pause();
                } else {
                    // Resume timers
                    player1Timer.resume();
                    player2Timer.resume();
                }
            }

        } catch (Exception exception) {
            System.err.println("there is and INPUT_ERROR ");
            System.out.print(" :-" + exception.toString());
        }

    }


    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}