package BeatBox;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList; //массив для флажков
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Bass Drum", "Closed Hi_Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
    "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
    "Open Hi Conga"};
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public static void main(String[] args) {
        new BeatBox().buildGUI();
    }

    public void buildGUI(){
        theFrame = new JFrame("BeatBox mp3");
        theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout(); //диспетчер компановки
        JPanel background = new JPanel(layout); //устанавливаем взамен дефолтного диспт компановки
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS); //панель кнопок, расположение по вертикали

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        start.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        start.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton clear = new JButton("Clear");
        clear.addActionListener(new MyClearListener());
        buttonBox.add(clear);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout gridLayout = new GridLayout(16,16);
        gridLayout.setVgap(1);
        gridLayout.setHgap(2);

        mainPanel = new JPanel(gridLayout);
        background.add(BorderLayout.CENTER, mainPanel);
        //создаем флажки со значением 0 и добавляем их в лист
        // и на панель
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(250,250,350,350);
        //устанавливаем полный размер для цент компонента
        theFrame.pack();
        theFrame.setVisible(true);
    }//close method

    // Метод для получения синтезатора и
    // секвенсора и дорожки
    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }catch (Exception e){e.printStackTrace();}
    }//close method

    public void buildTrackAndStart(){
        //массив из 16 элементов, чтобы хранить значения
        //для каждого инструмента на все 16 тактов
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();
        //цикл для обхода всех инструментов
        for (int i = 0; i < 16; i++) {

            trackList = new int[16];
            //клавиша которая представляет инструмент
            int key = instruments[i];
            //цикл для обхода всех тактов
            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));
                //Установлен ли флажок на этом такте?
                //Если да, то помещаем значение клавиши в текущую
                //ячейку , которая представляет такт
                //иначе присваеваем ячейки 0, то есть инструмент не должен играть
                if (jc.isSelected()){
                    trackList[j] = key;
                }else {
                    trackList[j] = 0;
                }
            }
            //для этого инструмента и для остальных 16 создаем события и добавляем на дорожку
            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }
        //мы всегда должны быть уверены что на 16 такте существует какое либо событие
        //иначе beatbox может не пройти все 16 тактов перед тем как заново начать
        //последовательность
        track.add(makeEvent(182,9,1,0,15));

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }catch (Exception e){e.printStackTrace();}
    }//close method


    public class MyStartListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }
    //метод увеличивает темп на 3 %
    public class MyUpTempoListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor*1.03));
        }
    }
    // метод уменьшает темп на 3 %
    public class MyDownTempoListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor*0.97));
        }
    }
    public class MyClearListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            for (JCheckBox myJC : checkboxList) {
                JCheckBox jc = new JCheckBox();
                jc = myJC;
                jc.setSelected(false);
            }
        }
    }

    /*
    Метод создает события для одного инструмента за каждый проход
    цикла для всех 16 таков. каждый элемент массива list содержит либо
    клавишу этого инструмента либо ноль. Если это ноль инструмент не должен играть на
    текущем такте, иначе нужно создать событие и добавить его в дорожку.
     */
    public void makeTracks(int[] list){
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            if (key != 0){
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i+1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        }catch (Exception e){e.printStackTrace();}
        return  event;
    }
}
