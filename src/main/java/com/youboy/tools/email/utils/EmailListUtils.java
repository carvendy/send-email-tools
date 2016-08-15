package com.youboy.tools.email.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;  

import javax.mail.MessagingException;  
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;  
  


import org.springframework.mail.MailException;  
import org.springframework.mail.javamail.JavaMailSender;  
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;  
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;  
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;  
  


import freemarker.template.Template;  
  
/** 
 * 发送邮件 可以自己编写html模板 
 * 
 */  
public class EmailListUtils {  
  
    private List<JavaMailSender> senderList;    

    private String formEmail;
    private String emailAccountName="一呼百应采购商城";
    
    public String errorMsg="";
    private int sleepTime = 0;
    
    //---------getter&setter------------
    public void setFormEmail(String formEmail) {    
        this.formEmail = formEmail;    
    } 
      
    /** 
     * 发送邮件 
     * @param root 存储动态数据的map 
     * @param toEmail 邮件地址 
     * @param subject 邮件主题 
     * @return 
     */  
    public boolean sendTemplateMail(String subject,String email,String html){    
    	System.out.println("休眠："+sleepTime / 1000+"秒。。。。。。");
    	try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	
        try {  
        	JavaMailSender sender = getSender();
            MimeMessage msg = sender.createMimeMessage();    
            MimeMessageHelper helper=new MimeMessageHelper(msg,false,"utf-8");//由于是html邮件，不是mulitpart类型    
            helper.setFrom(formEmail,emailAccountName);    
            helper.setTo(email);   
           // helper.setCc(getCopyList());
            helper.setSubject(subject);   
            
            helper.setText(html, true);    
            sender.send(msg);  
            //System.out.println("成功发送模板邮件");    
            return true;  
        } catch (MailException e) {  
           // System.out.println("失败发送模板邮件"); 
            e.printStackTrace();  
            errorMsg=e.toString();
            return false;  
        } catch (MessagingException e) {  
        	//System.out.println("失败发送模板邮件");  
            e.printStackTrace();  
        	errorMsg=e.toString();
            return false;  
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			errorMsg=e.toString();
            return false;  
		}   
    	
    }    
    

	private int count = 0;
	private JavaMailSender getSender(){
		int size = senderList.size();
		int random = count % size;
		count++;
		JavaMailSender javaMailSender = senderList.get(random);
		JavaMailSenderImpl impl = (JavaMailSenderImpl)javaMailSender;
		formEmail = impl.getUsername();//修改账号
		return javaMailSender;
	}
	
    public List<JavaMailSender> getSenderList() {
		return senderList;
	}

	public void setSenderList(List<JavaMailSender> senderList) {
		this.senderList = senderList;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
}  