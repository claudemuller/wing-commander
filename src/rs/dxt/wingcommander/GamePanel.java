package rs.dxt.wingcommander;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    
    public static int WIDTH = 400;
    public static int HEIGHT = 400;
    
    private Thread thread;
    private boolean running;
    
    private BufferedImage image;
    private Graphics2D g;
    
    private int FPS = 30;
    private double averageFPS;
    
    public static Player player;
    public static ArrayList<Bullet> bullets;
    
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
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D)image.getGraphics();
        // Switch on anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        player = new Player();
        bullets = new ArrayList<Bullet>();
        
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
        player.update();
        
        // Update bullets
        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }
    }
    
    /**
     * Where all the game assets are rendered
     */
    public void gameRender() {
        // Draw background
        g.setColor(new Color(0, 100, 255));
        g.fillRect(0,  0,  WIDTH,  HEIGHT);
        
        // Debug
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + averageFPS,  10, 20);
        g.drawString("# of bullets: " + bullets.size(), 10, 40);

        // Draw the player
        player.draw(g);
        
        // Draw the bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
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
            player.setFiring(true);
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
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(false);
        }
    }
    
}
