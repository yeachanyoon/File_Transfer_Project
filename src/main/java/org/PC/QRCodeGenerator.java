package org.PC;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {
    /** Public API --------------------------------------------------- */
    public static boolean createQRCode(Data data) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(data);

            System.out.println(jsonString);

            generatorQRCodeImage(jsonString, "C:\\Users\\jeche\\OneDrive\\Documents\\QR.png");

            return true;

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

}
