package com.koreais.crawler;

import java.awt.Container;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

public class CoinJTable extends JFrame {

	CoinJTable(ArrayList<Bean> list, String type) {
		setTitle(type + " ���� ����");
		setBounds(300, 300, 800, 800);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Container con = getContentPane();

		String[] title = { "����", "���θ�", "��ȭ�ŷ��� ��սü�(��)" };

		// TableModel : �� �� �� �ٷ� �� �ִ� ����� ���
		DefaultTableModel model = new DefaultTableModel(null, title);
		JTable table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);

		// ���̺� �����͸� �߰��ϴ� �ڵ� addRow
		for (int i = 0; i < list.size(); i++) {
			Bean b = list.get(i);

			String[] addData = new String[3];
			addData[0] = String.format("%d��", (b.getRank()));
			addData[1] = b.getCoinName();
			addData[2] = b.getAvgPrice();

			model.addRow(addData);
		}

		// ���� ��� �߰�
		table.setRowSorter(new TableRowSorter(model));

		con.add(sp);

		pack(); // �������� ������ ũ��� ����

		setVisible(true);
	}
}
