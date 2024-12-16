package mazegame.scenes;

import com.sun.opengl.util.GLUT;

import javax.media.opengl.*;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HowToPlayScene implements GLEventListener, KeyListener {
    private JFrame frame;
    private GLCanvas canvas;
    private GLUT glut;

    public void start() {
        System.out.println("instructions ....");

        frame = new JFrame("Maze Game - How To Play");
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        frame.add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        glut = new GLUT();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        // Draw header at the top
        drawText(gl, "Instructions", 0.7f);

        // Draw the instruction list below the header
        float startY = 0.5f; // Start slightly below the header
        float lineSpacing = -0.15f; // Spacing between each line
        drawText(gl, "* Use arrow keys to move the pointer", startY + 0 * lineSpacing);
        drawText(gl, "* Reach the escape gate before the time runs out", startY + 1 * lineSpacing);
        drawText(gl, "* Yellow color represents the path traced", startY + 2 * lineSpacing);
        drawText(gl, "* If you are lost press 1 to relocate to the beginning gate", startY + 3 * lineSpacing);
        drawText(gl, "* You have 90 seconds to finish the game", startY + 4 * lineSpacing);
        drawText(gl, "* Press ESC to return to the main menu", startY + 5 * lineSpacing);
    }

    private void drawText(GL gl, String text, float y) {
        gl.glDisable(GL.GL_TEXTURE_2D); // Disable textures to avoid interference with text rendering
        gl.glDisable(GL.GL_BLEND); // Disable blending for text to apply colors correctly

        // Set the text color
        gl.glColor3f(0,0,0); // Set black color for text

        // Calculate the width of the text in pixels
        int textWidth = 0;
        for (char c : text.toCharArray()) {
            textWidth += glut.glutBitmapWidth(GLUT.BITMAP_HELVETICA_18, c);
        }

        // Calculate x position to center the text
        float x = -((float) textWidth / 800); // Assuming 800 is the width of the window
        gl.glRasterPos2f(x, y);

        // Draw each character
        for (char c : text.toCharArray()) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, c);
        }

        gl.glEnable(GL.GL_BLEND); // Re-enable blending
        gl.glEnable(GL.GL_TEXTURE_2D); // Re-enable textures

    }
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE) {
            frame.dispose();
            Menu mainMenu = new Menu();
            mainMenu.start();
        }
    }
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}