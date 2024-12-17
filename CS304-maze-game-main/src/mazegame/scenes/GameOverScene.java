package mazegame.scenes;

import com.sun.opengl.util.GLUT;
import static utilities.texture.TextureReader.readTexture;
import utilities.texture.TextureReader;
import com.sun.opengl.util.GLUT;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import mazegame.logic.musicPlayer;
//fahmy
/**
 *
 */
public class GameOverScene implements GLEventListener, KeyListener {
    int gameOverCondition=0;

    private GLUT glut;
    private musicPlayer musicPlayer = new musicPlayer();

    public GameOverScene(int gameOverCondition) {
        this.gameOverCondition = gameOverCondition;
    }

    JFrame frame;
    private final String[] textureNames = {"p1.jpg","p2.jpg","sp.jpg","d.jpg","go.jpg"};
    private final int textureLen = textureNames.length;
    private int[] textureID = new int[textureLen];
    private TextureReader.Texture[] textures = new TextureReader.Texture[textureLen];
    public void start() {
        frame = new JFrame("Maze Game - Game Over");
        GLCapabilities capabilities = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        frame.add(canvas);
        frame.setSize(1300, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        glut = new GLUT();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(1f, 1, 1,1);

//        gl.glClearColor(26/255f, 29/255f, 37/255f,0);
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

                // تحسين جودة النسيج باستخدام فلتر LINEAR
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        gl.glColor3f(1f, 1f, 1f);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        //player1 won
        if (gameOverCondition == 1) {
            musicPlayer.playSoundEffect("src/utilities/sounds/winGame.wav");
            gameover(gl, glAutoDrawable,0);
        }
        //player2 won
        if (gameOverCondition == 2) {
            musicPlayer.playSoundEffect("src/utilities/sounds/winGame.wav");
            gameover(gl, glAutoDrawable,1);
        }
        //single player won
        if (gameOverCondition == 3) {
            musicPlayer.playSoundEffect("src/utilities/sounds/winGame.wav");
            gameover(gl, glAutoDrawable,2);

        }
        //Single player time expired
        if (gameOverCondition == 4) {
            musicPlayer.playSoundEffect("src/utilities/sounds/loseGame.wav");
            gameover(gl, glAutoDrawable,4);
        }
        //time expired, the game ended as a draw
        if (gameOverCondition == 5) {
            musicPlayer.playSoundEffect("src/utilities/sounds/loseGame.wav");
            gameover(gl, glAutoDrawable,3);
        }
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {

    }
    private void drawText(GL gl, String text, float x, float y) {
        gl.glColor3f(toRGB(255), toRGB(255), toRGB(255));
//        gl.glColor3f(0f,0f,0f);
        gl.glRasterPos2f(x, y);

        for (char c : text.toCharArray()) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, c);
        }
    }
    public void gameover (GL gl, GLAutoDrawable drawable,int textureIndex) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[textureIndex]);

        gl.glPushMatrix();

        int width = drawable.getWidth();  // أبعاد الـ Canvas الفعلية
        int height = drawable.getHeight(); // أبعاد الـ Canvas الفعلية

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

    public float toRGB(int color){return (color/255f);}

}
