package mazegame.entities;

public class Bomb {
    private int x, y;
    private boolean isVisible;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.isVisible = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void detonate() {
        isVisible = true;
        // Play a bomb explosion sound here
        // ...
    }
}