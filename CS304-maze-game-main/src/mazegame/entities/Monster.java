package mazegame.entities;

import utilities.texture.TextureReader;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;
import java.io.IOException;
import java.util.Random;

import static utilities.texture.TextureReader.readTexture;

public class Monster {
    private int x, y; // Monster position in the MAZE
    private Random random;

        // static attributes for the textures
    private static final String[] textureNames = {"back1.png"};
    private static final int textureLen = textureNames.length;
    private static int[] textureID = new int[textureLen];
    private static TextureReader.Texture[] textures = new TextureReader.Texture[textureLen];

    public Monster(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.random = new Random();

        GLAutoDrawable drawable = new GLCanvas();
        GL gl = drawable.getGL();
        loadTextures(gl);
    }

    public void moveSmartly(int[][] maze, int player1X, int player1Y, int player2X, int player2Y) {
        int targetX = player1X;
        int targetY = player1Y;

        // تحديد أقرب لاعب
        int distanceToPlayer1 = Math.abs(player1X - x) + Math.abs(player1Y - y);
        int distanceToPlayer2 = Math.abs(player2X - x) + Math.abs(player2Y - y);
        if (distanceToPlayer2 < distanceToPlayer1) {
            targetX = player2X;
            targetY = player2Y;
        }

        // حركة بسيطة (تفضيل الاتجاه نحو اللاعب)
        if (x < targetX && maze[y][x + 1] != 1) x++;
        else if (x > targetX && maze[y][x - 1] != 1) x--;
        else if (y < targetY && maze[y + 1][x] != 1) y++;
        else if (y > targetY && maze[y - 1][x] != 1) y--;

        // حركة عشوائية إذا لم يتمكن من التحرك نحو اللاعب
        int direction = random.nextInt(4);
        if (direction == 0 && maze[y][x + 1] != 1) x++; // يمين
        if (direction == 1 && maze[y][x - 1] != 1) x--; // يسار
        if (direction == 2 && maze[y + 1][x] != 1) y++; // تحت
        if (direction == 3 && maze[y - 1][x] != 1) y--; // فوق
    }

//    public void drawMonster(GL gl) {
//        gl.glColor3f(1.0f, 0.0f, 0.0f); // اللون الأحمر مؤقتًا (لاحقًا سنستبدله بالنسيج)
//        gl.glBegin(GL.GL_QUADS);
//        gl.glVertex2f(x, y);
//        gl.glVertex2f(x + 1, y);
//        gl.glVertex2f(x + 1, y + 1);
//        gl.glVertex2f(x, y + 1);
//        gl.glEnd();
//    }

    public void drawMonster(GL gl) {
        //nextInt(bound n) generates a random integer from 0 (inclusive) to the specified bound (exclusive)
        // Adding 1 to n ensures you can get the number n itself
        int randomMonster = random.nextInt(textureLen);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[randomMonster]);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(x, y);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(x + 1, y);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(x + 1, y + 1);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(x, y + 1);
        gl.glEnd();
        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    public static void loadTextures(GL gl) { //static => load all monsters only once
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
                System.out.println("Error loading texture: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

