package com.youboy.tools.email.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.StringUtils;

import com.youboy.tools.email.service.SendEmailService;
import com.youboy.tools.email.utils.MyFileUtils;
import com.youboy.tools.email.utils.SwingUtils;

public class MainForm extends JFrame{
	
	//基本参数
	int width=400;
	int height=400;
	
	String title="邮件发送工具";
	final String[] columnNames = {"Num","Email"};
	
	//控件
	JDialog dialog = new JDialog(getThis());
    JTable stateTable=new JTable();
	JScrollPane stateScrollPanel = new JScrollPane(stateTable);   //支持滚动
	JProgressBar progressBar=new JProgressBar(0,100);
	JLabel timeLabel=new JLabel("剩余时间：");
	
	JButton loadEmailBtn=new JButton("录入邮箱");
	JButton loadTempletBtn=new JButton("录入模板");
	JButton openTempletBtn=new JButton("打开模板");

	JButton setTitleBtn=new JButton("确定");
	JButton sendEmailBtn=new JButton("发送");
	JButton isSuspendedBtn=new JButton("暂停");
	
	final FileDialog loadEmailFd=new FileDialog(this,
			"录入邮箱【内容格式：一行写一个Email】",FileDialog.LOAD);
	final FileDialog loadTempletFd=new FileDialog(this,"录入模板【内容格式：纯文本或html】",FileDialog.LOAD);
	final JTable emailTable=new JTable(new DefaultTableModel());
	
	// 辅助参数
	List<String> emailList=new ArrayList<String>();
	List<String[]> sendStateList= new ArrayList<String[]>();
	
	int totalSeconds;
	private boolean pauseState=false;
	private boolean stopState=false;
	
	public boolean isPause(){
		return pauseState;
	}
	
	//
	public MainForm(){
		this.setTitle(title);
		this.setSize(width, height);
		//this.setLocation((screenWidth-width)/2, (screenHeight-height)/2);
		SwingUtils.setControlCenter(this);
		
		this.setVisible(true);
		//this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		 //设置程序关闭的类型，防止关闭
	    this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				int state = JOptionPane.showConfirmDialog(null, "是否关闭？", "提示",
						JOptionPane.YES_NO_OPTION);
				
				if(JOptionPane.YES_OPTION==state){
					System.exit(-1);
				}
			}
		});
			
	}
	
	
	private MainForm getThis(){
		return this;
	} 
	
	
	
	private void setControl(){
		setLayout();
        
        controlEvent();
	}


	private void setLayout() {
		//展示
		JPanel mainPanel=new JPanel();
		//默认值
		JScrollPane scrollPane = new JScrollPane(emailTable);   //支持滚动
		
		/*mainPanel.setLayout(new FlowLayout(0, 12, 12));
		mainPanel.add(loadEmailBtn);
		mainPanel.add(loadTempletBtn);
		mainPanel.add(openTempletBtn);
		//mainPanel.add(setTitleBtn);
		mainPanel.add(sendEmailBtn);*/
		
		JPanel panelOne=new JPanel();
		panelOne.add(loadEmailBtn);
		panelOne.add(loadTempletBtn);
		panelOne.add(openTempletBtn);
		
		JPanel panelTwo=new JPanel();
		//panelTwo.add(new JLabel("邮件标题："));
		JTextField titleText = new JTextField("请输入你的邮件标题,然后点击发送。",20);
		panelTwo.add(titleText);
		//panelTwo.add(setTitleBtn);
		panelTwo.add(sendEmailBtn);
		
		mainPanel.add(panelOne);
		mainPanel.add(panelTwo);
		mainPanel.setLayout(new GridLayout(2, 1));
		
		this.add(mainPanel,BorderLayout.NORTH);
		this.add(scrollPane,BorderLayout.CENTER);
		this.add(new Panel(),BorderLayout.SOUTH);
		
		//初始化table
		String emailListData = MyFileUtils.read(getEmailListPath());
		setTableData(emailTable, emailListData);
		
		this.validate();
        this.repaint();
	}


	private void controlEvent() {
		//事件绑定
		loadEmailBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				loadEmailFd.setVisible(true);
				
				if(loadEmailFd.getFile()==null){
					return;
				}
				
				String data = MyFileUtils.read(loadEmailFd.getDirectory()+"/"+loadEmailFd.getFile());
				setTableData(emailTable, data);
				
				MyFileUtils.write(data, getEmailListPath());
			}
		});
		
		loadTempletBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				loadTempletFd.setVisible(true);
				if(loadTempletFd.getFile()==null){
					return ;
				}
				
				String data = MyFileUtils.read(loadTempletFd.getDirectory()+"/"+loadTempletFd.getFile());
				MyFileUtils.write(data, getTempletPath() );
			}
		});
		
		openTempletBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//String cmd="C:/Windows/System32/notepad";
				String cmd="rundll32 url.dll FileProtocolHandler file://"+getTempletPath();
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		sendEmailBtn.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				int state = JOptionPane.showConfirmDialog(null, "是否马上发邮件？", "提示",
						JOptionPane.YES_NO_OPTION);
				
				if(JOptionPane.YES_OPTION!=state){
					return;
				}
				
				stopState=false;
				pauseState=false;
				
				dialog.setVisible(true);
				dialog.setModal(true);
				dialog.setTitle("邮件发送进度");
				dialog.setSize(400, 400);
				//dialog.setLocation((screenWidth-width)/2+50, (screenHeight-height)/2+50);
				SwingUtils.setControlCenter(dialog);
				
				progressBar.setStringPainted(true); // 显示百分比字符
				progressBar.setIndeterminate(false); // 不确定的进度条
				
				JPanel progressPanel =new JPanel();
				progressPanel.setLayout( new FlowLayout(3, 8, 10));
				progressPanel.add(new JLabel("当前:"));
				progressPanel.add(progressBar);
				progressPanel.add(timeLabel);
				progressPanel.add(isSuspendedBtn);
				
				dialog.add(progressPanel,BorderLayout.NORTH);
				dialog.add(stateScrollPanel,BorderLayout.CENTER);
				updateSendStateData("开始");
				
				//开线程，要不然控件绘图会有问题
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						SendEmailService.sendEmail(emailList,getThis());
					}
				}).start();
				
				getThis().setEnabled(false);
			}
		});
		
		//对话窗口关闭的时候
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				getThis().setEnabled(true);
				stopState=true;
			}
		});
		
		
		/*setTitleBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JDialog dialog = new JDialog(getThis());
				dialog.setVisible(true);
				dialog.setTitle("邮件内容");
				
				//dialog.setLayout(new FlowLayout(3, 3, 3));
				dialog.setSize(200, 100);
				SwingUtils.setControlCenter(dialog);
			}
		});*/
		
		isSuspendedBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(isPause()==false){
					System.out.println("继续");
					pauseState=true;
					isSuspendedBtn.setText("继续");
				}else{
					System.out.println("暂停");
					pauseState=false;
					isSuspendedBtn.setText("暂停");
				}
			}
		});
	}
	
	private String getTempletPath(){
		String templetPath="/mail/mail.ftl";
		String filePath = getJarPath();
		File file=new File(filePath + "/"+templetPath);
		noExistCreate(file);
		
		return file.getPath();
	}

	private String getEmailListPath(){
		String emailPath = "/mail/emailList.txt";

		String filePath = getJarPath();
		File file=new File(filePath + "/"+emailPath);
		noExistCreate(file);
		
		return file.getPath();
	}
	
	private void noExistCreate(File file) {
		if(!file.exists()){
			/*try {
				FileUtils.write(new File(filePath + "/"+templetPath), "");
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			new File(file.getParent()).mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getJarPath() {
		URL url = MainForm.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
			// 截取路径中的jar包名
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}

		//class运行环境
		filePath=filePath.replaceAll("classes", "");
		
		File file = new File(filePath);

		filePath = file.getAbsolutePath();// 得到windows下的正确路径
		return filePath;
	}
	
	private void setTableData(final JTable emailTable, String data) {
		if(StringUtils.isBlank(data)){
			System.out.println("没有数据");
			return;
		}
		
		String[] dataStr = data.split("\n");
		String[][] emailArr=new String[dataStr.length][2];
		for(int i=0;i<emailArr.length;i++ ){
			emailArr[i][0]=(i+1)+"";
			emailArr[i][1]=dataStr[i];
		}
		emailList=Arrays.asList(dataStr);
		
		TableModel dataModel=new DefaultTableModel(emailArr, columnNames) {
			public boolean isCellEditable(int row, int column) {
			    return false;//返回true表示能编辑，false表示不能编辑
			}   
		};
		emailTable.setModel(dataModel);
		
		SwingUtils.setValueCenter(emailTable);
		
		//列宽
		emailTable.getColumn(columnNames[0]).setPreferredWidth(20);
		emailTable.getColumn(columnNames[1]).setPreferredWidth(180);
	}
	
	
	//******************public***********************
	public void updateSendStateData(String email){
		String[] columnNames = new String[]{"num","email"};
		sendStateList.add(new String[]{sendStateList.size()+"",email});
		
		String[][] data = sendStateList.toArray(new String[sendStateList.size()+2][]);

		/*for(String[] strArr:data){
			for(String str:strArr){
				System.out.print(str+"-");
			}
			System.out.println();
		}*/
		TableModel dataModel=new DefaultTableModel(data, columnNames){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		stateTable.setModel(dataModel);
		SwingUtils.setValueCenter(stateTable);
		
		stateTable.getColumn(columnNames[0]).setPreferredWidth(20);
		stateTable.getColumn(columnNames[1]).setPreferredWidth(180);
		
		//stateScrollPanel.doLayout();
		
		//控制滚动条位置
		JScrollBar jsVB = stateScrollPanel.getVerticalScrollBar();  
        if (jsVB != null) {  
            jsVB.setValue(jsVB.getMaximum()-1);  
        }  
  
        JScrollBar jsHB = stateScrollPanel.getHorizontalScrollBar();  
        if (null != jsHB) {  
            jsHB.setValue(jsHB.getMaximum() / 6);  
        }  
		
	}
	
	public void updateProgressBar(int data){
		progressBar.setValue(data);
	}
	
	public void startTimeLabel(final int count,final int sleepTime){
		new Thread(){
			
			@Override
			public void run() {
				totalSeconds=(count-1)*sleepTime/1000;
				int surplusSeconds=(count-1)*sleepTime/1000;
				try {
					while(true){
						if(stopState){
							break;
						}
						
						if(isPause()){
							Thread.sleep(1);
							continue;
						}
						
						if(surplusSeconds<0){
							return;
						}
						//百分比
						int percents=(totalSeconds-surplusSeconds)*100/totalSeconds;
						//System.out.println(percents);
						getThis().updateProgressBar(percents);
						
						//时间
						int seconds=surplusSeconds%60;
						int minutes=surplusSeconds/60;
						DecimalFormat df = new DecimalFormat("00");
						timeLabel.setText("time： "+df.format(minutes)+":"+df.format(seconds));
						Thread.sleep(1000);
						
						surplusSeconds--;
					}
				
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println("结束");
			}
		}.start();
		
	}
	
	public void alert(String title,String text){
		
		JDialog alert = new JDialog();
		alert.setVisible(true);
		alert.setTitle(title);
		
		alert.setSize(200, 100);
		alert.add(new JLabel(text),BorderLayout.CENTER);
		
		SwingUtils.setControlCenter(alert);
	}
	
	public void alert(String text){
		alert("提示",text);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainForm mainForm = new MainForm();
				mainForm.setControl();
			}
		});
	}
	
}
