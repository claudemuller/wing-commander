package rs.dxt.wingcommander;

import java.awt.*;

public class Text extends Entity {
    
    private long time;
    private String s;
    private long start;
    
    /**
     * Class constructor
     * 
     * @param x    The x at which to draw the text
     * @param y    The y at which to draw the text
     * @param time The number of milliseconds to stay on the screen
     * @param s    The string to display
     */
    public Text(double x, double y, long time, String s) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.s = s;
        start = System.nanoTime();
    }
    
    /**
     * Update all the text logic
     * 
     * @return boolean
     */
    public boolean update() {
        long elapsed = (System.nanoTime() - start) / 1000000;
        if (elapsed > time) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Draw all text related stuff
     * 
     * @param g The graphics context
     */
    public void draw(Graphics2D g) {
        g.setFont(GamePanel.fontSmall);
        long elapsed = (System.nanoTime() - start) / 1000000;
        int alpha = (int)(255 * Math.sin(3.14 * elapsed / time));
        if (alpha > 255) {
            alpha = 255;
        }
        g.setColor(new Color(255, 255, 255, alpha));
        int length = (int)g.getFontMetrics().getStringBounds(s,  g).getWidth();
        g.drawString(s, (int)(x - length / 2), (int)y);
    }

}
