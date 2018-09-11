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

package com.datatrees.spider.bank.plugin.util;

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
