package org.PC;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Enumeration;

public class QRCodeGenerator {
    /** Public API --------------------------------------------------- */
    public static boolean createQRCode(int port, String secretKey) {
        try {
            NetworkInfo info = getActiveNetworkInfo();
            if(info == null) { System.err.println("QRCodeGenerator Error: 활성화된 네트워크 어댑터를 찾을 수 없습니다."); return false; }

            Data data = new Data(info, port, secretKey);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(data);

            System.out.println(jsonString);

            generatorQRCodeImage(jsonString, "C:\\Users\\jeche\\OneDrive\\Documents\\QR.png");

            return true;

        } catch (SocketException e) {
            System.err.println("네트워크 오류"); return false;
        } catch (WriterException e) {
            System.err.println("QR코드 생성 오류"); return false;
        } catch (IOException e) {
            System.err.println("파일 저장 오류"); return false;
        }
    }

    /** -------------------------------------------------------------- */



    private static void generatorQRCodeImage(String text, String filePath) throws WriterException, IOException{
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * 
     * 
     * @throws SocketException
     */
    private static NetworkInfo getActiveNetworkInfo() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            //Filter
            if(!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.getHardwareAddress() == null) continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                //실제 주소 (IPv4)가 할당된 인터페이스인지 확인
                if(address instanceof java.net.Inet4Address && !address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                    String ip = address.getHostAddress();
                    String mac = formatMacAddress(networkInterface.getHardwareAddress());
                    return new NetworkInfo(mac, ip);
                }
            }
        }
        return null;
    }
    
    /**
     * MAC 주소 byte 배열을 "XX-XX-XX-XX-XX-XX" 형식의 문자열로 반환합니다.
     * @param mac
     */
    private static String formatMacAddress(byte[] mac) {
        if (mac == null || mac.length == 0) return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return stringBuilder.toString();
    }

    /** Json Data Struct */
    private static class Data {
        private String macAddress;
        private String ip;
        private Integer port;
        private String secretKey;

        Data(NetworkInfo networkInfo, Integer port, String secretKey) {
            this.macAddress = networkInfo.macAddress;
            this.ip = networkInfo.ip;
            this.port = port;
            this.secretKey = secretKey;
        }
    }

    /** Network Info Struct */
    private static class NetworkInfo {
        String macAddress;
        String ip;

        NetworkInfo(String macAddress, String ip) {
            this.macAddress = macAddress;
            this.ip = ip;
        }
    }

}
