package com.koreais.crawler;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqliteDao {
	Connection conn;
	
	// 생성자
	SqliteDao() {
		connect();
	}
	
	// DB연결
	void connect() {
		String projectPath 	= System.getProperty("user.dir");
		String dbPath = projectPath + "\\lib\\coin_crawler.db";

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

		} catch (SQLException e) { e.printStackTrace(); }
	}

	// 테이블 만들기
	void creatTable() {

		try {
			Statement s = conn.createStatement();				
			s.execute("CREATE TABLE change_coin( rank INTEGER , name TEXT , price INTEGER )");
			s.execute("CREATE TABLE amount_coin( rank INTEGER , name TEXT , price INTEGER )");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 정보 넣기
	void db_insert( ArrayList<Bean> list, String type, int howMany) {
		String table_name = "";
		
		if(type.equals("변동률")) {
			table_name = "change_coin";
		}
		else {
			table_name = "amount_coin";
		}
		
		String query = "INSERT INTO " + table_name + " (rank, name, price) values (?,?,?)";
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(query);
			for (int i = 0; i < howMany; i++) {
				Bean b = list.get(i);
				ps.setInt(1, b.getRank());
				ps.setString(2, b.getCoinName());
				ps.setString(3, b.getAvgPrice());
				ps.executeUpdate(); // 쿼리문 수행
			}
			ps.close();
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	// 정보 출력 (확인용)
	void db_print(String type)  {
		String table_name = "";
		
		if(type.equals("변동률")) {
			table_name = "change_coin";
		}
		else {
			table_name = "amount_coin";
		}
		try {
			Statement s = conn.createStatement();
			ResultSet r = s.executeQuery("SELECT * FROM " + table_name);
			while (r.next()) {
				System.out.println("순위 : " + r.getInt("rank"));
				System.out.println("코인명 : " + r.getString("name"));
				System.out.println("원화거래소 평균시세 : " + r.getString("price"));
			}
			s.close();
			r.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	// 재시작 전에 Clear
	void db_clear() {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("DELETE FROM change_coin");
			ps.executeUpdate(); // 쿼리문을 수행
			ps = conn.prepareStatement("DELETE FROM amount_coin");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	// 검색기능
	void db_search(String table, ArrayList<Bean> list, int howMany) {
		
		// 코인명을 입력하세요 (입력 가능한 코인이름을 출력)
		try {
			Statement s = conn.createStatement();
			String help = "코인 : ";
			
			ResultSet rs = s.executeQuery("SELECT name FROM " + table);
			if(rs.next()) {
				help += rs.getString("name");
				
				while( rs.next() ) {
					help += ", ";
					help += rs.getString("name");
				}
			}
			
			String search = JOptionPane.showInputDialog(null, "코인명을 입력하세요.\n" + help );
			
//			검색시 코인명을 보여주기 위해 코인명을 배열에 넣는다.
			String[] arr = new String[howMany];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = list.get(i).getCoinName();
			}
			
			boolean flag = false;
			for (int i = 0; i < arr.length; i++) {
				if(arr[i].equals(search)) {
					flag = true;
				}
			}
			
//			입력이 잘못 되었을 때 메시지 띄워주기
			if(search!=null && flag == true) {
				ResultSet r = s.executeQuery("SELECT * FROM " + table +" WHERE name = \"" + search + "\"");
				JOptionPane.showMessageDialog(null, "원화거래소 평균시세 : " + r.getString("price"));
			} else if(search!=null && flag == false){
				JOptionPane.showMessageDialog(null, "코인 이름이 틀립니다.");
			} else if(search == null) {
				JOptionPane.showMessageDialog(null, "입력이 없습니다.");
			}
			
			s.close();
			rs.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
//	랜덤
	void db_rand(int howMany) {
		int answer = (int)(Math.random() * howMany) + 1;
		int where = (int)(Math.random() * 2);
		try {
			Statement s = conn.createStatement();
			ResultSet r = null;
			if(where==0) {
				r = s.executeQuery("SELECT * FROM change_coin WHERE rank = \"" + answer + "\"");
			} else {
				r = s.executeQuery("SELECT * FROM amount_coin WHERE rank = \"" + answer + "\"");
			}
			JOptionPane.showMessageDialog(null, r.getString("name"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
