package rs.dxt.wingcommander;

import java.awt.*;

public class Explosion extends Entity {
    
    private int maxRadius;
    
    /**
     * Class constructor
     * 
     * @param x   The x at which to draw the explosion
     * @param y   The y at which to draw the explosion
     * @param r   The initial radius of the explosion
     * @param max The final radius of the explosion
     */
    public Explosion(double x, double y, int r, int max) {
        this.x = x;
        this.y = y;
        this.r = r;
        maxRadius = max;
    }
    
    /**
     * Update all the explosion logic
     * 
     * @return boolean
     */
    public boolean update() {
        r += 2;
        
        if (r >= maxRadius) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Draw all explosion related stuff
     * 
     * @param g The graphics context
     */
    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 128));
        g.setStroke(new BasicStroke(3));
        g.drawOval((int)x - r, (int)y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }
}
