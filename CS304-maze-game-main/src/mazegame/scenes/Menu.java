package mazegame.scenes;

import static utilities.texture.TextureReader.readTexture;

import utilities.texture.TextureReader;
import com.sun.opengl.util.GLUT;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import mazegame.logic.musicPlayer;

public class Menu implements GLEventListener, KeyListener {
    private JFrame frame;
    private GLUT glut;
    private static musicPlayer musicPlayer = new musicPlayer();

    public static musicPlayer getMusicPlayer() {
        return musicPlayer;
    }
    private final String[] textureNames = {"back1.jpg"};
    private final int textureLen = textureNames.length;
    private int[] textureID = new int[textureLen];
    private TextureReader.Texture[] textures = new TextureReader.Texture[textureLen];


    public void start() {
        System.out.println("main menu ....");
        musicPlayer.playBackgroundMusic("src/utilities/sounds/home.wav");
        frame = new JFrame("Maze Game - START");

        GLCapabilities capabilities = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY(), canvas.getWidth(), canvas.getHeight());

            }
        });


        frame.add(canvas);
        frame.setSize(1300, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        glut = new GLUT();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glGenTextures(textureLen, textureID, 0);
        for (int i = 0; i < textureLen; i++) {
            try {
                gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[i]);
                textures[i] = readTexture("./utilities/images/" + textureNames[i], true);

                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA,
                        textures[i].getWidth(),
                        textures[i].getHeight(),
                        GL.GL_RGBA,
                        GL.GL_UNSIGNED_BYTE,
                        textures[i].getPixels()
                );
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        gl.glColor3f(1f, 1f, 1f);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        drawBackground(gl, drawable, 0);
    }

    public void drawBackground(GL gl, GLAutoDrawable drawable, int textureIndex) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[textureIndex]);

        gl.glPushMatrix();

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();

        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }


    public void handleMouseClick(int mouseX, int mouseY, int canvasWidth, int canvasHeight) {
        float normalizedX = (2.0f * mouseX) / canvasWidth - 1.0f;
        float normalizedY = 1.0f - (2.0f * mouseY) / canvasHeight;

        if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.13f && normalizedY <= 0.1f) {
            musicPlayer.playSoundEffect("src/utilities/sounds/toy-button.wav");
            starGame();
        } else if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.45f && normalizedY <= -0.23f) {
            musicPlayer.playSoundEffect("src/utilities/sounds/toy-button.wav");
            showHowToPlay();
        } else if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.805f && normalizedY <= -0.57f) {
            exitGame();
        }
    }


    public void starGame() {
        System.out.println("START GAME ");
        frame.dispose();
        MainMenuScene mainMenuScene = new MainMenuScene();
        mainMenuScene.start();
    }

    public void showHowToPlay() {
        System.out.println("Show How to Play");
        frame.dispose();
        HowToPlayScene howToPlayScene = new HowToPlayScene();
        howToPlayScene.start();
    }

    public void exitGame() {
        System.out.println("Exit Game");
        System.exit(0);
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == e.VK_ESCAPE) {
            exitGame();
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}