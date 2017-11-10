package MusicPlayer;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;

public class MiniMusicPlayer{
    static JFrame frame = new JFrame("Winamp mp3 player");
    static MyDrawPanel panel;

    public static void main(String[] args) {
        MiniMusicPlayer mini = new MiniMusicPlayer();
        mini.go();
    }

    public void setUpGui(){
        panel = new MyDrawPanel();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setBounds(30,30, 300, 300);
        frame.setVisible(true);
    }

    public void go(){
        setUpGui();
        try {
            //создаем синтезатор и открываем его
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // событие которое нас интерисует
            int[] eventsIWant = {127};
            //регистрируем событие синтезатором
            sequencer.addControllerEventListener(panel, eventsIWant);

            //создаем последовательность и дорожку
            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();
            //инструмента
            int r = 0;
            //создаем группу событий чтобы ноты поднимались от  ноты 5 до ноты 60
            for (int i = 5; i < 60 ; i+=4) {
                r = (int)((Math.random()*50) + 1);
                track.add(makeEvent(144,1, r, 100, i));
                //ловим событие между стартом и концом ноты
                track.add(makeEvent(176,1,127,0,i));

                track.add(makeEvent(128,1, r,100,i+2));
            }

            sequencer.setSequence(seq);
            sequencer.setTempoInBPM(120);
            sequencer.start();

        }catch (Exception e){ e.printStackTrace();}


    }


    public MidiEvent makeEvent(int cmd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(cmd, chan, one, two);
            event = new MidiEvent(a, tick);
        }catch (Exception e){}

        return  event;
    }

    class MyDrawPanel extends JPanel implements ControllerEventListener{
        boolean msg = false;

        @Override
        public void controlChange(ShortMessage event) {
            msg = true;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (msg){
                //Graphics2D g2d = (Graphics2D)g;

                int red = (int)(Math.random()*250);
                int green = (int)(Math.random()*250);
                int blue = (int)(Math.random()*250);

                g.setColor(new Color(red,green,blue));

                int ht = (int)((Math.random()*120) + 10);
                int width = (int)((Math.random()*120) + 10);

                int x = (int)((Math.random()*40) + 10);
                int y = (int)((Math.random()*40) + 10);

                g.fillRect(x,y,ht,width);
                msg = false;
            }
        }
    }
}
