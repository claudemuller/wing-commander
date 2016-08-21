package rs.dxt.wingcommander;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    
    public static int WIDTH = 600;
    public static int HEIGHT = 800;
    
    private Thread thread;
    private boolean running, menu;
    
    private BufferedImage image;
    private Graphics2D g;
    
    private int FPS = 30;
    private double averageFPS;
    
    public static ArrayList<Star> stars;
    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<Explosion> explosions;
    
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;
    
    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownTimerLength = 6000;
    
    private Font font, fontLarge;
    
    /**
     * Class constructor
     */
    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }
    
    public void addNotify() {
        super.addNotify();
        
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        
        addKeyListener(this);
    }
    
    /**
     * Run method for the Runnable implementation
     */
    public void run() {
        running = true;
        menu = true;
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D)image.getGraphics();
        // Switch on anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        try {
            InputStream is = new FileInputStream("fonts/slkscr.ttf");
            Font ttfFont = Font.createFont(Font.TRUETYPE_FONT, is);
            font = ttfFont.deriveFont(18f);
            fontLarge = ttfFont.deriveFont(40f);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            font = new Font("Century Gothic", Font.PLAIN, 18);
        }

        stars = new ArrayList<Star>();
        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        explosions = new ArrayList<Explosion>();
        
        // Create stars
        for (int i = 0; i < 100; i++) {
            stars.add(new Star());
        }

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
        
        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        int maxFrameCount = 30;
        long targetTime = 1000 / FPS;
        
        // Game loop
        while (running) {
            startTime = System.nanoTime();
            
            // Update game logic
            gameUpdate();
            // Draw everything to the screen
            gameRender();
            // Swap the buffers
            gameDraw();
            
            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            
            // Frame rate limiter
            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }
            
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
        
        // Game over
    }
    
    /**
     * Where all the game logic get's updated
     */
    public void gameUpdate() {
        // New Wave
        if (waveStartTimer == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = true;
            waveStartTimer = System.nanoTime();
        } else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if (waveStartTimerDiff > waveDelay) {
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }
        
        // Create enemies
        if (waveStart && enemies.size() == 0) {
            createNewEnemies();
        }

        // Update stars
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).update();
        }

        // Update player
        player.update();
        
        // Update bullets
        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }
        
        // Update enemies
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
        }
        
        // Update explosions
        for (int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if (remove) {
                explosions.remove(i);
                i--;
            }
        }
        
        // Bullet-Enemy collision
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            double bx = b.getX();
            double by = b.getY();
            double br = b.getR();
            
            for (int j = 0; j < enemies.size(); j++) {
                Enemy e = enemies.get(j);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();
                
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                // Collision if dist less than both radii added together
                if (dist < br + er) {
                    e.hit();
                    bullets.remove(b);
                    if (i > 0) {
                        i--;
                    }
                }
            }
        }
        
        // Check for dead enemies
        for (int i = 0; i < enemies.size(); i++) {
           if (enemies.get(i).isDead()) {
               Enemy e = enemies.get(i);
               
               // Chance for powerup
               double rand = Math.random();
               if (rand < 0.01) {

               } else if (rand < 0.01) {

               } else if (rand < 0.01) {

               } else if (rand < 0.01) {

               }
               
               player.addScore(e.getType() + e.getRank());
               
               enemies.remove(i);
               i--;
               
               e.explode();
               explosions.add(new Explosion(e.getX(), e.getY(), e.getR(),
                       e.getR() + 30));
           }
        }
        
        // Check if the player is dead
        if (player.isDead()) {
            running = false;
        }
        
        // Player-Enemy collision
        if (!player.isRecovering()) {
            double px = player.getX();
            double py = player.getY();
            double pr = player.getR();
            
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();
                
                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                if (dist < pr - er) {
                    player.loseLife();
                }
            }
        }
    }
    
    /**
     * Where all the game assets are rendered
     */
    public void gameRender() {
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0,  0,  WIDTH,  HEIGHT);
        
        // Debug
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + averageFPS,  10, 20);
        g.drawString("# of bullets: " + bullets.size(), 10, 40);

        if (menu) {
            g.setFont(fontLarge);
            g.setColor(Color.WHITE);
            String s = "Wing Commander";
            int length = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2 - 20);

            g.setFont(font);
            g.setColor(Color.BLUE);
            s = "Press SPACE to play";
            length = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2 + 20);
        } else {
            // Draw the stars
            for (int i = 0; i < stars.size(); i++) {
                stars.get(i).draw(g);
            }

            // Draw the player
            player.draw(g);
            
            // Draw the bullets
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).draw(g);
            }
            
            // Draw enemies
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).draw(g);
            }
            
            // Draw explosions
            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).draw(g);
            }
            
            // Draw wave number
            if (waveStartTimer != 0) {
                g.setFont(font);
                String s = "-  W A V E  " + waveNumber + "  -";
                int length = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
                int alpha = (int)(255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
                if (alpha > 255) {
                    alpha = 255;
                }
                g.setColor(new Color(255, 255, 255, alpha));
                g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
            }
        }
    }
    
    /**
     * Where the buffers are swapped
     */
    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
    
    private void createNewEnemies() {
        enemies.clear();
        
        if (waveNumber == 1) {
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }
        if (waveNumber == 2) {
            for (int i = 0; i < 10; i++) {
                enemies.add(new Enemy(1, 1));
            }
            enemies.add(new Enemy(1, 2));
        }
        if (waveNumber == 3) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
            enemies.add(new Enemy(1, 2));
            enemies.add(new Enemy(1, 2));
        }
        if (waveNumber == 4) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 4));
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(2, 1));
            }
        }
        if (waveNumber == 5) {
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
        }
        if (waveNumber == 6) {
            enemies.add(new Enemy(1, 3));
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(2, 1));
                enemies.add(new Enemy(3, 1));
            }
        }
        if (waveNumber == 7) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
            enemies.add(new Enemy(3, 3));
        }
        if (waveNumber == 8) {
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(2, 4));
            enemies.add(new Enemy(3, 4));
        }
        if (waveNumber == 9) {
            running = false;
        }
    }
    
    public void keyTyped(KeyEvent key) {}
    
    /**
     * When a key is pressed
     * 
     * @param key The KeyEvent that occurred
     */
    public void keyPressed(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(true);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(true);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(true);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(true);
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            if (menu) {
                menu = false;
            } else {
                player.setFiring(true);
            }
        }
        if (keyCode == KeyEvent.VK_Q) {
            menu = true;
        }
    }
    
    /**
     * When a key is released
     * 
     * @param key The KeyEvent that occurred
     */
    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(false);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(false);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(false);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(false);
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            player.setFiring(false);
        }
    }
    
}
