package com.koreais.crawler;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class CrawlerMain extends JFrame implements Runnable{
	int frame_w = 600;
	int frame_h = 600;
	int idx = 0;
	int howMany = 0;
	
	JLabel image_label = null;			// �ΰ� label 
	JLabel clawler_label = null;		// ũ�Ѹ� label
	JLabel chart_label = null;			// ǥ label
	JLabel change_chart_label = null;	// �������� ǥ label
	JLabel amount_chart_label = null;	// �ŷ��ݾ׼� ǥ label
	JLabel write_label1 = null;			// ���Ͽ� ���� label1
	JLabel write_label2 = null;			// ���Ͽ� ���� label2
	JLabel search_label = null;			// �˻� label
	
	JButton clawler_btn = null;			// ũ�Ѹ� button
	JButton change_chart_btn = null;	// �������� ǥ button
	JButton amount_chart_btn = null;	// �ŷ��ݾ׼� ǥ button
	JButton change_write_btn = null;	// ������ �� cvs���� ���� button
	JButton amount_write_btn = null;	// �ŷ��ݾ� �� cvs���� ���� button
	JButton search_btn = null;			// �˻� button
	JButton rand_btn = null;			// ���� button
	
	JComboBox comb = null;
	
	CoinCrawler cc = new CoinCrawler();
	SqliteDao db = new SqliteDao();
	
	public CrawlerMain() {
		// Frame ����
		setTitle("���� ũ�ѷ�");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screen1 = new Dimension(); // ���� �ػ�
		screen1 = Toolkit.getDefaultToolkit().getScreenSize();
		int xPos = (screen1.width / 2) - (frame_w / 2);
		int yPos = (screen1.height / 2) - (frame_h / 2);
		setBounds(xPos, yPos, frame_w, frame_h);

		Container con = getContentPane();
		con.setLayout(null);
		con.setBackground(Color.black);
		
		
		// ���⼭���� ������Ʈ ����
		
		
		//////////////////////////
		// 1. �̹��� �߰�		//
		//////////////////////////
		
		ImageIcon img = new ImageIcon("img/bithumb.png");
		image_label = new JLabel("", img , JLabel.CENTER);
		add(image_label);
		
		
		
		//////////////////////////
		// 2. ũ�Ѹ��ϱ�		//
		//////////////////////////
		
		clawler_label = new JLabel("ũ�Ѹ� �Ϸ��� Ŭ���ϼ���.");
		clawler_label.setForeground(Color.white);
		con.add(clawler_label);
		
		clawler_btn = new JButton("ũ�Ѹ��ϱ�");
		clawler_btn.setForeground(Color.WHITE);
		clawler_btn.setBackground(new Color(224,74,51));
		clawler_btn.setToolTipText("https://www.bithumb.com/���� ũ�Ѹ��� �����մϴ�.");
		clawler_btn.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String temp = JOptionPane.showInputDialog(clawler_btn, "�� ������ ����ұ��? (1~60)");
				try {
					howMany = Integer.parseInt(temp);
				} catch (Exception e2) { 
					if(temp==null) {
						JOptionPane.showMessageDialog(clawler_btn, "�Է��� �����ϴ�.");
					} else {
						JOptionPane.showMessageDialog(clawler_btn, "���ڷ� �Է��ϼ���");
						temp = null;
					}
				}
				
				if(temp!=null && howMany<1 || howMany>60) {
					JOptionPane.showMessageDialog(null, "1~60 ������ ���ڸ� �Է��ϼ���.");
				} else if (howMany>0 && howMany<=60){
					cc.start(howMany); // ũ�Ѹ� ����
//					db.connect();
//					db.creatTable();	 // SQL�� ���̺��� �����ΰ� ����
					db.db_clear();
					db.db_insert(cc.change_list, "������", howMany);
					db.db_insert(cc.amount_list, "�ŷ��ݾ�", howMany);
					
//					c.change_list �� c.amount_list �� �߰��� ������ �ִٸ� ũ�Ѹ� ����
					if (cc.amount_list.size() != 0 && cc.change_list.size() != 0) {
						JOptionPane.showMessageDialog(clawler_btn, "ũ�Ѹ��� �������ϴ�.");
					}
				}
			}
		});
		con.add(clawler_btn);

		
		
		chart_label = new JLabel("ǥ�� ������ Ŭ���ϼ���");
		chart_label.setForeground(Color.white);
		con.add(chart_label);
		
		//////////////////////////////////
		// 3-1. ������������ ǥ ���	//
		//////////////////////////////////
		
		change_chart_label = new JLabel("�庯������");
		change_chart_label.setForeground(Color.white);
		con.add(change_chart_label);
		
		change_chart_btn = new JButton("<html>&nbsp;&nbsp;���θ� & <br />��ȭ�ŷ��� ��սü�(��)</html>");
		change_chart_btn.setForeground(Color.WHITE);
		change_chart_btn.setBackground(new Color(252,155,58));
		change_chart_btn.setToolTipText("<html>&nbsp;&nbsp;�������� ���� ������<br />���� ������ ǥ�� ����մϴ�.</html>");
		change_chart_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (cc.amount_list.size() != 0) {
					new CoinJTable(cc.change_list, "������");
					// db.db_print("������");
				} else {
					JOptionPane.showMessageDialog(null, "���� ũ�Ѹ��� �����ϼ���.");
				}
			}
		});
		con.add(change_chart_btn);
		
		
		//////////////////////////////////
		// 3-2. �ŷ��ݾ׼����� ǥ ���	//
		//////////////////////////////////

		amount_chart_label = new JLabel("��ŷ��ݾ׼�");
		amount_chart_label.setForeground(Color.white);
		con.add(amount_chart_label);
		
		amount_chart_btn = new JButton("<html>&nbsp;&nbsp;���θ� & <br />��ȭ�ŷ��� ��սü�(��)</html>");
		amount_chart_btn.setForeground(Color.WHITE);
		amount_chart_btn.setBackground(new Color(234,129,53));
		amount_chart_btn.setToolTipText("<html>&nbsp;&nbsp;�ŷ��ݾ��� ���� ������<br />���� ������ ǥ�� ����մϴ�.</html>");
		amount_chart_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cc.amount_list.size() != 0) {
					new CoinJTable(cc.amount_list, "�ŷ��ݾ�");
					// db.db_print("�ŷ��ݾ�");
				} else {
					JOptionPane.showMessageDialog(null, "���� ũ�Ѹ��� �����ϼ���.");
				}
			}
		});
		con.add(amount_chart_btn);
		
		
		write_label1 = new JLabel("cvs ���Ϸ� �����Ϸ��� Ŭ���ϼ���.");
		write_label1.setForeground(Color.WHITE);
		con.add(write_label1);
		write_label2 = new JLabel("(lib ������ ����˴ϴ�.)");
		write_label2.setFont(new Font("Dialog", Font.PLAIN, 11));
		write_label2.setForeground(Color.WHITE);
		con.add(write_label2);
		
		
		//////////////////////////////////
		// 4-1. ���Ͽ� ���� - ������	//
		//////////////////////////////////
		
		change_write_btn = new JButton("<html>���Ͽ� ����<br />- ������ -</html>");
		change_write_btn.setForeground(Color.white);
		change_write_btn.setBackground(new Color(56,115,244));
		change_write_btn.setToolTipText("������ csv���Ϸ� �ۼ��˴ϴ�.");
		change_write_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cc.amount_list.size() != 0) {
					cc.change_list_bufferedwriter();
				} else {
					JOptionPane.showMessageDialog(null, "���� ũ�Ѹ��� �����ϼ���.");
				}
			}
		});
		con.add(change_write_btn);
		
		//////////////////////////////////
		// 4-2. ���Ͽ� ���� - �ŷ��ݾ�	//
		//////////////////////////////////
		
		amount_write_btn = new JButton("<html>���Ͽ� ����<br />- �ŷ��ݾ� -</html>");
		amount_write_btn.setForeground(Color.white);
		amount_write_btn.setBackground(new Color(68,56,247));
		amount_write_btn.setToolTipText("������ csv���Ϸ� �ۼ��˴ϴ�.");
		amount_write_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cc.amount_list.size() != 0) {
					cc.amount_list_bufferedwriter();
				} else {
					JOptionPane.showMessageDialog(null, "���� ũ�Ѹ��� �����ϼ���.");
				}
			}
		});
		con.add(amount_write_btn);
		
		
		//////////////////////////
		// 5. �˻���� �߰�		//
		//////////////////////////
		
		search_label = new JLabel("��ȭ�ŷ��� ��սü� �˻�");
		search_label.setForeground(Color.white);
		con.add(search_label);
		
		
		String [] lang = {"������", "�ŷ��ݾ�"};
		
		
		comb = new JComboBox(lang);
		comb.setToolTipText("�˻� ������ �����Ϸ��� Ŭ���ϼ���.");
		comb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JComboBox obj = (JComboBox) ae.getSource();
				idx = obj.getSelectedIndex();
			}
		});

		search_btn = new JButton("Ŭ��");
		search_btn.setToolTipText("�˻��Ϸ��� Ŭ���ϼ���.");
		search_btn.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cc.amount_list.size() != 0) {
					if(idx==0) {
						db.db_search("change_coin", cc.change_list, howMany);
					} else {
						db.db_search("amount_coin", cc.amount_list, howMany);
					}
				} else {
					JOptionPane.showMessageDialog(null, "���� ũ�Ѹ��� �����ϼ���.");
				}
			}
		} );
		con.add(comb);
		con.add(search_btn);

		
		//////////////////////////
		// 6. ������� �߰�		//
		//////////////////////////
		
		rand_btn = new JButton("�ڷ��� ���� ��õ��");
		rand_btn.setForeground(Color.white);
		rand_btn.setBackground(Color.BLACK);
		rand_btn.setToolTipText("<html>�����ǡ�<br />����� å������ �ʽ��ϴ�!!!!</html>");
		rand_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cc.amount_list.size() != 0) {
					db.db_rand(howMany);
				} else {
					JOptionPane.showMessageDialog(null, "���� ũ�Ѹ��� �����ϼ���.");
				}
			}
		});
		con.add(rand_btn);

		setVisible(true);
		
	} // Main() ��
	
	
	public static void main(String[] args) {
		CrawlerMain crawler = new CrawlerMain();
		Thread thread = new Thread(crawler);
		thread.start();
	} // main() ��


	@Override
	public void run() {
		while(true) {
//			ȭ�� ũ�⸦ �������� �� Jlabel�� Jbutton ���� ��ġ�� ����
			
			frame_w = this.getWidth();
			frame_h = this.getHeight()/6;
			image_label.setBounds(frame_w/2-100, 10, 200, 65);
			clawler_label.setBounds(frame_w/2-80, frame_h-20, 220, 50);
			chart_label.setBounds(frame_w/2-60, frame_h*2-40, 220, 50);
			change_chart_label.setBounds(frame_w/2-90, frame_h*2-20, 220, 50);
			amount_chart_label.setBounds(frame_w/2+20, frame_h*2-20, 220, 50);
			write_label1.setBounds(frame_w/2-85, frame_h*3-7, 220, 50);
			write_label2.setBounds(frame_w/2-50, frame_h*3+6, 220, 50);
			search_label.setBounds(frame_w/2-80, frame_h*4+10, 150, 50);
			
			clawler_btn.setBounds(frame_w/2-90, frame_h+20, 180, 40);
			change_chart_btn.setBounds(frame_w/2-110, frame_h*2+20, 110, 70);
			amount_chart_btn.setBounds(frame_w/2+10, frame_h*2+20, 110, 70);
			change_write_btn.setBounds(frame_w/2-110, frame_h*3+45, 110, 60);
			amount_write_btn.setBounds(frame_w/2+10, frame_h*3+45, 110, 60);
			search_btn.setBounds(frame_w/2+10, frame_h*4+50, 60, 30);
			rand_btn.setBounds(frame_w/2-80, frame_h*5+10, 150, 30);
			
			comb.setBounds(frame_w/2-80, frame_h*4+50, 80, 30);
			
			try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
		}
		
	}
} // class ��

