package rs.dxt.wingcommander;

import java.awt.*;

public class Entity {

    protected double x, y;
    protected int r;
    
    protected double dx, dy;
    protected double speed;

    protected Color colour1;

    /**
     * Get x
     * 
     * @return x
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get y
     * 
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Get r
     * 
     * @return r
     */
    public int getR() {
        return r;
    }

}
