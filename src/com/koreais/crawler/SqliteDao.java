package com.koreais.crawler;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqliteDao {
	Connection conn;
	
	// ������
	SqliteDao() {
		connect();
	}
	
	// DB����
	void connect() {
		String projectPath 	= System.getProperty("user.dir");
		String dbPath = projectPath + "\\lib\\coin_crawler.db";

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

		} catch (SQLException e) { e.printStackTrace(); }
	}

	// ���̺� �����
	void creatTable() {

		try {
			Statement s = conn.createStatement();				
			s.execute("CREATE TABLE change_coin( rank INTEGER , name TEXT , price INTEGER )");
			s.execute("CREATE TABLE amount_coin( rank INTEGER , name TEXT , price INTEGER )");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// ���� �ֱ�
	void db_insert( ArrayList<Bean> list, String type, int howMany) {
		String table_name = "";
		
		if(type.equals("������")) {
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
				ps.executeUpdate(); // ������ ����
			}
			ps.close();
			
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	// ���� ��� (Ȯ�ο�)
	void db_print(String type)  {
		String table_name = "";
		
		if(type.equals("������")) {
			table_name = "change_coin";
		}
		else {
			table_name = "amount_coin";
		}
		try {
			Statement s = conn.createStatement();
			ResultSet r = s.executeQuery("SELECT * FROM " + table_name);
			while (r.next()) {
				System.out.println("���� : " + r.getInt("rank"));
				System.out.println("���θ� : " + r.getString("name"));
				System.out.println("��ȭ�ŷ��� ��սü� : " + r.getString("price"));
			}
			s.close();
			r.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	// ����� ���� Clear
	void db_clear() {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("DELETE FROM change_coin");
			ps.executeUpdate(); // �������� ����
			ps = conn.prepareStatement("DELETE FROM amount_coin");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	// �˻����
	void db_search(String table, ArrayList<Bean> list, int howMany) {
		
		// ���θ��� �Է��ϼ��� (�Է� ������ �����̸��� ���)
		try {
			Statement s = conn.createStatement();
			String help = "���� : ";
			
			ResultSet rs = s.executeQuery("SELECT name FROM " + table);
			if(rs.next()) {
				help += rs.getString("name");
				
				while( rs.next() ) {
					help += ", ";
					help += rs.getString("name");
				}
			}
			
			String search = JOptionPane.showInputDialog(null, "���θ��� �Է��ϼ���.\n" + help );
			
//			�˻��� ���θ��� �����ֱ� ���� ���θ��� �迭�� �ִ´�.
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
			
//			�Է��� �߸� �Ǿ��� �� �޽��� ����ֱ�
			if(search!=null && flag == true) {
				ResultSet r = s.executeQuery("SELECT * FROM " + table +" WHERE name = \"" + search + "\"");
				JOptionPane.showMessageDialog(null, "��ȭ�ŷ��� ��սü� : " + r.getString("price"));
			} else if(search!=null && flag == false){
				JOptionPane.showMessageDialog(null, "���� �̸��� Ʋ���ϴ�.");
			} else if(search == null) {
				JOptionPane.showMessageDialog(null, "�Է��� �����ϴ�.");
			}
			
			s.close();
			rs.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
//	����
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
