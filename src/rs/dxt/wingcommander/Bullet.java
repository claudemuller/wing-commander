package rs.dxt.wingcommander;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet extends Entity {

    private double rad;
    private int speed;
    
    private Color color1;
    
    /**
     * Class constructor
     * 
     * @param angle The angle the bullet is moving in
     * @param x     The x at which to draw the bullet
     * @param y     The y at which to draw the bullet
     */
    public Bullet(double angle, int x, int y) {
        this.x = x;
        this.y = y;
        r = 2;
        
        rad = Math.toRadians(angle);
        speed = 10;
        dx = (int)Math.cos(rad) * speed;
        dy = (int)Math.sin(rad) * speed;
        
        color1 = Color.YELLOW;
    }
    
    /**
     * Update all the player logic
     */
    public boolean update() {
        x += dx;
        y += dy;
        
        if (x < -r || x > GamePanel.WIDTH + r ||
                y < -r || y > GamePanel.HEIGHT + r) {
            return true;
        }
        
        return false;
    }

    /**
     * Draw all player related stuff
     */
    public void draw(Graphics2D g) {
        g.setColor(color1);
        g.fillRect(x - r, y - r, r, 2 * r);
    }

}
