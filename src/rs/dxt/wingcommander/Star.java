package rs.dxt.wingcommander;

import java.awt.Color;
import java.awt.Graphics2D;

public class Star extends Entity {
    
    public Star() {
        r = 1;
        x = (int)(Math.random() * GamePanel.WIDTH);
        y = (int)(Math.random() * GamePanel.HEIGHT);
        speed = 5;
    }
    
    public void update() {
        y += speed;
        
        if (y > GamePanel.HEIGHT) {
            y = -r;
        }
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval((int)x, (int)y, r, r);
    }

}
