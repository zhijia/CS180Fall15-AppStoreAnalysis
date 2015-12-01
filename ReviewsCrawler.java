package Parser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ReviewsCrawler implements Runnable{

	int timeout = 20000;
	static String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
	String seed = "";
	String output_directory = "";
	String logfile = "";
	String homeURL = "https://play.google.com";
	int cycle = 2, seedID = 1;
	
	//private Map<String, Boolean> urlqueue = new ConcurrentHashMap<String, Boolean>();
	
	public ReviewsCrawler() {}
	
	public ReviewsCrawler(String seed) {
		this.seed = seed;
		createDir("crawledreviewsfiles");
		this.logfile = output_directory + "/logfile";
	}
	
	public void run() {
		getAppPage();
	}
	
	public void getAppPage() {
		Connection conn = null;
		String url=null;
		try{
			
			//conn = DriverManager.getConnection("jdbc:mysql://localhost/playstore","root","");
			conn=DriverManager.getConnection("jdbc:mysql://localhost/appstore?user=root&password=googleplay123");
			Statement smt = conn.createStatement();
			String query="SELECT * FROM appraw where review is null order by crawltime ";
			ResultSet rs_url = smt.executeQuery( query );
			
			while(rs_url.next()) {
				
				url = rs_url.getString("app_url");
			
				WebDriver driver = new FirefoxDriver();
				JavascriptExecutor js = (JavascriptExecutor) driver;
				
				driver.get(homeURL + url);
				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				
				WebElement expandButton = driver.findElement(By.className("expand-next"));
				int count=0;
				while(!expandButton.isEnabled()) {
					Thread.sleep(100);
				}
				/*while(expandButton.isEnabled() && count<=10) {
					Thread.sleep(10);
					expandButton.click();
					count++;
				}*/
				expandButton.click();
					//Object buttonObject = js.executeScript("var elements = document.getElementsByTagName('button'); var len = elements.length; while(len--) {if(elements[len].className == 'content id-view-permissions-details fake-link'){ elements[len].click();}}");
					//Thread.sleep(10000);
				WebDriverWait wait = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("expand-pages-container")));
			
				Object docObject = js.executeScript("return document.getElementsByTagName('html')[0].innerHTML");
				String docum = docObject.toString();
				Document doc = Jsoup.parse(docum, "UTF-8");
				
				int i=0;
				while(i<doc.select("div[class=single-review]").size())
				 {
					String authorName=null;
					String authorID=null;
					Date reviewDate=null;
					Double reviewRating=null;
					String reviewText=null;
					  Element s = doc.select("div[class=single-review]").get(i);
					  authorName=s.select("div[class=review-info]").select("span[class=author-name]").get(0).text();
					  System.out.println("Author Name: "+authorName);
					  Element devinfolnk=s.select("div[class=review-info]").select("span[class=author-name]").get(0);
					  String hrefs=devinfolnk.select("a").attr("href").trim();
					  if(hrefs.contains("id=")){
						  authorID=hrefs.split("id=")[1].trim();  
					  }
					  else{
						  authorID=null;
					  }
					  String tempdate=s.select("div[class=review-info]").select("span[class=review-date]").get(0).text().replaceAll("[,]", "");
					  reviewDate = new SimpleDateFormat("MMM dd yyyy").parse(tempdate);
					  System.out.println("review Date : "+reviewDate);
					  String[] temp=s.select("div[class=review-info]").select("div[class=tiny-star star-rating-non-editable-container]").attr("aria-label").trim().split(" ");
					  reviewRating=Double.parseDouble(temp[1]);
					  System.out.println(reviewRating);
					  if(i<doc.select("div[class=review-text]").size()){
						  reviewText=doc.select("div[class=review-text]").get(i).text();
						  System.out.println(reviewText);  
					  }
					  else if(i<doc.select("div[class=review-body]").size()){
						  reviewText=doc.select("div[class=review-body]").get(i).text();
						  System.out.println(reviewText);  
					  }
					PreparedStatement insertreview = conn.prepareStatement("INSERT appreview(app_id,rev_auth_name,review_date,review,ratingGiven,authorID) values (?, ?, ?,?,?,?)");
					insertreview.setString(1, url.split("=")[1].trim());
					insertreview.setString(2,authorName);
					//insertreview.setString(3,reviewDate);
					java.sql.Date sqlDate = new java.sql.Date(reviewDate.getTime());
					insertreview.setDate(3,sqlDate );
					insertreview.setString(4, reviewText);
					insertreview.setDouble(5, reviewRating);
					insertreview.setString(6,authorID);
					insertreview.executeUpdate();
					insertreview.close();
					 i++;
				 }
				driver.close();
				driver.quit();
				
					//Document doc = Jsoup.connect(homeURL+url).userAgent(userAgent).timeout(timeout).get();
				
				PreparedStatement updatereview=conn.prepareStatement("UPDATE `appraw` SET `review`=0 WHERE `appraw`.`app_url`=?");
				updatereview.setString(1, url);
				updatereview.executeUpdate();
				updatereview.close();
				System.out.println(url);

				String ouputfilename = getTime() + "_" + url.substring(url.indexOf("id=")+3);
				BufferedWriter htmlfilewrite = new BufferedWriter(new FileWriter(output_directory+"/" + ouputfilename + ".html", true));
				htmlfilewrite.append( docum );
				htmlfilewrite.close();
				
				writeLog(new Date() + " :: " + url, logfile);
				
			}
			smt.close();
			conn.close();
			
			return;
			
		} catch(Exception e) {
	
			e.printStackTrace();
			try {
				PreparedStatement updateFailure = conn.prepareStatement("UPDATE `appraw` SET `review`=1 WHERE `appraw`.`app_url`=?");
				updateFailure.setString(1, url);
				System.out.println("Failed URL :" + url);
				updateFailure.executeUpdate();
				updateFailure.close();
				conn.close();
				getAppPage();
			} catch(Exception ex) {
				ex.printStackTrace();
				return;
			}

		}

	}
	
	public void createDir(String output_directory) {

		this.output_directory = output_directory;
		File file = new File(output_directory);
		if ( !file.exists() ) {
			file.mkdir();
		}

	}

	public void writeLog(String logstr, String logfilepath) {

		BufferedWriter log;

		try {
			log = new BufferedWriter(new FileWriter(logfilepath, true));
			log.append(logstr);
			log.newLine();
			log.close();

		} catch (IOException ioe) {

			System.out.println("!!!!!an IOException happened while writing to log file");
			ioe.printStackTrace();
			return;
		}

	}
	
	public String getTime() {

		Date date = new Date();
		DateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
		return dateFormat.format(date);
	}

	public static void main(String[] args) {
		
		//System.setProperty("webdriver.chrome.driver", "C:\\Education\\4th Quarter\\Software Engineering\\PlayStoreAnalystics\\chromedriver_win32\\chromedriver.exe");
		
		String seed = "https://play.google.com/store/apps/collection/topselling_free";
		final int NTHREDS = 1; //Integer.parseInt(args[0]);
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
			executor.execute(  new ReviewsCrawler( seed ) );
			executor.shutdown();

		} 
		catch(Exception ee) {
			ee.printStackTrace();
			//System.exit(1);

		}

	}

}
