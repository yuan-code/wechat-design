package com.hualala.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XMLParse class
 * <p>
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
public class XMLParse {


    /**
     * @param xml 要转换的xml字符串
     * @return 转换成map后返回结果
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String xml) throws Exception {
        Map<String, String> respMap = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
        Element root = doc.getRootElement();
        xmlToMap(root, respMap);
        return respMap;
    }

    /**
     * map对象转行成xml
     *
     * @param map
     * @return
     * @throws IOException
     */
    public static String mapToXml(Map<String, Object> map) throws IOException {
        Document d = DocumentHelper.createDocument();
        Element root = d.addElement("xml");
        mapToXml(root, map);
        StringWriter sw = new StringWriter();
        XMLWriter xw = new XMLWriter(sw);
        xw.setEscapeText(false);
        xw.write(d);
        return sw.toString();
    }


    /**
     * 递归转换
     *
     * @param root
     * @param map
     * @return
     * @throws IOException
     */
    private static Element mapToXml(Element root, Map<String, Object> map) throws IOException {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Element element = root.addElement(entry.getKey());
                mapToXml(element, (Map<String, Object>) entry.getValue());
            } else {
                root.addElement(entry.getKey()).addText(entry.getValue().toString());
            }
        }
        return root;
    }


    /**
     * 递归转换
     *
     * @param tmpElement
     * @param respMap
     * @return
     */
    private static Map<String, String> xmlToMap(Element tmpElement, Map<String, String> respMap) {
        if (tmpElement.isTextOnly()) {
            respMap.put(tmpElement.getName(), tmpElement.getText());
            return respMap;
        }
        Iterator<Element> eItor = tmpElement.elementIterator();
        while (eItor.hasNext()) {
            Element element = eItor.next();
            xmlToMap(element, respMap);
        }
        return respMap;
    }


}
