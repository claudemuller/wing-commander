package rs.dxt.wingcommander;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet extends Entity {

    private double rad;
    
    /**
     * Class constructor
     * 
     * @param angle The angle the bullet is moving in
     * @param x     The x at which to draw the bullet
     * @param y     The y at which to draw the bullet
     */
    public Bullet(double angle, double x, double y) {
        this.x = x;
        this.y = y;
        r = 2;
        
        rad = Math.toRadians(angle);
        speed = 10;
        dy = Math.sin(rad) * speed;
        
        colour1 = Color.YELLOW;
    }
    
    /**
     * Update all the player logic
     */
    public boolean update() {
        y += dy;

        if (y < -r || y > GamePanel.HEIGHT + r) {
            return true;
        }
        
        return false;
    }

    /**
     * Draw all player related stuff
     */
    public void draw(Graphics2D g) {
        g.setColor(colour1);
        g.fillRect((int)(x - r), (int)(y - r), r, 2 * r);
    }

}
