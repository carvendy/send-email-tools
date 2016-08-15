package com.youboy.tools.email.service;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.youboy.tools.email.swing.MainForm;
import com.youboy.tools.email.utils.EmailUtils;

public class SendEmailService {

	static int sleepTime =5000;
	static String subject="我是测试的";
	
	public static void sendEmail(List<String> emailList,MainForm mainForm){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/root-context.xml");
		EmailUtils emailUtils=(EmailUtils)ctx.getBean("templateEmail");
		
		if(emailList.size()==0){
			return ;
		}
		
		mainForm.startTimeLabel(emailList.size(), sleepTime);
		
		try {
			for(int i=0;i<emailList.size();i++){
				if(mainForm.isPause()){
					i--;
					continue;
				}
				//System.out.println("第"+(i+1)+"个--"+emailList.get(i));
				mainForm.updateSendStateData(emailList.get(i));
				//**********send**************
				emailUtils.sendTemplateMail(subject, emailList.get(i));
					
				if(i<emailList.size()-1){
					Thread.sleep(sleepTime);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mainForm.alert("结束");
		//System.out.println("完成~！");
	}
	
	
	
}
