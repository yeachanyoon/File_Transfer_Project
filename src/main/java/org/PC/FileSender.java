package org.PC;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileSender {
    private ServerSocket serverSocket;

    public void initiateFileTransfer(Component parentComponent) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parentComponent);

        // 파일이 선택 되었을 때만 실행
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // QRCode에 담을 정보 생성
            Data data = new Data();
            
            // QRCode 생성
            BufferedImage qrCodeImage = QRCodeGenerator.createQRCode(data);

            // QRCode를 화면에 표시
            if(qrCodeImage != null) {
                ImageIcon qrIcon = new ImageIcon(qrCodeImage);
                String message = String.format("아래 QR 코드를 수신할 기기에서 스캔하세요");
                
                JOptionPane.showMessageDialog(parentComponent, message, "QR 코드", JOptionPane.INFORMATION_MESSAGE, qrIcon);
            } else {
                JOptionPane.showMessageDialog(parentComponent, "QR 코드 생성에 실패하였습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }


            // 파일 전송 시작
            startSending(data, selectedFile);
        } else {
            System.out.println("파일 선택을 취소하였습니다.");
        }
    }


    /**
     * 파일 전송을 시작하는 메소드로 UI 스레드와 분리되서 실행
     * @param file
     */
    private void startSending(Data data, File file) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(data.getPort());
                System.out.println("서버가 시작되었습니다...");
                System.out.println("수신자의 연결을 기다리고 있습니다...");

                Socket clientSocket = serverSocket.accept();
                System.out.println("연결되었습니다! " + clientSocket.getInetAddress().getHostAddress());

                transferFile(clientSocket, file);
            } catch (IOException e) {
                System.err.println("서버 오류: " + e.getMessage());
            } finally {
                closeServerSocket();
            }
        }).start();
    }

    /**
     * 실제 파일 데이터를 클라이언트 소켓을 통해 전송
     * @param socket 클라이언트와 연결된 소켓
     * @param file 전송할 파일
     */
    private void transferFile(Socket socket, File file) {
        try (OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            FileInputStream fis = new FileInputStream(file)) {
            // 파일 이름 전송
            dos.writeUTF(file.getName());
            // 파일 크기 전송
            dos.writeLong(file.length());
            // 파일 본문 전송 <- 여기다가 암호화 추가 예정 (따로 클래스를 분리시킬꺼임)
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalSent = 0;
            while((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
                totalSent += bytesRead;
                System.out.println("전송 진행중: " + totalSent + "/" + file.length() + "bytes");
            }
            dos.flush();
            System.out.println("파일 전송 완료.");
        } catch (IOException e) {
            System.err.println("파일 전송 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 서버 소켓을 닫는 메소드
     */
    private void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("서버 소켓을 닫았습니다.");
            }
        } catch (IOException e) {
            System.err.println("서버 소켓을 닫는 중 오류가 발생했습니다.: " + e.getMessage());
        }
    }
}
