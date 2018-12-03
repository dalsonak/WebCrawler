package com.koreais.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class CoinCrawler { 
	
	ArrayList<Bean> change_list = new ArrayList<>();
	ArrayList<Bean> amount_list = new ArrayList<>();
	
	void start(int howMany) {
		 /* 
		 * 1. ũ�Ѹ� ����
		 * 2. ������ �������� �ŷ��ݾ� ���� ������ ������� ���� ǥ ��� 
		 *	  (ǥ���� ������/�ŷ��ݾ׼� ���� ���ı�� �߰�)
		 * 3. ������ ���� ������ ���� ���
		 * 4. �ŷ��ݾ� ���� ������ ���� ���
		 * 5. ���ϴ� ������ ��ȭ�ŷ��� ��հ���(��) �˻� ��ư
		 */
		
		String projectPath = System.getProperty("user.dir"); // ���� ������Ʈ ����
		String chromePath = projectPath + "\\lib\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", chromePath);
		
		
		WebDriver driver = new ChromeDriver(); // driver��� ������ �ν��Ͻ� ���� - ũ��â ����
		
		driver.get("https://www.bithumb.com"); // url �̵�
		Document doc = Jsoup.parse(driver.getPageSource()); // ������ �ҽ� ��������
		
		try { Thread.sleep(500);}
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// �̺�Ʈ ��ư �ݱ� (https://www.bithumb.com �� ����� �� �̺�Ʈ â�� �ߴ� ���)
//		WebElement x_button = driver.findElement( By.xpath("//*[@id=\"layerBanner_become\"]/div[2]/div/div"));
//		x_button.click();
//		try { Thread.sleep(500); }
//		catch (InterruptedException e) { e.printStackTrace(); }
		
		
		// ������ ����
		WebElement sort_change = driver.findElement( By.xpath("//*[@id=\"tableAsset\"]/thead/tr/th[4]/span"));
		sort_change.click();
		getlist(driver, change_list, "������", howMany);
		
		
		//�ŷ��ݾ� ����
		WebElement sort_amount = driver.findElement( By.xpath("//*[@id=\"tableAsset\"]/thead/tr/th[5]/span"));
		sort_amount.click();
		getlist(driver, amount_list, "�ŷ��ݾ�", howMany);
		
		driver.close();

	} // start() ��

	// main_driver�� main�� driver
	void getlist(WebDriver main_driver, ArrayList<Bean> list, String type, int howMany) { 
		Document doc = Jsoup.parse(main_driver.getPageSource());
		Elements tr = doc.select("table.table_st1 tbody tr");
		Elements coin2 = new Elements();

//		���̺� ������ �����´�
		for (int i = 0; i < tr.size(); i++) {
			if (tr.get(i).attr("class").equals("coin_list")) {
				continue;
			}
			coin2.add(tr.get(i));
		}
		
		WebDriver driver = new ChromeDriver(); // driver��� ������ �ν��Ͻ� ���� - ũ��â ����
		
//		System.out.println(type + "�� ���� ����");	// Ȯ�ο�
		
		for (int i = 0; i < howMany; i++) { // ArrayList�� �� ��ǰ ������ �߰�
			Element coin = coin2.get(i);
			
			String coin_name = coin.select("span.blind").text();
//			���θ��� ��Ʈ���� / �������� / BTC�� �����Ƿ� 3�� ¥�� �迭��
//			������ ���� ������ ���� ���ڿ��� �� 1���� ���������� ���ڿ��� ���� �����Ѵ�.
			String[] coin_names = new String[2];
			int eng = 0;
			for (int j = 0; j < coin_name.length(); j++) {
				if(getType(coin_name.charAt(j)+"")){
					eng = j;
					coin_names[1] = coin_name.substring(j);	// ���� ���θ�
					break;
				}
			}
			coin_names[0] = coin_name.substring(0, (eng+1)/2);	// �ѱ� ���θ�
			
			
			String price_avg = "";
			
//			�ش� ������ ������ǥ�� �̵�
			driver.get("http://index.bithumb.com/coinsdaq/index.php?coin=" + coin_names[1]);

			try {
				Thread.sleep(500);
				WebElement targetMarket = driver.findElement( By.xpath("//*[@id=\"targetMarket\"]/option[2]"));
				targetMarket.click();
				Thread.sleep(500);
			} catch (InterruptedException e) { e.printStackTrace(); }

			
			Document doc2 = Jsoup.parse(driver.getPageSource());
			Elements price_avg1 = doc2.select("div.global_market_price_wrap");
			Elements price_avg2 = price_avg1.select("div.global_market_price.datagroup-priceSummary.MARKET p.price.data-priceText");
			price_avg = price_avg2.text();
			list.add( new Bean( (i+1), coin_names[0], price_avg) );
//			Ȯ�ο�
//			System.out.println("���� : " + (i+1));
//			System.out.println("���θ� : " + coin_names[0]);
//			System.out.println("��հ��� : " + price_avg);
			
		}
//		System.out.println("===============");
		driver.close();
		
	} // getlist() ��
	
	
	
	//////////////////////////////////////////////////
	// ���Ͽ� ������ �а� ���� (BufferedWriter) - change_list
	void change_list_bufferedwriter() {

		String projectPath = System.getProperty("user.dir");
		String csvPath = projectPath + "\\lib\\bithumb_change_list.csv";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath));

			// ù ���� �� �׸��� ����
			writer.write("rank,name,price\n");

			// �� ��° �� ���ʹ� �� ��ǰ�� ������ ����
			for (int i = 0; i < change_list.size(); i++) {
				Bean p = change_list.get(i);
				writer.write(String.format("%d,%s,%s", p.getRank(), p.getCoinName(), p.getAvgPrice().replaceAll( "," , "" )));
				writer.newLine(); // ���Ͽ� ���� �߰�. \n ����ѰͰ� ����.
			} // for�� ��

			writer.close();
		} catch (IOException e) { e.printStackTrace(); }

		// ���Ͽ� ���� ������ �о����
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(csvPath)); // ������ �� ������ �ִٴ� ���� -> try-catch
//			String data; // ���Ͽ� �� �����͸� �о�� ����
//
//			ArrayList<Bean> p_list1 = new ArrayList<>();
//
//			try {
//				while ((data = reader.readLine()) != null) {
//					System.out.println(data);
//					String[] arr = data.split(",");
//
//					String rank = arr[0]; // ������ �����ʹ� int ���� �����ʹ� string -> Integer.parseInt(page)
//
//					if (rank.equals("rank")) { // ù ���� �� pass
//						continue;
//					}
//					String name = arr[1];
//					String price = arr[2];
//
//					p_list1.add(new Bean(Integer.parseInt(rank), name, price));
//
//				} // while�� ��
//
//				reader.close();
//				System.out.println("���Ͽ��� ���� ���� ����: " + p_list1.size());
//
//			} catch (IOException e) { e.printStackTrace(); }
//
//		} catch (FileNotFoundException e) {	e.printStackTrace(); }
	}
	
	
	
	//////////////////////////////////////////////////
	// ���Ͽ� ������ �а� ���� (BufferedWriter) - amount_list
	void amount_list_bufferedwriter() {

		String projectPath = System.getProperty("user.dir");
		String csvPath = projectPath + "\\lib\\bithumb_amount_list.csv";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath));

			// ù ���� �� �׸��� ����
			writer.write("rank,name,price\n");

			// �� ��° �� ���ʹ� �� ��ǰ�� ������ ����
			for (int i = 0; i < amount_list.size(); i++) {
				Bean p = amount_list.get(i);
				writer.write(String.format("%d,%s,%s", p.getRank(), p.getCoinName(), p.getAvgPrice().replaceAll( "," , "" )));
				writer.newLine(); // ���Ͽ� ���� �߰�.
			} // for�� ��

			writer.close();
		} catch (IOException e) { e.printStackTrace(); }

		// ���Ͽ� ���� ������ �о����
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(csvPath)); // ������ �� ������ �ִٴ� ���� -> try-catch
//			String data; // ���Ͽ� �� �����͸� �о�� ����
//
//			ArrayList<Bean> p_list2 = new ArrayList<>();
//
//			try {
//				while ((data = reader.readLine()) != null) {
//					System.out.println(data);
//					String[] arr = data.split(",");
//
//					String rank = arr[0]; // ������ �����ʹ� int ���� �����ʹ� string -> Integer.parseInt(page)
//
//					if (rank.equals("rank")) { // ù ���� �� pass
//						continue;
//					}
//					String name = arr[1];
//					String price = arr[2];
//
//					p_list2.add(new Bean(Integer.parseInt(rank), name, price));
//
//				} // while�� ��
//
//				reader.close();
//				System.out.println("���Ͽ��� ���� ���� ����: " + p_list2.size());
//
//			} catch (IOException e) { e.printStackTrace(); }
//
//		} catch (FileNotFoundException e) {	e.printStackTrace(); }
	}
	
//	���ڰ� �������� Ȯ��
    public static boolean getType(String word) {
        return Pattern.matches("^[a-zA-Z]*$", word);
    }
	
}
