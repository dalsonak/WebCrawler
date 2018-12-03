package com.koreais.crawler;


// 크롤링한 정보들을 멤버로 가진 클래스

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
