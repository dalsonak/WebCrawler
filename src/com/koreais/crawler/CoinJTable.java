package com.koreais.crawler;

import java.awt.Container;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

public class CoinJTable extends JFrame {

	CoinJTable(ArrayList<Bean> list, String type) {
		setTitle(type + " 코인 정보");
		setBounds(300, 300, 800, 800);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Container con = getContentPane();

		String[] title = { "순위", "코인명", "원화거래소 평균시세(원)" };

		// TableModel : 한 줄 씩 다룰 수 있는 기능을 사용
		DefaultTableModel model = new DefaultTableModel(null, title);
		JTable table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);

		// 테이블에 데이터를 추가하는 코드 addRow
		for (int i = 0; i < list.size(); i++) {
			Bean b = list.get(i);

			String[] addData = new String[3];
			addData[0] = String.format("%d위", (b.getRank()));
			addData[1] = b.getCoinName();
			addData[2] = b.getAvgPrice();

			model.addRow(addData);
		}

		// 정렬 기능 추가
		table.setRowSorter(new TableRowSorter(model));

		con.add(sp);

		pack(); // 프레임을 적절한 크기로 조절

		setVisible(true);
	}
}
