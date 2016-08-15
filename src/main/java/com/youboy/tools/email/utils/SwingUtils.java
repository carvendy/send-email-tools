package com.youboy.tools.email.utils;

import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SwingUtils {

	 static Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	public static void setControlCenter(Component component){
		int screenHeight =(int) toolkit.getScreenSize().getHeight();
		int screenWidth = (int)toolkit.getScreenSize().getWidth();
		
		component.setLocation((screenWidth-component.getWidth())/2
					, (screenHeight-component.getHeight())/2);
	}
	
	
	public static void setValueCenter(JTable table){
		//内容居中
		DefaultTableCellRenderer   r   =   new   DefaultTableCellRenderer();   
		r.setHorizontalAlignment(JLabel.CENTER);   
		table.setDefaultRenderer(Object.class,   r);
	}
}
