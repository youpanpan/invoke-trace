package com.chengxuunion.util.xml;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @Author youpanpan
 * @Description:
 * @Date:创建时间: 2019-02-22 10:53
 * @Modified By:
 */
public class XmlUtils {

    private XmlUtils() {

    }

    /**
     * 解析XML
     *
     * @param xmlContent
     * @return
     */
    public static Document parseXml(String xmlContent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlContent));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 格式化XML
     *
     * @param unformattedXml    未格式化的XML
     * @param lineWidth         行宽
     * @param indentSize        缩进长度
     * @return
     */
    public static String formatXml(String unformattedXml, int lineWidth, int indentSize) {
        try {
            final Document document = parseXml(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(lineWidth);
            format.setIndenting(true);
            format.setIndent(indentSize);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
