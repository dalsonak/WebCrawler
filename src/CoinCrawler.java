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
		 * 1. 크롤링 시작
		 * 2. 변동률 높은순과 거래금액 많은 순으로 만들어진 코인 표 출력 
		 *	  (표에서 변동률/거래금액순 으로 정렬기능 추가)
		 * 3. 변동률 높은 순으로 코인 출력
		 * 4. 거래금액 많은 순으로 코인 출력
		 * 5. 원하는 코인의 원화거래소 평균가격(원) 검색 버튼
		 */
		
		String projectPath = System.getProperty("user.dir"); // 현재 프로젝트 폴더
		String chromePath = projectPath + "\\lib\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", chromePath);
		
		
		WebDriver driver = new ChromeDriver(); // driver라는 변수의 인스턴스 생성 - 크롬창 띄우기
		
		driver.get("https://www.bithumb.com"); // url 이동
		Document doc = Jsoup.parse(driver.getPageSource()); // 페이지 소스 가져오기
		
		try { Thread.sleep(500);}
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// 이벤트 버튼 닫기 (https://www.bithumb.com 을 띄웠을 때 이벤트 창이 뜨는 경우)
//		WebElement x_button = driver.findElement( By.xpath("//*[@id=\"layerBanner_become\"]/div[2]/div/div"));
//		x_button.click();
//		try { Thread.sleep(500); }
//		catch (InterruptedException e) { e.printStackTrace(); }
		
		
		// 변동률 정렬
		WebElement sort_change = driver.findElement( By.xpath("//*[@id=\"tableAsset\"]/thead/tr/th[4]/span"));
		sort_change.click();
		getlist(driver, change_list, "변동률", howMany);
		
		
		//거래금액 정렬
		WebElement sort_amount = driver.findElement( By.xpath("//*[@id=\"tableAsset\"]/thead/tr/th[5]/span"));
		sort_amount.click();
		getlist(driver, amount_list, "거래금액", howMany);
		
		driver.close();

	} // start() 끝

	// main_driver은 main의 driver
	void getlist(WebDriver main_driver, ArrayList<Bean> list, String type, int howMany) { 
		Document doc = Jsoup.parse(main_driver.getPageSource());
		Elements tr = doc.select("table.table_st1 tbody tr");
		Elements coin2 = new Elements();

//		테이블 내용을 가져온다
		for (int i = 0; i < tr.size(); i++) {
			if (tr.get(i).attr("class").equals("coin_list")) {
				continue;
			}
			coin2.add(tr.get(i));
		}
		
		WebDriver driver = new ChromeDriver(); // driver라는 변수의 인스턴스 생성 - 크롬창 띄우기
		
//		System.out.println(type + "의 코인 정보");	// 확인용
		
		for (int i = 0; i < howMany; i++) { // ArrayList에 각 제품 정보를 추가
			Element coin = coin2.get(i);
			
			String coin_name = coin.select("span.blind").text();
//			코인명이 비트코인 / ㅂㅌㅋㅇ / BTC로 나뉘므로 3개 짜리 배열에
//			영문자 앞을 반으로 나눈 문자열중 앞 1개와 영문자이후 문자열을 각각 저장한다.
			String[] coin_names = new String[2];
			int eng = 0;
			for (int j = 0; j < coin_name.length(); j++) {
				if(getType(coin_name.charAt(j)+"")){
					eng = j;
					coin_names[1] = coin_name.substring(j);	// 영문 코인명
					break;
				}
			}
			coin_names[0] = coin_name.substring(0, (eng+1)/2);	// 한글 코인명
			
			
			String price_avg = "";
			
//			해당 코인의 보조지표로 이동
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
//			확인용
//			System.out.println("순위 : " + (i+1));
//			System.out.println("코인명 : " + coin_names[0]);
//			System.out.println("평균가격 : " + price_avg);
			
		}
//		System.out.println("===============");
		driver.close();
		
	} // getlist() 끝
	
	
	
	//////////////////////////////////////////////////
	// 파일에 데이터 읽고 쓰기 (BufferedWriter) - change_list
	void change_list_bufferedwriter() {

		String projectPath = System.getProperty("user.dir");
		String csvPath = projectPath + "\\lib\\bithumb_change_list.csv";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath));

			// 첫 줄은 각 항목의 제목
			writer.write("rank,name,price\n");

			// 두 번째 줄 부터는 각 제품의 정보를 쓰기
			for (int i = 0; i < change_list.size(); i++) {
				Bean p = change_list.get(i);
				writer.write(String.format("%d,%s,%s", p.getRank(), p.getCoinName(), p.getAvgPrice().replaceAll( "," , "" )));
				writer.newLine(); // 파일에 개행 추가. \n 사용한것과 같다.
			} // for문 끝

			writer.close();
		} catch (IOException e) { e.printStackTrace(); }

		// 파일에 써진 데이터 읽어오기
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(csvPath)); // 파일을 못 읽을수 있다는 가정 -> try-catch
//			String data; // 파일에 서 데이터를 읽어올 변수
//
//			ArrayList<Bean> p_list1 = new ArrayList<>();
//
//			try {
//				while ((data = reader.readLine()) != null) {
//					System.out.println(data);
//					String[] arr = data.split(",");
//
//					String rank = arr[0]; // 가져올 데이터는 int 읽은 데이터는 string -> Integer.parseInt(page)
//
//					if (rank.equals("rank")) { // 첫 줄일 때 pass
//						continue;
//					}
//					String name = arr[1];
//					String price = arr[2];
//
//					p_list1.add(new Bean(Integer.parseInt(rank), name, price));
//
//				} // while문 끝
//
//				reader.close();
//				System.out.println("파일에서 읽은 코인 개수: " + p_list1.size());
//
//			} catch (IOException e) { e.printStackTrace(); }
//
//		} catch (FileNotFoundException e) {	e.printStackTrace(); }
	}
	
	
	
	//////////////////////////////////////////////////
	// 파일에 데이터 읽고 쓰기 (BufferedWriter) - amount_list
	void amount_list_bufferedwriter() {

		String projectPath = System.getProperty("user.dir");
		String csvPath = projectPath + "\\lib\\bithumb_amount_list.csv";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath));

			// 첫 줄은 각 항목의 제목
			writer.write("rank,name,price\n");

			// 두 번째 줄 부터는 각 제품의 정보를 쓰기
			for (int i = 0; i < amount_list.size(); i++) {
				Bean p = amount_list.get(i);
				writer.write(String.format("%d,%s,%s", p.getRank(), p.getCoinName(), p.getAvgPrice().replaceAll( "," , "" )));
				writer.newLine(); // 파일에 개행 추가.
			} // for문 끝

			writer.close();
		} catch (IOException e) { e.printStackTrace(); }

		// 파일에 써진 데이터 읽어오기
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(csvPath)); // 파일을 못 읽을수 있다는 가정 -> try-catch
//			String data; // 파일에 서 데이터를 읽어올 변수
//
//			ArrayList<Bean> p_list2 = new ArrayList<>();
//
//			try {
//				while ((data = reader.readLine()) != null) {
//					System.out.println(data);
//					String[] arr = data.split(",");
//
//					String rank = arr[0]; // 가져올 데이터는 int 읽은 데이터는 string -> Integer.parseInt(page)
//
//					if (rank.equals("rank")) { // 첫 줄일 때 pass
//						continue;
//					}
//					String name = arr[1];
//					String price = arr[2];
//
//					p_list2.add(new Bean(Integer.parseInt(rank), name, price));
//
//				} // while문 끝
//
//				reader.close();
//				System.out.println("파일에서 읽은 코인 개수: " + p_list2.size());
//
//			} catch (IOException e) { e.printStackTrace(); }
//
//		} catch (FileNotFoundException e) {	e.printStackTrace(); }
	}
	
//	글자가 영어인지 확인
    public static boolean getType(String word) {
        return Pattern.matches("^[a-zA-Z]*$", word);
    }
	
}
