package com.datatrees.rawdatacentral.plugin.operator.an_hui_10000_web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfUtils {

    private static Logger log = LoggerFactory.getLogger(PdfUtils.class);

    public static String pdfToHtml(InputStream input) {
        PDDocument document = null;
        try {
            document = PDDocument.load(input);
            CebPDFDomParser pdfParser = new CebPDFDomParser();
            return pdfParser.getText(document);
        } catch (Exception e) {
            log.error("Parser PDF Error: " + e.getMessage(), e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                    log.error("Close document Error: " + e.getMessage(), e);
                }
            }
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/yaojun-2/20160516.pdf");
        String htmlContent = PdfUtils.pdfToHtml(new FileInputStream(file));
        System.out.println(htmlContent);
    }
}
