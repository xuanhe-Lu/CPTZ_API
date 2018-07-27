package com.ypiao.util;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @NAME:MonthFound
 * @DESCRIPTION:时间与时间戳互相转换
 * @AUTHOR:luxh
 * @DATE:2018/7/16
 * @VERSION:1.0
 */
public class MonthFound {

    /*
     * @CLASSNAME:getDataInfo
     * @DESCRIPTION:时间转换为时间戳，
     * @AUTHOR:luxh
     * @DATE:2018/7/16
     * @VERSION:1.0
     */
 public  static  long getDataStamp(String data,String format)throws ParseException{
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
     java.util.Date date = simpleDateFormat.parse(data);
     long ts = date.getTime();
     return ts;
 }
 /*
  * @CLASSNAME:getDataFormat
  * @DESCRIPTION:时间戳转换为时间
  * @AUTHOR:luxh
  * @DATE:2018/7/16
  * @VERSION:1.0
  */
 public static String getDataFormat(long ts,String format){
     Date date1 = new Date(ts);
     SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(format);
     String da = simpleDateFormat1.format(date1);
     return da;
 }


}
