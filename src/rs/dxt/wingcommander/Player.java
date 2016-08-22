package rs.dxt.wingcommander;

import java.awt.*;

public class Player extends Entity {

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    
    private boolean firing;
    private long firingTimer;
    private long firingDelay;
    
    private boolean recovering;
    private long recoveryTimer;
    
    private int score;
    
    private int lives;
    private Color colour2;
    
    private int powerLevel;
    private int power;
    private int[] requiredPower = {
            1, 2, 3, 4, 5
    };
    
    /**
     * Class constructor
     */
    public Player() {
        r = 5;
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT - r * 2;
        
        dx = 0;
        dy = 0;
        speed = 5;
        
        lives = 3;
        colour1 = Color.WHITE;
        colour2 = Color.RED;
        
        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;
        
        recovering = false;
        recoveryTimer = 0;
        
        score = 0;
    }

    public int getLives() {
        return lives;
    }

    /**
     * Get score
     * 
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Get whether dead
     * 
     * @return boolean
     */
    public boolean isDead() {
        return lives <= 0;
    }

    /**
     * Get recovering
     * 
     * @return recovering
     */
    public boolean isRecovering() {
        return recovering;
    }
    
    /**
     * get powerLevel
     * 
     * @return powerLevel
     */
    public int getPowerLevel() {
        return powerLevel;
    }
    
    /**
     * Get power 
     * 
     * @return power
     */
    public int getPower() {
        return power;
    }
    
    /**
     * Get requiredPower
     * 
     * @return requiredPower
     */
    public int getRequiredPower() {
        return requiredPower[powerLevel];
    }
    
    /**
     * Set left
     * 
     * @param b
     */
    public void setLeft(boolean b) {
        left = b;
    }

    /**
     * Set right
     * 
     * @param b
     */
    public void setRight(boolean b) {
        right = b;
    }

    /**
     * Set up
     * 
     * @param b
     */
    public void setUp(boolean b) {
        up = b;
    }

    /**
     * Set down
     * 
     * @param b
     */
    public void setDown(boolean b) {
        down = b;
    }
    
    /**
     * Set firing
     * 
     * @param b
     */
    public void setFiring(boolean b) {
        firing = b;
    }
    
    /**
     * Update the score
     * 
     * @param score The addition to the current score
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * Subtract a life
     */
    public void loseLife() {
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }
    
    /**
     * Add one to lives
     */
    public void gainLife() {
        lives++;
    }
    
    /**
     * Increase the power by i
     * 
     * @param i
     */
    public void increasePower(int i) {
        power += i;
        if (powerLevel == 4) {
            if (power > requiredPower[powerLevel]) {
                power = requiredPower[powerLevel];
            }
            return;
        }
        if (power >= requiredPower[powerLevel]) {
            power -= requiredPower[powerLevel];
            powerLevel++;
        }
    }

    /**
     * Update all the player logic
     */
    public void update() {
        if (left) {
            dx = -speed;
        }
        if (right) {
            dx = speed;
        }
        if (up) {
            dy = -speed;
        }
        if (down) {
            dy = speed;
        }
        
        x += dx;
        y += dy;
        
        if (x < r) {
            x = r;
        }
        if (x > GamePanel.WIDTH - r) {
            x = GamePanel.WIDTH - r;
        }
        if (y < r) {
            y = r;
        }
        if (y > GamePanel.HEIGHT - r) {
            y = GamePanel.HEIGHT - r;
        }
        
        dx = 0;
        dy = 0;
        
        // Limit firing and different guns
        if (firing) {
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;
            if (elapsed > firingDelay) {
                firingTimer = System.nanoTime();

                if (powerLevel < 2) {
                    GamePanel.bullets.add(new Bullet(270, x, y));
                } else if (powerLevel < 4) {
                    GamePanel.bullets.add(new Bullet(270, x + 5, y));
                    GamePanel.bullets.add(new Bullet(270, x - 5, y));
                } else {
                    GamePanel.bullets.add(new Bullet(270, x, y));
                    GamePanel.bullets.add(new Bullet(275, x + 5, y));
                    GamePanel.bullets.add(new Bullet(265, x - 5, y));
                }
            }
        }
        
        // If recovery time up, reset recovery timer
        if (recovering) {
            long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
            if (elapsed > 2000) {
                recovering = false;
                recoveryTimer = 0;
            }
        }
    }
    
    /**
     * Draw all player related stuff
     * 
     * @param g The graphics context
     */
    public void draw(Graphics2D g) {
        if (recovering) {
            g.setColor(colour2);
            g.fillPolygon(new int[]{(int)x, (int)x + r, (int)x - r},
                    new int[]{(int)y - r * 2, (int)y + r, (int)y + r}, 3);
        } else {
            g.setColor(colour1);
            g.fillPolygon(new int[]{(int)x, (int)x + r, (int)x - r},
                    new int[]{(int)y - r * 2, (int)y + r, (int)y + r}, 3);
        }
    }
}
