/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码 生产和解析的工具类
 * Created by guimeichao on 18/1/11.
 */
public class QRUtils {

    /**
     * 图片的宽度
     */
    private static final int IMG_WIDTH = 200;
    /**
     * 图片的高度
     */
    private static final int IMG_HEIGHT = 200;

    /**
     * 二维码生成
     * @param url 需要生成二维码的URL
     * @return 二维码的字节数组
     */
    public byte[] createCode(String url) throws IOException, WriterException {
        if (StringUtils.isNotEmpty(url)) {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix m = writer.encode(url, BarcodeFormat.QR_CODE, IMG_HEIGHT, IMG_WIDTH);
                MatrixToImageWriter.writeToStream(m, "png", stream);

                stream.flush();
                return stream.toByteArray();
            }
        }
        return null;

    }

    /**
     * 二维码解析
     * @param bytes 需要解析的二维码图片字节码
     * @return 二维码的内容
     */
    public String parseCode(byte[] bytes) throws IOException, NotFoundException {
        try (InputStream input = new ByteArrayInputStream(bytes)) {
            BufferedImage image = ImageIO.read(input);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<>(1);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            MultiFormatReader formatReader = new MultiFormatReader();
            Result result = formatReader.decode(binaryBitmap, hints);
            return result.getText();
        }
    }
}
