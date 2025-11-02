package org.PC;

public class main {
    private static final int port = 8080;
    private static final String SECRET_KEY_FILE = "test_secret_key";

    public static void main(String[] args) {
        boolean isSuccess = QRCodeGenerator.createQRCode(port, SECRET_KEY_FILE);

        if(isSuccess) System.out.println("Main:: 파일 생성 성공");
        else System.out.println("Main:: 파일 생성 실패");
    }

}
