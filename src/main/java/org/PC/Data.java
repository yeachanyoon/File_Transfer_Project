package org.PC;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Data {
    private String macAddress;
    private String ip;
    private Integer port;
    private String secretKey;

    Data() {
        try {
            NetworkInfo info = getActiveNetworkInfo();
            if(info == null) { System.err.println("NetworkInfo Error: 활성화된 네트워크 어댑터를 찾을 수 없습니다."); }

            String secretKey = generateSecretKey();
            if(secretKey == null) {System.err.println("SecretKey Error: 비밀 키 생성에 실패하였습니다."); }

            this.macAddress = info.macAddress;
            this.ip = info.ip;
            this.port = 8080;
            this.secretKey = secretKey;
        } catch (SocketException e) {
            System.err.println("네트워크 오류 발생 (getActiveNetworkInfo 함수)");
        }
    }

    /** getter 부분 */
    public String getMacAddress() { return this.macAddress; }
    public String getIp() { return this.ip; }
    public Integer getPort() { return this.port; }
    public String getSecretKey() { return this.secretKey; }

    /**
     * AES-256 암호화에 사용할 안전한 비밀 키를 생성하고 Base64 문자열로 인코딩합니다.
     * @return Base64로 인코딩된 SecretKey(String)
     */
    private static String generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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

    public static class NetworkInfo {
        String macAddress;
        String ip;

        NetworkInfo(String macAddress, String ip) {
            this.macAddress = macAddress;
            this.ip = ip;
        }
    }
}
