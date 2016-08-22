package rs.dxt.wingcommander;

import java.awt.Color;
import java.awt.Graphics2D;

public class Star extends Entity {
    
    /**
     * Class constructor
     */
    public Star() {
        r = 1;
        x = (int)(Math.random() * GamePanel.WIDTH);
        y = (int)(Math.random() * GamePanel.HEIGHT);
        speed = 5;
    }
    
    /**
     * Update all the star logic
     */
    public void update() {
        y += speed;
        
        if (y > GamePanel.HEIGHT) {
            y = -r;
        }
    }
    
    /**
     * Draw all the star related stuff
     * 
     * @param g The graphics context
     */
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval((int)x, (int)y, r, r);
    }

}
