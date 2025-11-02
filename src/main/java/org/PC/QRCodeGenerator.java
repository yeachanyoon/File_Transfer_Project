package org.PC;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class QRCodeGenerator {

    /** Public API --------------------------------------------------- */
    public static boolean createQRCode() {
        
    }

    /** -------------------------------------------------------------- */


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
            stringBuilder.append(String.format("02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
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
