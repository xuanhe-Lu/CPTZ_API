package com.ypiao.bean;

import java.io.File;
import java.io.IOException;

public interface NetService {

	/** 连接编号 */
	public long getId();
	/** 向服务端发送数据 */
	public Messager send(int cls, int rev, Message msg) throws IOException;
	/** 向服务端发送数据 */
	public Messager send(int cls, int rev, Message msg, File file) throws IOException;
	/** 向客户端发送数据 */
	public void sender(int cls, int rev, Message msg) throws IOException;
	/** 向客户端发送数据 */
	public void sender(int cls, int rev, Message msg, File file) throws IOException;

}
