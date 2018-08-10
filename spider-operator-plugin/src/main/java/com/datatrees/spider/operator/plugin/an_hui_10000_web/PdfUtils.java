package com.datatrees.spider.operator.plugin.an_hui_10000_web;

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

}
