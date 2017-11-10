package SimpleGuiJText;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextArea implements ActionListener{
    JTextArea text;

    public static void main(String[] args) {
        TextArea gui = new TextArea();
        gui.go();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        text.append("Лёха будет рабоать " + ((int)(Math.random()*8)) + " часов \n");
    }

    public void go(){
        JFrame frame = new JFrame("Work day");
        JPanel panel = new JPanel();
        JButton button = new JButton("Lexa go work!");
        button.addActionListener(this);
        text = new JTextArea(10,20);
        text.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.SOUTH, button);

        frame.setSize(350,300);
        frame.setVisible(true);

    }
}
