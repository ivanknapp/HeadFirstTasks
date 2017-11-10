package SimpleAnimate;

import javax.swing.*;
import java.awt.*;

public class Animate {
    int x = 1;
    int y = 1;

    public static void main(String[] args) {
        Animate gui = new Animate();
        gui.go();
    }

    public void go(){
        JFrame frame = new JFrame("Анимашка");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MyDrawP drawP = new MyDrawP();
        frame.getContentPane().add(drawP);
        frame.setSize(500,250);
        frame.setVisible(true);

        for (int i = 0; i < 124; i++, x++,y++) {
            x++;
            drawP.repaint();
            try {
                Thread.sleep(50);
            }catch (Exception e){

            }
        }



    }

    class MyDrawP extends JPanel{
        public void paintComponent(Graphics g){
            g.setColor(Color.WHITE);
            g.fillRect(0,0,500,250);

            int red = (int)(Math.random()*250);
            int green = (int)(Math.random()*250);
            int blue = (int)(Math.random()*250);

            Color randomColor = new Color(red, green, blue);

            g.setColor(randomColor);
            g.fillRect(x,y,500-x*2, 250-y*2);
        }
    }
}
