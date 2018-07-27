package com.ypm.user;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.ypm.util.MD5Util;



public class Qrytrans {
	private static final String ENCODEING="UTF-8";
	public static void requestPost(String url,List<NameValuePair> params) throws ClientProtocolException, IOException {
	    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
	    HttpPost httppost = new HttpPost(url);
	    httppost.setEntity(new UrlEncodedFormEntity(params,ENCODEING));
	    CloseableHttpResponse response = httpclient.execute(httppost);
	    System.out.println(response.toString());
	    HttpEntity entity = response.getEntity();
	    String jsonStr = EntityUtils.toString(entity, "utf-8");
	    System.out.println(jsonStr);
	    httppost.releaseConnection();
	}
	
	public static String getDate(int i){
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(new Date());
			if(i==1){
				String str = sf.format(calendar.getTime()); 
				return str;
			}else{
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				String str = sf.format(calendar.getTime()); 
				return str;	 
			}	
		}
	public static void main(String[] args){
	    try {
	    	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"+
	    			"<qrytransreq>"+
	    			"<ver>1.00</ver>"+
	    			"<busicd>AP01</busicd>"+    //AP01:代付  AC01：代收  TP01：退票
//	    			"<orderno></orderno>"+      //查询多个流水，流水中间用英文,间隔，一次最多50个
	    			"<startdt>"+Qrytrans.getDate(2)+"</startdt>"+  
	    			"<enddt>"+Qrytrans.getDate(1)+"</enddt>"+
//	    			"<transst>1</transst>"+
	    			"</qrytransreq>";
	    	String macSource = "0002900F0345178|123456|"+"qrytransreq"+"|"+xml;
	    	String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
	        String loginUrl = "https://fht-test.fuiou.com/fuMer/req.do";
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("merid", "0002900F0345178"));
	        params.add(new BasicNameValuePair("reqtype", "qrytransreq"));
	        params.add(new BasicNameValuePair("xml", xml));
	        params.add(new BasicNameValuePair("mac", mac));
	        requestPost(loginUrl,params);
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
}
