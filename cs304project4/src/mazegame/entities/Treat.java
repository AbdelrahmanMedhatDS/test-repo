package mazegame.entities;

import javax.media.opengl.GL;
import java.util.Random;
import java.util.Timer;

public class Treat {
    public int x, y;
    private int shape;   // either star = 1 or circle = 0
    public int effect; // 1: Increase time, 2: Decrease opponent's time, 3: Reset opponent

    public Treat(int x, int y) {
        this.x = x;
        this.y = y;

        // Randomly select a color
        Random random = new Random();
        shape = random.nextInt(2); // 0 or 1
        effect = random.nextInt(3) + 1; // 1, 2, or 3
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEffect() {
        return effect;
    }


    public void draw(GL gl) {
        gl.glColor4f(1.0f,0.6f,0.0f,0.8f);// Orange
        drawCircle(gl);
    }

    private void drawCircle(GL gl) {
        int segments = 20;
        float radius = 0.3f;

        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex2f(x + 0.5f, y + 0.5f); // Center point

        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2.0 * Math.PI / segments);
            float cx = x + 0.5f + (float) (Math.cos(angle) * radius);
            float cy = y + 0.5f + (float) (Math.sin(angle) * radius);
            gl.glVertex2f(cx, cy);
        }

        gl.glEnd();
    }



}


