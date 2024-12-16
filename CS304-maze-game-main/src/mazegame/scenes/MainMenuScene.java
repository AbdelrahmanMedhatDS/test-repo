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

public class MainMenuScene implements GLEventListener, KeyListener {
    private JFrame frame;
    private GLUT glut;

    // صورة الأزرار
    private final String[] textureNames = {"back1.Png", "start.png", "Multi.png", "exit.png", "howToPlay.png"};
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

        // إضافة MouseMotionListener لتحسين تأثيرات التفاعل مع الأزرار
        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseOver(e.getX(), e.getY(), canvas.getWidth(), canvas.getHeight());
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

        gl.glClearColor(26/255f, 29/255f, 37/255f,0);
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
        drawButtons(gl, drawable);
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
        gl.glVertex3f(-1f, 0.0f, -1.0f);  // الزاوية السفلية اليسرى
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1f, 0.0f, -1.0f);   // الزاوية السفلية اليمنى
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1f, 1.0f, -1.0f);    // الزاوية العليا اليمنى
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1f, 1.0f, -1.0f);   // الزاوية العليا اليسرى
        gl.glEnd();

        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public void drawButtons(GL gl, GLAutoDrawable drawable) {
        // تحديد أبعاد الأزرار ومواقعها
        float buttonWidth = 0.6f;  // زيادة العرض
        float buttonHeight = 0.25f; // زيادة الارتفاع

        // زر 1 (Start)
        drawButton(gl, -0.9f, -0.3f, buttonWidth, buttonHeight, 1);

        // زر 2 (Start 2) - كان "How to Play" في السابق
        drawButton(gl, 0.3f, -0.3f, buttonWidth, buttonHeight, 2);

        // زر 3 (Exit)
        drawButton(gl, -0.9f, -0.8f, buttonWidth, buttonHeight, 3);

        // زر 4 (How to Play)
        drawButton(gl, 0.3f, -0.8f, buttonWidth, buttonHeight, 4);  // زر Start 2 في المكان الجديد
    }

    public void drawButton(GL gl, float x, float y, float width, float height, int textureIndex) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0f);  // تحديد موقع الزر
        // رسم الصورة كزر
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[textureIndex]);

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(0, 0, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(width, 0, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(width, height, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(0, height, 0);
        gl.glEnd();

        gl.glPopMatrix();
    }

    public void handleMouseClick(int mouseX, int mouseY, int canvasWidth, int canvasHeight) {
        // تحويل إحداثيات الماوس من شاشة إلى إحداثيات OpenGL
        float normalizedX = (2.0f * mouseX) / canvasWidth - 1.0f;
        float normalizedY = 1.0f - (2.0f * mouseY) / canvasHeight;

        // التحقق من الضغط على الأزرار
        if (normalizedX >= -0.9f && normalizedX <= -0.3f && normalizedY >= -0.3f && normalizedY <= -0.05f) {
            single();
        } else if (normalizedX >= 0.3f && normalizedX <= 0.9f && normalizedY >= -0.3f && normalizedY <= -0.05f) {
            startGame(); // الزر الجديد (Start 2)
        } else if (normalizedX >= -0.9f && normalizedX <= -0.3f && normalizedY >= -0.8f && normalizedY <= -0.55f) {
            exitGame();
        } else if (normalizedX >= 0.3f && normalizedX <= 0.9f && normalizedY >= -0.8f && normalizedY <= -0.55f) {
            showHowToPlay();  // زر "How to Play"
        }
    }

    public void handleMouseOver(int mouseX, int mouseY, int canvasWidth, int canvasHeight) {
        // تحويل إحداثيات الماوس من شاشة إلى إحداثيات OpenGL
        float normalizedX = (2.0f * mouseX) / canvasWidth - 1.0f;
        float normalizedY = 1.0f - (2.0f * mouseY) / canvasHeight;

        // التحقق من التفاعل مع الأزرار
        isButtonHovered[0] = normalizedX >=  -0.9f && normalizedX <= -0.3f && normalizedY >= -0.3f && normalizedY <= -0.05f;
        isButtonHovered[1] = normalizedX >= 0.3f && normalizedX <= 0.9f && normalizedY >= -0.3f && normalizedY <= -0.05f;
        isButtonHovered[2] = normalizedX >= -0.9f && normalizedX <= -0.3f && normalizedY >= -0.8f && normalizedY <= -0.55f;
        isButtonHovered[3] = normalizedX >= 0.3f && normalizedX <= 0.9f && normalizedY >= -0.8f && normalizedY <= -0.55f; // زر Start 2
    }

    public void startGame() {
        System.out.println("Start Game");
        frame.dispose();
        Levels multiGameScene = new Levels();
        multiGameScene.start();
    }
    public void single() {
        System.out.println("single Game");
        frame.dispose();
        SingleLevels singleGameScene = new SingleLevels();
        singleGameScene.start();
    }

    public void showHowToPlay() {
        System.out.println("Show How to Play");
        frame.dispose();
        HowToPlayScene howToPlayScene = new HowToPlayScene();
        howToPlayScene.start();
    }

    public void exitGame() {
        System.out.println("Exit Game");
        System.out.println("START GAME ");
        frame.dispose();
        Menu mainMenuScene = new Menu();
        mainMenuScene.start();
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
