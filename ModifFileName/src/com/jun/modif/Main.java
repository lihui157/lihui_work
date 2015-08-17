package com.jun.modif;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

	private JFrame frame;
	private JTextField textField;
	private JLabel lblNewLabel;
	private JFileChooser fromChooser;
	private JFileChooser toChooser;
	private File fromFile;
	private File toFile;
	private int count=0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("\u8BBE\u7F6E\u6587\u4EF6\u6E90\u76EE\u5F55\u4E0E\u5BFC\u51FA\u76EE\u5F55");
		label.setBounds(10, 10, 184, 15);
		frame.getContentPane().add(label);
		
		textField = new JTextField();
		textField.setBounds(10, 35, 296, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton button = new JButton("\u9009\u62E9\u76EE\u5F55");
		button.addActionListener(fromChooserListener);
		button.setBounds(310, 34, 114, 23);
		frame.getContentPane().add(button);
		
		JButton runButton = new JButton("运行(.PNG 转 .png)");
		runButton.addActionListener(runListener);
		runButton.setBounds(10, 97, 208, 23);
		
		frame.getContentPane().add(runButton);
		
		JButton btnNewButton = new JButton("运行(转@2x、@3x)");
		btnNewButton.addActionListener(runListener1);
		btnNewButton.setBounds(228, 97, 196, 23);
		frame.getContentPane().add(btnNewButton);
		
		textField_1 = new JTextField();
		textField_1.setBounds(10, 66, 296, 21);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("\u5BFC\u51FA\u76EE\u5F55");
		btnNewButton_1.addActionListener(toChooserListener);
		btnNewButton_1.setBounds(310, 65, 114, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(10, 130, 414, 15);
		frame.getContentPane().add(lblNewLabel);
	}
	
	private ActionListener fromChooserListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			fromChooser = new JFileChooser();
			fromChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fromChooser.showDialog(new JLabel(), "选择");
			fromFile=fromChooser.getSelectedFile(); 
			if(fromFile!=null)
			textField.setText(fromFile.getAbsolutePath());
		}
	};
	
	private ActionListener toChooserListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			toChooser = new JFileChooser();
			toChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			toChooser.showDialog(new JLabel(), "选择");
			toFile=toChooser.getSelectedFile(); 
			if(toFile!=null)
				textField_1.setText(toFile.getAbsolutePath());
		}
	};
	
	private ActionListener runListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(fromFile==null||toFile==null) return;
	        processFile1(fromFile);
	        lblNewLabel.setText("完成,本次处理文件 "+count+" 个");
	        count = 0;
		}
	};
	
	private ActionListener runListener1 = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(fromFile==null||toFile==null) return;
	        processFile2(fromFile);
	        lblNewLabel.setText("完成,本次处理文件 "+count+" 个");
	        count = 0;
		}
	};
	private JTextField textField_1;
	/**
	 * .PNG 改 .png
	 * @param fFile
	 */
	private void processFile1(File fFile){
		
		if(fFile.exists()){
			File[] files = fFile.listFiles();
			for(File f:files){
				if(f.isDirectory()){
					processFile1(f);
				}else{
					if(f.getName().toLowerCase().endsWith(".png")){
						
						try {
							FileUtil.saveFile(f.getAbsolutePath()
									.replace(fromFile.getAbsolutePath()
											, (CharSequence) toFile.getAbsolutePath()).toLowerCase()
											, new FileInputStream(f));
							count++;
							System.out.println(""+f.getAbsolutePath());
							lblNewLabel.setText("process file "+f.getName()+"\n");
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	/**
	 * .png 转 @2x @3x
	 * @param fFile
	 */
private void processFile2(File fFile){
		
		if(fFile.exists()){
			File[] files = fFile.listFiles();
			for(File f:files){
				if(f.isDirectory()){
					processFile2(f);
				}else{
					if(f.getName().toLowerCase().endsWith(".png")){
						
						try {
							FileUtil.saveFile(f.getAbsolutePath()
									.replace(fromFile.getAbsolutePath()
											, toFile.getAbsolutePath()+"\\@2x\\").toLowerCase().replace(".png", "@2x.png")
											, new FileInputStream(f));
							FileUtil.saveFile(f.getAbsolutePath()
									.replace(fromFile.getAbsolutePath()
											, toFile.getAbsolutePath()+"\\@3x\\").toLowerCase().replace(".png", "@3x.png")
											, new FileInputStream(f));
							System.out.println(""+f.getAbsolutePath());
							count++;
							System.out.println(""+f.getAbsolutePath());
							lblNewLabel.setText("process file "+f.getName()+"\n");
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
