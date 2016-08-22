package rs.dxt.wingcommander;

import java.awt.*;

public class PowerUp extends Entity {
    
    private int type;

    /**
     * Class constructor
     * 
     * @param type The type of powerup
     * @param x    The x at which to draw the powerup
     * @param y    The y at which to draw the powerup
     */   
    public PowerUp(int type, double x, double y) {
        this.x = x;
        this.y = y;
        this.type = type;
        
        if (type == 1) {
            colour1 = Color.PINK;
            r = 3;
        }
        if (type == 2) {
            colour1 = Color.YELLOW;
            r = 3;
        }
        if (type == 3) {
            colour1 = Color.YELLOW;
            r = 5;
        }
        if (type == 4) {
            colour1 = Color.WHITE;
            r = 3;
        }
    }
    
    /**
     * Get type
     * 
     * @return type
     */
    public int getType() {
        return type;
    }
    
    /**
     * Update all the powerup logic
     * 
     * @return boolean
     */
    public boolean update() {
        y += 2;
        
        if (y > GamePanel.HEIGHT + r) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Draw all powerup related stuff
     * 
     * @param g The graphics context
     */
    public void draw(Graphics2D g) {
        g.setColor(colour1);
        g.fillRect((int)x - r, (int)y - r, 2 * r, 2 * r);

        g.setStroke(new BasicStroke(3));
        g.setColor(colour1.darker());
        g.fillRect((int)x - r, (int)y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }

}
