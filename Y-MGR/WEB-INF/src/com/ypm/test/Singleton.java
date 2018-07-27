package com.ypm.test;

import java.util.HashSet;
import java.util.Set;

/**
 * 单例 
 */
public class Singleton {
	
	private static final Set<String> set = new HashSet<>();
	
	public Set<String> getSet () {
		if (set.size() == 0) {
			System.out.println("set 为空，开始添加值...");
			set.add("a");
			set.add("b");
			set.add("c");
		} else {
			System.out.println("set 不为空，返回已经添加值的set...");
		}
		
		return set;
	}

	public static void main(String[] args) {
//		Singleton singleton = new Singleton();
		
		// 连续调用三次，查看结果
//		System.out.println(singleton.getSet());
//		System.out.println(singleton.getSet());
//		System.out.println(singleton.getSet());
		
		String fileName = "ymjf.apk";
		System.out.println(fileName.substring( 0, fileName.lastIndexOf('.') ));
	}
}
