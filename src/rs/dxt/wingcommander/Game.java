package rs.dxt.wingcommander;

import javax.swing.JFrame;

public class Game {

    /**
     * Program entry point
     * 
     * @param args The program arguments
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Wing Commander");
        window.setContentPane(new GamePanel());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

}
