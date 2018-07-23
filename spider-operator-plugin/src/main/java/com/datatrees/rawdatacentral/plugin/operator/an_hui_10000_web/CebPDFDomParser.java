package com.datatrees.rawdatacentral.plugin.operator.an_hui_10000_web;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.plugin.pdf.AbstractPDFDomParser;
import com.datatrees.crawler.plugin.pdf.pdfdom.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class CebPDFDomParser extends AbstractPDFDomParser {

    private static Logger      log                       = LoggerFactory.getLogger(CebPDFDomParser.class);

    /** Default style placed in the begining of the resulting document */
    protected      String      defaultStyle              = ".page{position:relative; border:1px solid blue;margin:0.5em}\n" +
            ".p,.r{position:absolute;}\n" +
            // disable text-shadow fallback for text stroke if stroke supported by browser
            "@supports(-webkit-text-stroke: 1px black) {" + ".p{text-shadow:none !important;}" + "}";

    /** The resulting document representing the PDF file. */
    protected      Document    doc;

    /** The head element of the resulting document. */
    protected      Element     head;

    /** The body element of the resulting document. */
    protected      Element     body;

    /** The title element of the resulting document. */
    protected      Element     title;

    /** The global style element of the resulting document. */
    protected      Element     globalStyle;

    /** The element representing the page currently being created in the resulting document. */
    protected      Element     curpage;

    /** Text element counter for assigning IDs to the text elements. */
    protected      int         textcnt;

    /** Page counter for assigning IDs to the pages. */
    protected      int         pagecnt;

    protected      int         tradecnt;

    protected      boolean     isTradeStarted;

    private        Node        lastNode;

    private        TextMetrics lastTextMetrics;

    private        String      lastData;

    private        boolean     processingTransDetails;

    private        String      transactionDetailsPattern = PropertiesConfiguration.getInstance().get("cebbank.transaction.details.pattern", "交易日");

    private        int         lineMaxPtSpace            = PropertiesConfiguration.getInstance().getInt("cebbank.max.pt.space", 1);

    private        String      tradeStartPattern         = PropertiesConfiguration.getInstance()
            .get("cebbank.trade.start.pattern", "\\d{4}/\\d{2}/\\d{2}");

    private        String      tradeEndPattern           = PropertiesConfiguration.getInstance()
            .get("cebbank.trade.end.pattern", "本期最低还款额|本期欠款|本期存款|共[\\s\\d]+页|^第|^￥\\d*|本期应还款额");

    private        String      megreSplite               = PropertiesConfiguration.getInstance().get("cebbank.megre.splite.pattern", " ");

    private        Element     currentTrade;

    /**
     * Creates a new PDF DOM parser.
     * @exception IOException
     * @exception ParserConfigurationException
     */
    public CebPDFDomParser() throws IOException, ParserConfigurationException {
        super();
        init();
    }

    /**
     * Internal initialization.
     * @exception ParserConfigurationException
     */
    private void init() throws ParserConfigurationException {
        pagecnt = 0;
        textcnt = 0;
        tradecnt = 0;
        disableImages = true;
        disableImageData = true;
        disableGraphics = true;
    }

    /**
     * Creates a new empty HTML document tree.
     * @exception ParserConfigurationException
     */
    protected void createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        DocumentType doctype = builder.getDOMImplementation()
                .createDocumentType("html", "-//W3C//DTD XHTML 1.1//EN", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
        doc = builder.getDOMImplementation().createDocument("http://www.w3.org/1999/xhtml", "html", doctype);
        head = doc.createElement("head");
        Element meta = doc.createElement("meta");
        meta.setAttribute("http-equiv", "content-type");
        meta.setAttribute("content", "text/html;charset=utf-8");
        head.appendChild(meta);
        title = doc.createElement("title");
        title.setTextContent("PDF Document");
        head.appendChild(title);
        globalStyle = doc.createElement("style");
        globalStyle.setAttribute("type", "text/css");
        // globalStyle.setTextContent(createGlobalStyle());
        head.appendChild(globalStyle);
        body = doc.createElement("body");
        Element root = doc.getDocumentElement();
        root.appendChild(head);
        root.appendChild(body);
    }

    /**
     * Obtains the resulting document tree.
     * @return The DOM root element.
     */
    public Document getDocument() {
        return doc;
    }

    @Override
    public void startDocument(PDDocument document) throws IOException {
        try {
            createDocument();
        } catch (ParserConfigurationException e) {
            throw new IOException("Error: parser configuration error", e);
        }
    }

    @Override
    protected void endDocument(PDDocument document) throws IOException {
        // use the PDF title
        String doctitle = document.getDocumentInformation().getTitle();
        if (doctitle != null && doctitle.trim().length() > 0) title.setTextContent(doctitle);
        // set the main style
        globalStyle.setTextContent(createGlobalStyle());
    }

    /**
     * Parses a PDF document and serializes the resulting DOM tree to an output. This requires a DOM
     * Level 3 capable implementation to be available.
     */
    @Override
    public void writeText(PDDocument doc, Writer outputStream) throws IOException {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            LSOutput output = impl.createLSOutput();
            writer.getDomConfig().setParameter("format-pretty-print", true);
            output.setCharacterStream(outputStream);
            createDOM(doc);
            writer.write(getDocument(), output);
        } catch (ClassCastException e) {
            throw new IOException("Error: cannot initialize the DOM serializer", e);
        } catch (ClassNotFoundException e) {
            throw new IOException("Error: cannot initialize the DOM serializer", e);
        } catch (InstantiationException e) {
            throw new IOException("Error: cannot initialize the DOM serializer", e);
        } catch (IllegalAccessException e) {
            throw new IOException("Error: cannot initialize the DOM serializer", e);
        }
    }

    /**
     * Loads a PDF document and creates a DOM tree from it.
     * @param doc the source document
     * @return a DOM Document representing the DOM tree
     * @exception IOException
     */
    public Document createDOM(PDDocument doc) throws IOException {
        /*
         * We call the original PDFTextStripper.writeText but nothing should be printed actually
         * because our processing methods produce no output. They create the DOM structures instead
         */
        super.writeText(doc, new OutputStreamWriter(System.out));
        return this.doc;
    }

    // ===========================================================================================

    @Override
    protected void startNewPage() {
        curpage = createPageElement();
        body.appendChild(curpage);
        lastNode = null;
        lastData = null;
        lastTextMetrics = null;
    }

    @Override
    protected void renderText(String data, TextMetrics metrics) {
        Element element = null;
        processingTransDetails = (data != null && PatternUtils.match(transactionDetailsPattern, data)) ? true : processingTransDetails;
        if (processingTransDetails) {
            if (isTradeStarted && isTradeEnd(data)) {
                if (lastNode != null && isNeedMerge(metrics)) {
                    currentTrade.removeChild(lastNode);
                    data = lastData + megreSplite + data;
                }
                isTradeStarted = false;
            }
            if (!isTradeStarted && isTradeStart(data)) {
                currentTrade = createTransElement();
                curpage.appendChild(currentTrade);
                isTradeStarted = true;
            }
            element = createTextElement(data, metrics.getWidth());
            if (isTradeStarted) {
                currentTrade.appendChild(element);
            } else {
                curpage.appendChild(element);
            }
            lastData = data;
        } else {
            if (lastNode != null && isNeedMerge(metrics)) {
                element = createTextElement(lastData + megreSplite + data, metrics.getWidth());
                curpage.replaceChild(element, lastNode);
                lastData = lastData + megreSplite + data;
            } else {
                element = createTextElement(data, metrics.getWidth());
                curpage.appendChild(element);
                lastData = data;
            }
        }
        lastNode = element;
        lastTextMetrics = metrics;
    }

    private boolean isTradeStart(String data) {
        if (PatternUtils.match(tradeStartPattern, data) && !PatternUtils.match(tradeStartPattern, lastData)) {
            return true;
        }
        return false;
    }

    private boolean isTradeEnd(String data) {
        if (PatternUtils.match(tradeStartPattern, data) && !PatternUtils.match(tradeStartPattern, lastData)) {
            return true;
        }
        if (PatternUtils.match(tradeEndPattern, data)) {
            processingTransDetails = false;
            return true;
        }
        return false;
    }

    private boolean isNeedMerge(TextMetrics metrics) {
        if (lastTextMetrics != null && (lastTextMetrics.getTop() - metrics.getTop()) <= lineMaxPtSpace &&
                (metrics.getTop() - lastTextMetrics.getTop()) <= lineMaxPtSpace &&
                (lastTextMetrics.getHeight() - metrics.getHeight()) <= lineMaxPtSpace &&
                (metrics.getHeight() - lastTextMetrics.getHeight()) <= lineMaxPtSpace) return true;
        return false;
    }

    protected Element createTransElement() {
        String pstyle = "";
        PDRectangle layout = getCurrentMediaBox();
        if (layout != null) {
            float w = layout.getWidth();
            float h = layout.getHeight();
            final int rot = pdpage.getRotation();
            if (rot == 90 || rot == 270) {
                float x = w;
                w = h;
                h = x;
            }
            pstyle = "width:" + w + UNIT + ";" + "height:" + h + UNIT + ";";
            pstyle += "overflow:hidden;";
        } else log.warn("No media box found");
        Element el = doc.createElement("div");
        el.setAttribute("id", "tradeDetail" + (tradecnt++));
        el.setAttribute("class", "TradeDetail");
        el.setAttribute("style", pstyle);
        return el;
    }

    @Override
    protected void renderPath(List<PathSegment> path, boolean stroke, boolean fill) throws IOException {
        float[] rect = toRectangle(path);
        if (rect != null) {
            curpage.appendChild(createRectangleElement(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1], stroke, fill));
        } else if (stroke) {
            for (PathSegment segm : path)
                curpage.appendChild(createLineElement(segm.getX1(), segm.getY1(), segm.getX2(), segm.getY2()));
        } else {
            curpage.appendChild(createPathImage(path));
        }
    }

    @Override
    protected void renderImage(float x, float y, float width, float height, ImageResource resource) throws IOException {
        curpage.appendChild(createImageElement(x, y, width, height, resource));
    }

    // ===========================================================================================

    /**
     * Creates an element that represents a single page.
     * @return the resulting DOM element
     */
    protected Element createPageElement() {
        String pstyle = "";
        PDRectangle layout = getCurrentMediaBox();
        if (layout != null) {
            float w = layout.getWidth();
            float h = layout.getHeight();
            final int rot = pdpage.getRotation();
            if (rot == 90 || rot == 270) {
                float x = w;
                w = h;
                h = x;
            }
            pstyle = "width:" + w + UNIT + ";" + "height:" + h + UNIT + ";";
            pstyle += "overflow:hidden;";
        } else log.warn("No media box found");
        Element el = doc.createElement("div");
        el.setAttribute("id", "page_" + (pagecnt++));
        el.setAttribute("class", "page");
        el.setAttribute("style", pstyle);
        return el;
    }

    /**
     * Creates an element that represents a single positioned box with no content.
     * @return the resulting DOM element
     */
    protected Element createTextElement(float width) {
        Element el = doc.createElement("div");
        el.setAttribute("id", "p" + (textcnt++));
        el.setAttribute("class", "p");
        String style = curstyle.toString();
        style += "width:" + width + UNIT + ";";
        el.setAttribute("style", style);
        return el;
    }

    /**
     * Creates an element that represents a single positioned box containing the specified text
     * string.
     * @param data the text string to be contained in the created box.
     * @return the resulting DOM element
     */
    protected Element createTextElement(String data, float width) {
        Element el = createTextElement(width);
        Text text = doc.createTextNode(data);
        el.appendChild(text);
        return el;
    }

    /**
     * Creates an element that represents a rectangle drawn at the specified coordinates in the
     * page.
     * @param x      the X coordinate of the rectangle
     * @param y      the Y coordinate of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param stroke should there be a stroke around?
     * @param fill   should the rectangle be filled?
     * @return the resulting DOM element
     */
    protected Element createRectangleElement(float x, float y, float width, float height, boolean stroke, boolean fill) {
        float lineWidth = transformWidth(getGraphicsState().getLineWidth());
        float wcor = stroke ? lineWidth : 0.0f;
        float strokeOffset = wcor == 0 ? 0 : wcor / 2;
        width = width - wcor < 0 ? 1 : width - wcor;
        height = height - wcor < 0 ? 1 : height - wcor;
        StringBuilder pstyle = new StringBuilder(50);
        pstyle.append("left:").append(style.formatLength(x - strokeOffset)).append(';');
        pstyle.append("top:").append(style.formatLength(y - strokeOffset)).append(';');
        pstyle.append("width:").append(style.formatLength(width)).append(';');
        pstyle.append("height:").append(style.formatLength(height)).append(';');
        if (stroke) {
            String color = colorString(getGraphicsState().getStrokingColor());
            pstyle.append("border:").append(style.formatLength(lineWidth)).append(" solid ").append(color).append(';');
        }
        if (fill) {
            String fcolor = colorString(getGraphicsState().getNonStrokingColor());
            pstyle.append("background-color:").append(fcolor).append(';');
        }
        Element el = doc.createElement("div");
        el.setAttribute("class", "r");
        el.setAttribute("style", pstyle.toString());
        el.appendChild(doc.createEntityReference("nbsp"));
        return el;
    }

    /**
     * Create an element that represents a horizntal or vertical line.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return the created DOM element
     */
    protected Element createLineElement(float x1, float y1, float x2, float y2) {
        HtmlDivLine line = new HtmlDivLine(x1, y1, x2, y2);
        String color = colorString(getGraphicsState().getStrokingColor());
        StringBuilder pstyle = new StringBuilder(50);
        pstyle.append("left:").append(style.formatLength(line.getLeft())).append(';');
        pstyle.append("top:").append(style.formatLength(line.getTop())).append(';');
        pstyle.append("width:").append(style.formatLength(line.getWidth())).append(';');
        pstyle.append("height:").append(style.formatLength(line.getHeight())).append(';');
        pstyle.append(line.getBorderSide()).append(':').append(style.formatLength(line.getLineStrokeWidth())).append(" solid ").append(color)
                .append(';');
        if (line.getAngleDegrees() != 0) pstyle.append("transform:").append("rotate(").append(line.getAngleDegrees()).append("deg);");
        Element el = doc.createElement("div");
        el.setAttribute("class", "r");
        el.setAttribute("style", pstyle.toString());
        el.appendChild(doc.createEntityReference("nbsp"));
        return el;
    }

    protected Element createPathImage(List<PathSegment> path) throws IOException {
        PathDrawer drawer = new PathDrawer(getGraphicsState());
        ImageResource renderedPath = drawer.drawPath(path);
        return createImageElement((float) renderedPath.getX(), (float) renderedPath.getY(), renderedPath.getWidth(), renderedPath.getHeight(),
                renderedPath);
    }

    /**
     * Creates an element that represents an image drawn at the specified coordinates in the page.
     * @param x        the X coordinate of the image
     * @param y        the Y coordinate of the image
     * @param width    the width coordinate of the image
     * @param height   the height coordinate of the image
     * @param type     the image type: <code>"png"</code> or <code>"jpeg"</code>
     * @param resource the image data depending on the specified type
     * @return
     */
    protected Element createImageElement(float x, float y, float width, float height, ImageResource resource) throws IOException {
        StringBuilder pstyle = new StringBuilder("position:absolute;");
        pstyle.append("left:").append(x).append(UNIT).append(';');
        pstyle.append("top:").append(y).append(UNIT).append(';');
        pstyle.append("width:").append(width).append(UNIT).append(';');
        pstyle.append("height:").append(height).append(UNIT).append(';');
        Element el = doc.createElement("img");
        el.setAttribute("style", pstyle.toString());
        // ignore img
        // String imgSrc = config.getImageHandler().handleResource(resource);
        String imgSrc = "";
        if (!disableImageData && !imgSrc.isEmpty()) el.setAttribute("src", imgSrc);
        else el.setAttribute("src", "");
        return el;
    }

    /**
     * Generate the global CSS style for the whole document.
     * @return the CSS code used in the generated document header
     */
    protected String createGlobalStyle() {
        StringBuilder ret = new StringBuilder();
        ret.append(createFontFaces());
        ret.append("\n");
        ret.append(defaultStyle);
        return ret.toString();
    }

    @Override
    protected void updateFontTable() {
        // skip font processing completley if ignore fonts mode to optimize processing speed
        // ignore font
        // if (!(config.getFontHandler() instanceof IgnoreResourceHandler)) super.updateFontTable();
    }

    protected String createFontFaces() {
        StringBuilder ret = new StringBuilder();
        for (FontTable.Entry font : fontTable.getEntries())
            createFontFace(ret, font);

        return ret.toString();
    }

    private void createFontFace(StringBuilder ret, FontTable.Entry font) {
        ret.append("@font-face {");
        ret.append("font-family:\"").append(font.usedName).append("\";");
        ret.append("src:url('");
        // ignore font
        // try {
        // String src = config.getFontHandler().handleResource(font);
        // ret.append(src);
        // } catch (IOException e) {
        // log.error("Error writing font face data for font: " + font.getName());
        // e.printStackTrace();
        // }
        ret.append("');");
        ret.append("}\n");
    }

    /**
     * Maps input line to an HTML div rectangle, since HTML does not support standard lines
     */
    protected class HtmlDivLine {

        private final float   x1;

        private final float   y1;

        private final float   x2;

        private final float   y2;

        private final float   width;

        private final float   height;

        // horizontal or vertical lines are treated separately (no rotations used)
        private final boolean horizontal;

        private final boolean vertical;

        public HtmlDivLine(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.width = Math.abs(x2 - x1);
            this.height = Math.abs(y2 - y1);
            this.horizontal = (height < 0.5f);
            this.vertical = (width < 0.5f);
        }

        public float getHeight() {
            return vertical ? height : 0;
        }

        public float getWidth() {
            if (vertical) return 0;
            else if (horizontal) return width;
            else return distanceFormula(x1, y1, x2, y2);
        }

        public float getLeft() {
            if (horizontal || vertical) return Math.min(x1, x2);
            else return Math.abs((x2 + x1) / 2) - getWidth() / 2;
        }

        public float getTop() {
            if (horizontal || vertical) return Math.min(y1, y2);
            else
                // after rotation top left will be center of line so find the midpoint and correct
                // for the line to border transform
                return Math.abs((y2 + y1) / 2) - (getLineStrokeWidth() + getHeight()) / 2;
        }

        public double getAngleDegrees() {
            if (horizontal || vertical) return 0;
            else return Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
        }

        public float getLineStrokeWidth() {
            float lineWidth = transformWidth(getGraphicsState().getLineWidth());
            if (lineWidth < 0.5f) lineWidth = 0.5f;
            return lineWidth;
        }

        public String getBorderSide() {
            return vertical ? "border-right" : "border-bottom";
        }

        private float distanceFormula(float x1, float y1, float x2, float y2) {
            return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }
    }
}
