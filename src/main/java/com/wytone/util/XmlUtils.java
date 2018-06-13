package com.wytone.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author 王永煌
 * @version 1.0
 * 深圳市采美信息技术有限公司
 * @date 2015年3月5日
 * @time 下午4:10:25
 */
public class XmlUtils {
    /**
     * 解析微信发来的请求（XML）
     *
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(String msg) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>(10);
        // 从request中取得输入流
        InputStream inputStream = new ByteArrayInputStream(msg.getBytes("UTF-8"));
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    public static void main(String[] args) throws Exception {
        String result = "<xml> " +
                "<return_code><![CDATA[FAIL]]></return_code>" +
                "<return_msg><![CDATA[参数错误:发放金额、最小金额、最大金额必须相等.]]></return_msg>" +
                "<result_code><![CDATA[FAIL]]></result_code>" +
                "<err_code><![CDATA[PARAM_ERROR]]></err_code>" +
                "<err_code_des><![CDATA[参数错误:发放金额、最小金额、最大金额必须相等.]]></err_code_des>" +
                "<mch_billno><![CDATA[100257913453476611]]></mch_billno>" +
                "<mch_id>10025791</mch_id>" +
                "<wxappid><![CDATA[wxea43a0f9ebce9e66]]></wxappid>" +
                "<re_openid><![CDATA[oVTYvt4xhmqGKSI3owBO1TdJqtYo]]></re_openid>" +
                "<total_amount>184</total_amount>" +
                "</xml>";
        Map<String, String> map = parseXml(result);
        System.out.println(map.get("return_code"));
    }
}
