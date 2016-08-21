package rs.dxt.wingcommander;

import java.awt.*;

public class Enemy extends Entity {
    
    private double rad;
    
    private int health;
    private int type;
    private int rank;
    
    private boolean ready;
    private boolean dead;
    
    private boolean hit;
    private long hitTimer;
    
    private boolean slow;
    
    public Enemy(int type, int rank) {
        this.type = type;
        this.rank = rank;
        
        // Default enemy
        if (type == 1) {
            colour1 = new Color(0, 0, 255);
            if (rank == 1) {
                speed = 2;
                r = 5;
                health = 1;
            }
            if (rank == 2) {
                speed = 2;
                r = 10;
                health = 2;
            }
            if (rank == 3) {
                speed = 1.5;
                r = 20;
                health = 3;
            }
            if (rank == 4) {
                speed = 1.5;
                r = 30;
                health = 4;
            }
        }

        // Stronger, faster enemy
        if (type == 2) {
            colour1 = new Color(255, 0, 0);
            if (rank == 1) {
                speed = 3;
                r = 5;
                health = 2;
            }
            if (rank == 2) {
                speed = 3;
                r = 10;
                health = 3;
            }
            if (rank == 3) {
                speed = 2.5;
                r = 20;
                health = 3;
            }
            if (rank == 4) {
                speed = 2.5;
                r = 30;
                health = 4;
            }
        }

        // Slow but hard to kill enemy
        if (type == 3) {
            colour1 = new Color(0, 255, 0);
            if (rank == 1) {
                speed = 1.5;
                r = 5;
                health = 3;
            }
            if (rank == 2) {
                speed = 1.5;
                r = 10;
                health = 4;
            }
            if (rank == 3) {
                speed = 1.5;
                r = 25;
                health = 5;
            }
            if (rank == 4) {
                speed = 1.5;
                r = 45;
                health = 5;
            }
        }

        x = Math.random() * 400;
        y = (Math.random() * -100) - r;
        
        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);
        
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;
        
        ready = false;
        dead = false;
        
        hit = false;
        hitTimer = 0;
    }
    
    public boolean isDead() {
        return dead;
    }
    
    public int getType() {
        return type;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setSlow(boolean b) {
        slow = b;
    }
    
    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
        hit = true;
        hitTimer = System.nanoTime();
    }
    
    public void explode() {
        if (rank > 1) {
            int amount = 0;
            if (type == 1) {
                amount = 3;
            }
            if (type == 2) {
                amount = 3;
            }
            if (type == 3) {
                amount = 4;
            }
            
            for (int i = 0; i < amount; i++) {
                Enemy e = new Enemy(getType(), getRank() -1);
                e.setSlow(slow);
                e.x = this.x;
                e.y = this.y;
                double angle = 0;
                if (!ready) {
                    angle = Math.random() * 140 + 20;
                } else {
                    angle = Math.random() * 360;
                }
                e.rad = Math.toRadians(angle);
                
                GamePanel.enemies.add(e);
            }
        }
    }
    
    public void update() {
        if (slow) {
            x += dx * 0.3;
            y += dy * 0.3;
        } else {
            x += dx;
            y += dy;
        }
        
        if (!ready) {
            if (x > r && x < GamePanel.WIDTH - r
                    && y > r && y < GamePanel.HEIGHT - r) {
                ready = true;
            }
        }
        
        if (x < r && dx < 0) {
            dx = -dx;
        }
        if (y < r && dy < 0) {
            dy = -dy;
        }
        if (x > GamePanel.WIDTH - r && dx > 0) {
            dx = -dx;
        }
        if (y > GamePanel.HEIGHT - r && dy > 0) {
            dy = -dy;
        }
        
        if (hit) {
            long elapsed = (System.nanoTime() - hitTimer) / 1000000;
            if (elapsed > 50) {
                hit = false;
                hitTimer = 0;
            }
        }
    }
    
    public void draw(Graphics2D g) {
        if (hit) {
            g.setColor(Color.WHITE);
            g.fillPolygon(new int[]{(int)x - r, (int)x + r, (int)x},
                    new int[]{(int)y - r * 2, (int)y - r * 2, (int)y + r}, 3);
        } else {
            g.setColor(colour1);
            g.fillPolygon(new int[]{(int)x - r, (int)x + r, (int)x},
                    new int[]{(int)y - r * 2, (int)y - r * 2, (int)y + r}, 3);
        }
    }
}

