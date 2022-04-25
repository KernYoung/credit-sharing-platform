package com.fanruan.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestPostHttp {

    public static void main(String[] args) {
        String url= "http://10.0.132.59:14114/ediserver/gateway.do";

        String param ="{\r\n"
                + "    \"datas\": [\r\n"
                + "        {\r\n"
                + "            \"ediKey\": \"businessid\",\r\n"
                + "            \"ediVal\": \"R2022000030\"\r\n"
                + "        }\r\n"
                + "    ],\r\n"
                + "    \"imethod\": \"getEdiMessages\"\r\n"
                + "}";


        String insureResponsePost = insureResponsePost(url,param);

        System.out.println(insureResponsePost);
    }

    public static String insureResponsePost(String url, String param) {
        PrintWriter out = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = "";
        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestMethod( "POST");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(300000);
            conn.setRequestProperty("Charset", "utf-8");
            // ��������Ϊjson�����Ϊ������ʽ���Խ����޸�
            conn.setRequestProperty( "Content-Type", "application/json");
            conn.setRequestProperty( "Content-Encoding", "utf-8");
            // ����POST�������������������
            conn.setDoOutput( true);
            conn.setDoInput( true);
            conn.setUseCaches( false);
            // ��ȡURLConnection�����Ӧ�������
            out = new PrintWriter(conn.getOutputStream());
            // �����������
            out.print(param);
            // flush������Ļ���
            out.flush();
            is = conn.getInputStream();
            BufferedReader bufferedReader = null;
            if (is != null) {
                // �˴���Ҫ�������ʽ����ΪUTF_8����� InputStream ����ȡʱ��������������
                bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }

            result = stringBuilder.toString();
        } catch (Exception e) {
            System. out.println( "���� POST ��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر��������������
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (conn!= null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


}

