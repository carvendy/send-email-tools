package com.youboy.tools.email.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.youboy.tools.email.utils.EmailListUtils;
import com.youboy.tools.email.utils.EmailUtils;

public class Test {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/root-context.xml");
		EmailListUtils emailListUtils=(EmailListUtils)ctx.getBean("templateEmailList");
		String subject="我是测试的";
		String html = "测试内容";
		emailListUtils.sendTemplateMail(subject, "1150483011@qq.com",html);
		
		/*emailListUtils.sendTemplateMail(subject, "1150483011@qq.com");
		
		emailListUtils.sendTemplateMail(subject, "1150483011@qq.com");*/
	}
	
	public static void one(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/root-context.xml");
		EmailUtils emailUtils=(EmailUtils)ctx.getBean("templateEmail");
		String subject="我是测试的";
		emailUtils.sendTemplateMail(subject, "11504830aa11@qq.com");
	}
}
