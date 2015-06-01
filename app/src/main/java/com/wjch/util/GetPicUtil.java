package com.wjch.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetPicUtil extends DefaultHandler {

	// private static URL url;
	private static Document document;
	public static Bitmap getImageByUrl(String url) {
		Bitmap bmp = null;
		try {
			if (url.equals("")) {
				return null;
			}
			URL myurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			InputStream is = conn.getInputStream();
			bmp = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	public static String parseUrl(String songname) {
		SAXReader reader = new SAXReader();
		//the standrad url:[q:songname]  [tag:song's tag]
		// http://api.douban.com/music/subjects?q=owboy&start-index=1&max-results=1
		String name = null;
		String allname = null;
		URL myurl;
		InputStream in = null;
		HttpURLConnection conn = null;
		try {
			name = URLEncoder.encode(songname,"UTF-8");
			String urlString = "http://api.douban.com/music/subjects?q=";
			allname = urlString +name + "&start-index=1&max-results=1";
			myurl = new URL(allname);
			conn = (HttpURLConnection) myurl.openConnection();
			  conn.setRequestProperty("Accept-Encoding", "identity");
			conn.setConnectTimeout(6000);// timeout //douban api1.0 ---10times/min
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			in = new BufferedInputStream(conn.getInputStream());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("==========start to catch!!======");
			document = reader.read(in);
		} catch (DocumentException e) {
			System.out.println("==========NO input source=======");
			e.printStackTrace();
			return "";
		} finally {
				conn.disconnect();
		}
//		System.out.println("allxmltext"+document.asXML());
		String s = document.asXML();
		return StringUtil.getUrlFromDoubanXml(s);
	}

}
