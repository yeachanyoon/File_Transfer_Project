package org.PC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class main extends JFrame {

    public main() {
        setTitle("파일 전송 프로그램");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 파일 전송하기 버튼
        JButton sendButton = new JButton("파일 전송하기");
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "파일 전송하기 버튼 클릭");
                // 여기에 파일 전송 로직을 추가할 예정
            }
        });
        panel.add(sendButton);

        // 파일 전송받기 버튼
        JButton receiveButton = new JButton("파일 전송받기");
        receiveButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        receiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "파일 전송받기 버튼 클릭");
                // 여기에 파일 전송받기 로직을 추가할 예정
            }
        });
        panel.add(receiveButton);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new main().setVisible(true);
            }
        });
    }
}