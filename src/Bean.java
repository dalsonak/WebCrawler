package com.koreais.crawler;


// ũ�Ѹ��� �������� ����� ���� Ŭ����

public class Bean {
	private int rank;
	private String coinName;
	private String avgPrice;
	
	public Bean(int rank, String coinName, String avgPrice) {
		this.rank = rank;
		this.coinName = coinName;
		this.avgPrice = avgPrice;
	}

	public int getRank() {
		return rank;
	}

	public String getCoinName() {
		return coinName;
	}

	public String getAvgPrice() {
		return avgPrice;
	}


	
}
