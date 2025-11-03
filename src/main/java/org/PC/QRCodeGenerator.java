package org.PC;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;

import java.nio.file.FileSystems;

public class QRCodeGenerator {

    /**
     * 주어진 Data를 이용해 QRCode 만드는 메세지
     * @param data
     * @return BufferedImage (QRCode)
     */
    public static BufferedImage createQRCode(Data data) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(data);

            System.out.println(jsonString);

            return generatorQRCodeImage(jsonString);

        } catch (WriterException e) {
            System.err.println("QR코드 생성 오류"); return null;
        }
    }


    /**
     * 문자열을 BufferedImage형식의 QR 이미지로 만듭니다
     * @param String text
     * @return BufferedImage (QRCode)
    */
    private static BufferedImage generatorQRCodeImage(String text) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

}
