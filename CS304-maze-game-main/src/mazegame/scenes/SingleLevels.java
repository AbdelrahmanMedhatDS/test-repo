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

public class SingleLevels implements GLEventListener, KeyListener {
    private JFrame frame;
    private GLUT glut;
    public static int myStart=0;

    // صورة الأزرار
    private final String[] textureNames = {"levels.jpg"};
    private final int textureLen = textureNames.length;
    private int[] textureID = new int[textureLen];
    private TextureReader.Texture[] textures = new TextureReader.Texture[textureLen];

    // حالة تأثيرات التفاعل مع الأزرار
    private boolean[] isButtonHovered = new boolean[textureLen];

    public void start() {
        System.out.println("main menu ....");

        frame = new JFrame("Maze Game - Main Menu");

        GLCapabilities capabilities = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        // إضافة MouseListener للاستماع للنقرات
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY(), canvas.getWidth(), canvas.getHeight());
            }
        });


        frame.add(canvas);
        frame.setSize(800, 600);
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

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        drawBackground(gl, drawable);

    }



    public void drawBackground(GL gl, GLAutoDrawable drawable) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[0]);

        gl.glPushMatrix();

        // الحصول على أبعاد الشاشة (حجم الـ Canvas)
        int width = drawable.getWidth();  // أبعاد الـ Canvas الفعلية
        int height = drawable.getHeight(); // أبعاد الـ Canvas الفعلية

        // ملء مساحة كامل النافذة (Canvas) مع تصغير الصورة لتناسب
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
        // تحويل إحداثيات الماوس من شاشة إلى إحداثيات OpenGL
        float normalizedX = (2.0f * mouseX) / canvasWidth - 1.0f;
        float normalizedY = 1.0f - (2.0f * mouseY) / canvasHeight;

        // التحقق من الضغط على الأزرار
        if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= 0.50f && normalizedY <= 0.73f) {
            easy();
        } else if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.05 && normalizedY <= 0.18) {
            normal(); // الزر الجديد (Start 2)
        } else if (normalizedX >= -0.36f && normalizedX <= 0.38f && normalizedY >= -0.65f && normalizedY <= -0.42f) {
            hard();
        }
        else if (normalizedX >= -0.8f && normalizedX <= -0.05f && normalizedY >= -0.94f && normalizedY <= -0.82f) {
            exitGame();
        }

    }


    public void exitGame() {
        System.out.println("Exit Game");
        System.out.println("START GAME ");
        frame.dispose();
        MainMenuScene mainMenuScene = new MainMenuScene();
        mainMenuScene.start();
    }
    public void normal() {
        myStart=2;
        frame.dispose();
        GameScene singleGameScene = new GameScene(2);
        singleGameScene.start();
    }

    public void easy() {
        myStart=1;
        frame.dispose();
        GameScene singleGameScene = new GameScene(1);
        singleGameScene.start();
    }


    public void hard() {
        myStart=3;
        frame.dispose();
        GameScene singleGameScene = new GameScene(3);
        singleGameScene.start();
    }


    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_ESCAPE) {
            frame.dispose();
            MainMenuScene mainMenu = new MainMenuScene();
            mainMenu.start();
        }
        if (keyCode == KeyEvent.VK_1 || keyCode == e.VK_NUMPAD1) {
            normal();
        } else if (keyCode == KeyEvent.VK_2 || keyCode == e.VK_NUMPAD2) {
            normal(); // الزر الجديد Start 2
        } else if (keyCode == KeyEvent.VK_3 || keyCode == e.VK_NUMPAD3) {
            hard();
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
