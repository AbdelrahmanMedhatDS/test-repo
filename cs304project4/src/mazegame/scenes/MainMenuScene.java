package mazegame.scenes;

import static utilities.texture.TextureReader.readTexture;
import utilities.texture.TextureReader;
import com.sun.opengl.util.GLUT;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;



public class MainMenuScene implements GLEventListener, KeyListener {
    private JFrame frame;
    private GLUT glut;
    public void start() {
        System.out.println("main menu ....");

        frame = new JFrame("Maze Game - Main Menu");

        GLCapabilities capabilities = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        frame.add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

         glut = new GLUT();
    }


    // -- back1 code --

    private final String[] textureNames = {"back1.png"};
    private final int textureLen = textureNames.length;
    private int[] textureID = new int[textureLen];
    private TextureReader.Texture[] textures = new TextureReader.Texture[textureLen];

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

//        gl.glClearColor(0.10980392f, 0.125490f, 0.1450980f, 1.0f);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);



        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureLen, textureID, 0);
        for(int i = 0; i < textureLen; i++){
            try {

                gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[i]);
                textures[i] = readTexture("./utilities/images/"+textureNames[i] ,true);

                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA,
                        textures[i].getWidth(),
                        textures[i].getHeight(),
                        GL.GL_RGBA,
                        GL.GL_UNSIGNED_BYTE,
                        textures[i].getPixels()
                );
            }
            catch( IOException e ) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        gl.glColor3f(1f,1f,1f);

    }


    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        drawBackground(gl);
        gl.glEnable(GL.GL_BLEND);
        drawText(gl, "PRESS: 1 OR 2 OR 3", -0.3f, -0.2f);
        drawText(gl, "1 -> Start Play", -0.3f, -0.4f);
        drawText(gl, "2 -> How to Play", -0.3f, -0.6f);
        drawText(gl, "3 -> Exit", -0.3f, -0.8f);
        gl.glDisable(GL.GL_BLEND);
    }

    public void drawBackground(GL gl){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[0]);

        gl.glPushMatrix();
        gl.glScaled(0.5, 0.5, 1);  // Scale the texture to 50% of its original size
        gl.glTranslated(0, 1, 0);  // Translate it to the top of the window

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
    private void drawText(GL gl, String text, float x, float y) {
        gl.glColor3f(toRGB(232), toRGB(230), toRGB(220));
//        gl.glColor3f(0f,0f,0f);
        gl.glRasterPos2f(x, y);

        for (char c : text.toCharArray()) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, c);
        }
    }

    public float toRGB(int color){return (color/255f);}

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_1 || keyCode == e.VK_NUMPAD1 ) {
            frame.dispose();
            MultiGameScene gameScene = new MultiGameScene();
            gameScene.start();
        } else if (keyCode == KeyEvent.VK_2 || keyCode == e.VK_NUMPAD2) {
            frame.dispose();
            HowToPlayScene howToPlayScene = new HowToPlayScene();
            howToPlayScene.start();
        } else if (keyCode == KeyEvent.VK_3 || keyCode == e.VK_NUMPAD3) {
            System.exit(0);
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
