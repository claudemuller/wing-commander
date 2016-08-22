package rs.dxt.wingcommander;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
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
    public static ArrayList<PowerUp> powerups;
    public static ArrayList<Text> texts;
    
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;
    
    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownTimerLength = 6000;
    
    public Font font, fontLarge, fontSmall;

    boolean played;
    
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
            URL resource = getClass().getResource("fonts/slkscr.ttf");
            Font ttfFont = Font.createFont(Font.TRUETYPE_FONT, resource.openStream());
            font = ttfFont.deriveFont(18f);
            fontLarge = ttfFont.deriveFont(40f);
            fontSmall = ttfFont.deriveFont(12f);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            font = new Font("Century Gothic", Font.PLAIN, 18);
            fontLarge = new Font("Century Gothic", Font.PLAIN, 40);
            fontSmall = new Font("Century Gothic", Font.PLAIN, 12);
        }

        stars = new ArrayList<Star>();
        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        explosions = new ArrayList<Explosion>();
        powerups = new ArrayList<PowerUp>();
        texts = new ArrayList<Text>();
        
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
        if (menu) {
            played = true;
            if (!played) {
                URL resource = getClass().getResource("sfx/powerup.wav");
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(resource);
                    //AudioFormat format = stream.getFormat();
                    //DataLine.Info info = new DataLine.Info(Clip.class, format);
                    //Clip clip = (Clip)AudioSystem.getLine(info);
                    Clip clip = AudioSystem.getClip();
                    clip.open(stream);
                    clip.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                played = true;
            }
        } else {
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
            
            // Update powerups
            for (int i = 0; i < powerups.size(); i++) {
                boolean remove = powerups.get(i).update();
                if (remove) {
                    powerups.remove(i);
                    i--;
                }
            }
            
            // Update explosions
            for (int i = 0; i < explosions.size(); i++) {
                boolean remove = explosions.get(i).update();
                if (remove) {
                    explosions.remove(i);
                    i--;
                }
            }
            
            // Update texts
            for (int i = 0; i < texts.size(); i++) {
                boolean remove = texts.get(i).update();
                if (remove) {
                    texts.remove(i);
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
                       powerups.add(new PowerUp(1, e.getX(), e.getY()));
                   } else if (rand < 0.01) {
                       powerups.add(new PowerUp(3, e.getX(), e.getY()));
                   } else if (rand < 0.01) {
                       powerups.add(new PowerUp(2, e.getX(), e.getY()));
                   } else if (rand < 0.01) {
                       powerups.add(new PowerUp(4, e.getX(), e.getY()));
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
            double px = player.getX();
            double py = player.getY();
            double pr = player.getR();
                
            if (!player.isRecovering()) {
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
            
            // Player-PowerUp collision
            for (int i = 0; i < powerups.size(); i++) {
                PowerUp p = powerups.get(i);
                double x = p.getX();
                double y = p.getY();
                double r = p.getR();
                
                double dx = px - x;
                double dy = py - y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                // Collected powerup
                if (dist < pr -r) {
                    int type = p.getType();
                    
                    if (type == 1) {
                        player.gainLife();
                        texts.add(new Text(player.getX(), player.getY(), 2000, "Extra Life"));
                    }
                    if (type == 2) {
                        player.increasePower(1);
                        texts.add(new Text(player.getX(), player.getY(), 2000, "Power"));
                    }
                    if (type == 3) {
                        player.increasePower(2);
                        texts.add(new Text(player.getX(), player.getY(), 2000, "Double Power"));
                    }
                    if (type == 4) {
                        slowDownTimer = System.nanoTime();
                        for (int j = 0; j < enemies.size(); j++) {
                            enemies.get(j).setSlow(true);
                        }
                        texts.add(new Text(player.getX(), player.getY(), 2000, "Extra Life"));
                    }
                    
                    powerups.remove(i);
                    i--;
                }
            }

            // Slowdown update
            if (slowDownTimer != 0) {
                slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
                if (slowDownTimerDiff > slowDownTimerLength) {
                    slowDownTimer = 0;
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.get(i).setSlow(false);
                    }
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
        //g.setFont(font);
        //g.setColor(Color.WHITE);
        //g.drawString("FPS: " + averageFPS,  10, 20);
        //g.drawString("# of bullets: " + bullets.size(), 10, 40);

        if (menu) {
            g.setFont(fontLarge);
            g.setColor(Color.WHITE);
            String s = "Wing Commander";
            int length = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2 - 20);

            g.setFont(font);
            g.setColor(Color.YELLOW);
            s = "Press SPACE to play";
            length = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2 + 20);
        } else {
            // Draw the stars
            for (int i = 0; i < stars.size(); i++) {
                stars.get(i).draw(g);
            }
            
            // Draw slowdown screen
            if (slowDownTimer != 0) {
                g.setColor(new Color(255, 255, 255, 64));
                g.fillRect(0, 0, WIDTH, HEIGHT);
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
            
            // Draw powerups
            for (int i = 0; i < powerups.size(); i++) {
                powerups.get(i).draw(g);
            }
            
            // Draw explosions
            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).draw(g);
            }
            
            // Draw texts
            for (int i = 0; i < texts.size(); i++) {
                texts.get(i).draw(g);
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
