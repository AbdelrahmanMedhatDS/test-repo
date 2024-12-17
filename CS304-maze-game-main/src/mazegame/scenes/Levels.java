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
//hogan
public class Levels implements GLEventListener, KeyListener {
    private JFrame frame;
    private GLUT glut;

    // صورة الأزرار
    private final String[] textureNames = {"levels.jpg"};
    private final int textureLen = textureNames.length;
    private int[] textureID = new int[textureLen];
    private TextureReader.Texture[] textures = new TextureReader.Texture[textureLen];
    private musicPlayer musicPlayer = Menu.getMusicPlayer();

    public void start() {
        System.out.println("main menu ....");

        frame = new JFrame("Maze Game - Multi Levels");
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

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
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
        drawBackground(gl, drawable);

    }

    public void drawBackground(GL gl, GLAutoDrawable drawable) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[0]);

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


        if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= 0.50f && normalizedY <= 0.73f) {
            musicPlayer.playSoundEffect("src/utilities/sounds/toy-button.wav");
            MultiEasy();
            musicPlayer.stopBackgroundMusic();
        } else if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.05 && normalizedY <= 0.18) {
            musicPlayer.playSoundEffect("src/utilities/sounds/toy-button.wav");
            MultiNormal();
            musicPlayer.stopBackgroundMusic();
        } else if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.65f && normalizedY <= -0.42f) {
            musicPlayer.playSoundEffect("src/utilities/sounds/toy-button.wav");
            MultiHard();
            musicPlayer.stopBackgroundMusic();
        } else if (normalizedX >= -0.8f && normalizedX <= -0.05f && normalizedY >= -0.94f && normalizedY <= -0.82f) {
            musicPlayer.playSoundEffect("src/utilities/sounds/toy-button.wav");
            exitGame();
        }

    }

    public void exitGame() {
        frame.dispose();
        MainMenuScene mainMenuScene = new MainMenuScene();
        mainMenuScene.start();
    }

    public void MultiEasy() {

        frame.dispose();
        MultiGameScene multiGameScene = new MultiGameScene(1);
        multiGameScene.start();
    }

    public void MultiNormal() {
        frame.dispose();
        MultiGameScene multiGameScene = new MultiGameScene(2);
        multiGameScene.start();
    }


    public void MultiHard() {

        frame.dispose();
        MultiGameScene multiGameScene = new MultiGameScene(3);
        multiGameScene.start();
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_ESCAPE||keyCode == KeyEvent.VK_BACK_SPACE) {
            frame.dispose();
            MainMenuScene mainMenu = new MainMenuScene();
            mainMenu.start();
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
