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
	
	JLabel image_label = null;			// 로고 label 
	JLabel clawler_label = null;		// 크롤링 label
	JLabel chart_label = null;			// 표 label
	JLabel change_chart_label = null;	// 변동률순 표 label
	JLabel amount_chart_label = null;	// 거래금액순 표 label
	JLabel write_label1 = null;			// 파일에 쓰기 label1
	JLabel write_label2 = null;			// 파일에 쓰기 label2
	JLabel search_label = null;			// 검색 label
	
	JButton clawler_btn = null;			// 크롤링 button
	JButton change_chart_btn = null;	// 변동률순 표 button
	JButton amount_chart_btn = null;	// 거래금액순 표 button
	JButton change_write_btn = null;	// 변동률 순 cvs파일 쓰기 button
	JButton amount_write_btn = null;	// 거래금액 순 cvs파일 쓰기 button
	JButton search_btn = null;			// 검색 button
	JButton rand_btn = null;			// 랜덤 button
	
	JComboBox comb = null;
	
	CoinCrawler cc = new CoinCrawler();
	SqliteDao db = new SqliteDao();
	
	public CrawlerMain() {
		// Frame 설정
		setTitle("코인 크롤러");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screen1 = new Dimension(); // 현재 해상도
		screen1 = Toolkit.getDefaultToolkit().getScreenSize();
		int xPos = (screen1.width / 2) - (frame_w / 2);
		int yPos = (screen1.height / 2) - (frame_h / 2);
		setBounds(xPos, yPos, frame_w, frame_h);

		Container con = getContentPane();
		con.setLayout(null);
		con.setBackground(Color.black);
		
		
		// 여기서부터 컴포넌트 부착
		
		
		//////////////////////////
		// 1. 이미지 추가		//
		//////////////////////////
		
		ImageIcon img = new ImageIcon("img/bithumb.png");
		image_label = new JLabel("", img , JLabel.CENTER);
		add(image_label);
		
		
		
		//////////////////////////
		// 2. 크롤링하기		//
		//////////////////////////
		
		clawler_label = new JLabel("크롤링 하려면 클릭하세요.");
		clawler_label.setForeground(Color.white);
		con.add(clawler_label);
		
		clawler_btn = new JButton("크롤링하기");
		clawler_btn.setForeground(Color.WHITE);
		clawler_btn.setBackground(new Color(224,74,51));
		clawler_btn.setToolTipText("https://www.bithumb.com/에서 크롤링을 시작합니다.");
		clawler_btn.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String temp = JOptionPane.showInputDialog(clawler_btn, "몇 위까지 출력할까요? (1~60)");
				try {
					howMany = Integer.parseInt(temp);
				} catch (Exception e2) { 
					if(temp==null) {
						JOptionPane.showMessageDialog(clawler_btn, "입력이 없습니다.");
					} else {
						JOptionPane.showMessageDialog(clawler_btn, "숫자로 입력하세요");
						temp = null;
					}
				}
				
				if(temp!=null && howMany<1 || howMany>60) {
					JOptionPane.showMessageDialog(null, "1~60 사이의 숫자를 입력하세요.");
				} else if (howMany>0 && howMany<=60){
					cc.start(howMany); // 크롤링 시작
//					db.connect();
//					db.creatTable();	 // SQL에 테이블을 만들어두고 시작
					db.db_clear();
					db.db_insert(cc.change_list, "변동률", howMany);
					db.db_insert(cc.amount_list, "거래금액", howMany);
					
//					c.change_list 랑 c.amount_list 에 추가된 내용이 있다면 크롤링 성공
					if (cc.amount_list.size() != 0 && cc.change_list.size() != 0) {
						JOptionPane.showMessageDialog(clawler_btn, "크롤링이 끝났습니다.");
					}
				}
			}
		});
		con.add(clawler_btn);

		
		
		chart_label = new JLabel("표를 보려면 클릭하세요");
		chart_label.setForeground(Color.white);
		con.add(chart_label);
		
		//////////////////////////////////
		// 3-1. 변동률순으로 표 출력	//
		//////////////////////////////////
		
		change_chart_label = new JLabel("▼변동률순");
		change_chart_label.setForeground(Color.white);
		con.add(change_chart_label);
		
		change_chart_btn = new JButton("<html>&nbsp;&nbsp;코인명 & <br />원화거래소 평균시세(원)</html>");
		change_chart_btn.setForeground(Color.WHITE);
		change_chart_btn.setBackground(new Color(252,155,58));
		change_chart_btn.setToolTipText("<html>&nbsp;&nbsp;변동률이 높은 순서로<br />코인 정보를 표로 출력합니다.</html>");
		change_chart_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (cc.amount_list.size() != 0) {
					new CoinJTable(cc.change_list, "변동률");
					// db.db_print("변동률");
				} else {
					JOptionPane.showMessageDialog(null, "먼저 크롤링을 시작하세요.");
				}
			}
		});
		con.add(change_chart_btn);
		
		
		//////////////////////////////////
		// 3-2. 거래금액순으로 표 출력	//
		//////////////////////////////////

		amount_chart_label = new JLabel("▼거래금액순");
		amount_chart_label.setForeground(Color.white);
		con.add(amount_chart_label);
		
		amount_chart_btn = new JButton("<html>&nbsp;&nbsp;코인명 & <br />원화거래소 평균시세(원)</html>");
		amount_chart_btn.setForeground(Color.WHITE);
		amount_chart_btn.setBackground(new Color(234,129,53));
		amount_chart_btn.setToolTipText("<html>&nbsp;&nbsp;거래금액이 많은 순서로<br />코인 정보를 표로 출력합니다.</html>");
		amount_chart_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cc.amount_list.size() != 0) {
					new CoinJTable(cc.amount_list, "거래금액");
					// db.db_print("거래금액");
				} else {
					JOptionPane.showMessageDialog(null, "먼저 크롤링을 시작하세요.");
				}
			}
		});
		con.add(amount_chart_btn);
		
		
		write_label1 = new JLabel("cvs 파일로 저장하려면 클릭하세요.");
		write_label1.setForeground(Color.WHITE);
		con.add(write_label1);
		write_label2 = new JLabel("(lib 폴더에 저장됩니다.)");
		write_label2.setFont(new Font("Dialog", Font.PLAIN, 11));
		write_label2.setForeground(Color.WHITE);
		con.add(write_label2);
		
		
		//////////////////////////////////
		// 4-1. 파일에 쓰기 - 변동률	//
		//////////////////////////////////
		
		change_write_btn = new JButton("<html>파일에 쓰기<br />- 변동률 -</html>");
		change_write_btn.setForeground(Color.white);
		change_write_btn.setBackground(new Color(56,115,244));
		change_write_btn.setToolTipText("정보가 csv파일로 작성됩니다.");
		change_write_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cc.amount_list.size() != 0) {
					cc.change_list_bufferedwriter();
				} else {
					JOptionPane.showMessageDialog(null, "먼저 크롤링을 시작하세요.");
				}
			}
		});
		con.add(change_write_btn);
		
		//////////////////////////////////
		// 4-2. 파일에 쓰기 - 거래금액	//
		//////////////////////////////////
		
		amount_write_btn = new JButton("<html>파일에 쓰기<br />- 거래금액 -</html>");
		amount_write_btn.setForeground(Color.white);
		amount_write_btn.setBackground(new Color(68,56,247));
		amount_write_btn.setToolTipText("정보가 csv파일로 작성됩니다.");
		amount_write_btn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cc.amount_list.size() != 0) {
					cc.amount_list_bufferedwriter();
				} else {
					JOptionPane.showMessageDialog(null, "먼저 크롤링을 시작하세요.");
				}
			}
		});
		con.add(amount_write_btn);
		
		
		//////////////////////////
		// 5. 검색기능 추가		//
		//////////////////////////
		
		search_label = new JLabel("원화거래소 평균시세 검색");
		search_label.setForeground(Color.white);
		con.add(search_label);
		
		
		String [] lang = {"변동률", "거래금액"};
		
		
		comb = new JComboBox(lang);
		comb.setToolTipText("검색 기준을 변경하려면 클릭하세요.");
		comb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JComboBox obj = (JComboBox) ae.getSource();
				idx = obj.getSelectedIndex();
			}
		});

		search_btn = new JButton("클릭");
		search_btn.setToolTipText("검색하려면 클릭하세요.");
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
					JOptionPane.showMessageDialog(null, "먼저 크롤링을 시작하세요.");
				}
			}
		} );
		con.add(comb);
		con.add(search_btn);

		
		//////////////////////////
		// 6. 랜덤기능 추가		//
		//////////////////////////
		
		rand_btn = new JButton("★랜덤 코인 추천★");
		rand_btn.setForeground(Color.white);
		rand_btn.setBackground(Color.BLACK);
		rand_btn.setToolTipText("<html>★주의★<br />결과에 책임지지 않습니다!!!!</html>");
		rand_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cc.amount_list.size() != 0) {
					db.db_rand(howMany);
				} else {
					JOptionPane.showMessageDialog(null, "먼저 크롤링을 시작하세요.");
				}
			}
		});
		con.add(rand_btn);

		setVisible(true);
		
	} // Main() 끝
	
	
	public static void main(String[] args) {
		CrawlerMain crawler = new CrawlerMain();
		Thread thread = new Thread(crawler);
		thread.start();
	} // main() 끝


	@Override
	public void run() {
		while(true) {
//			화면 크기를 조정했을 때 Jlabel과 Jbutton 등의 위치를 조정
			
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
} // class 끝

