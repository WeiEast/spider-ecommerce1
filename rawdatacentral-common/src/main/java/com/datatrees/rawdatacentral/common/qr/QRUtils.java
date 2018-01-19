package com.datatrees.rawdatacentral.common.qr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.StringUtils;

/**
 * 二维码 生产和解析的工具类
 * Created by guimeichao on 18/1/11.
 */
public class QRUtils {

    /**
     * 二维码生成
     * @param url 需要生成二维码的URL
     * @return 二维码的字节数组
     * @exception IOException
     */
    public static byte[] createCode(String url) throws IOException {
        if (StringUtils.isNotEmpty(url)) {
            int width = 200;// 图片的宽度
            int height = 200;// 高度
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix m;
            try {
                m = writer.encode(url, BarcodeFormat.QR_CODE, height, width);
                MatrixToImageWriter.writeToStream(m, "png", stream);
                byte[] bytes = stream.toByteArray();
                return bytes;
            } catch (WriterException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } finally {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            }

        }
        return null;

    }

    /**
     * 二维码解析
     * @param bytes 需要解析的二维码图片字节码
     * @return 二维码的内容
     */
    public static String parseCode(byte[] bytes) throws IOException {
        InputStream input = null;
        try {
            MultiFormatReader formatReader = new MultiFormatReader();
            input = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(input);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            Result result = formatReader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            input.close();
        }
        return null;
    }
}
