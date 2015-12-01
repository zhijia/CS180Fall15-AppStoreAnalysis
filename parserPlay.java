package Parser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;









/*
 * The imports are for log4j  
 */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * The imports are for jsoup  
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class parserPlay 
{
	
	 //static Logger log = Logger.getLogger("hotpadsRent"); // making an object of logger which writes it to files
	
	public static void main(String args[])throws IOException, SQLException, JSONException, InterruptedException  // the main metho
	{
       
		//PropertyConfigurator.configure("log4j.properties");  // for the properties file which is there in the project
		Connection conn=null;
		Statement stmt=null;
		
		
		//File input = new File("3.txt");     
		try
		{
			//database connection
			//conn=DriverManager.getConnection("jdbc:mysql://localhost/playstore","root","");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/playstore","root","");
			//conn=DriverManager.getConnection("jdbc:mysql://localhost/appstore?user=root&password=googleplay123");
			stmt=conn.createStatement();
			String query="SELECT * FROM appraw order by crawltime ";
			//String query="SELECT * FROM appraw WHERE `appraw`.`app_page` is not null and `appraw`.`app_url` NOT IN ('/store/apps/details?id=com.bowengames.farmingusa') ORDER BY `app_url` ASC";
			ResultSet rs=stmt.executeQuery(query);
			//Iterating through records
			while(rs.next()){

				//variables for parsing
				int seed_id=0;
				String app_url=null;
				String app_id=null;
				String appname=null;
				Date crawledDate=null;
				String app_page=null;
			    long peopleRated=0;
			    StringBuilder category=new StringBuilder();
			    String version=null;
			    double appsize=0.0;
				double numberOfStars=0.0;
				String desc =null;
				String Developerinfo=null;
				Date lastupdated=null;
				long[] numofratings=new long[5];
				String numofInstalls=null;
				long numinstallsupperbound=0;
				long numinstallslowerbound=0;
				String OSreq=null;
				String offeredBy=null;
				double cost=0.0;
				String inapp_cost=null;
				StringBuilder whatsNew=new StringBuilder();
				
				
				seed_id=rs.getInt("seed_id");
				app_url=rs.getString("app_url");
				app_page=rs.getString("app_page");
				crawledDate=rs.getTimestamp("crawltime");
				if(!(app_page==null)){
				//Parsing the html page using Jsoup
				Document doc = Jsoup.parse(app_page, "UTF-8");
			    
				
				//app_id=app_url.substring(app_url.indexOf('?')+3);
				app_id=rs.getString("appid");
				//App name
				    Elements appnameele=doc.select("h1[class=document-title]");
				    if(appnameele.size()>0){
				    	appname=appnameele.get(0).text();
				    }
				    //  rating-count
				    
				    Elements metalinks = doc.select("span[class=rating-count]"); 
				  //  System.out.println(metalinks);
				    //cost of the app
				    Elements costlink=doc.select("button[class=price buy id-track-click]");
				    if(costlink.size()>0){
				    	int i=0;
			    		Element costlnk=costlink.get(0);
			    		if(costlnk.text().contains("Buy")){
			    			cost=Double.parseDouble(costlnk.text().trim().split(" ")[0].substring(1));
			    		}
			    		i++;
				    }
				    //Number of people given rating
				    String[] temp= metalinks.first().attr("aria-label").trim().split(" ");
				    peopleRated = Long.parseLong(temp[0].replaceAll("[^0-9]", ""));
				    //Category of the app
				    Elements categories=doc.select("a[class=document-subtitle category]");
				    if(categories.size()>0){
				    	int i=0;
					  while(i<categories.size())
					  {
						  category.append(categories.get(i).text()+" ");
						  i++;
					  }
				    }
				    
				    // the total number of stars on average
				    metalinks = doc.select("div[class=tiny-star star-rating-non-editable-container]"); 
				    temp=metalinks.first().attr("aria-label").trim().split(" ");
				    numberOfStars =Double.parseDouble(temp[1]) ;
				    
				    // description
				    
				    desc = doc.select("div[class=id-app-orig-desc]").get(0).text(); 
				    
				    
				    // Number of persons for each rating
					  if(doc.select("span[class=bar-number]").size()>0)
					  {
						  int i=0;
						  while(i<doc.select("span[class=bar-number]").size())
						  {
							  numofratings[4-i]=Long.parseLong(doc.select("span[class=bar-number]").get(i).text().replaceAll("[^0-9]", ""));
							  i++;
						  }
					  }  
					  // Additional Information
					  
					  
					  if(doc.select("div[class=meta-info]").size()>0)
					  {
						  int i=0;
						  while(i<doc.select("div[class=meta-info]").size())
						  {
							  Element links = doc.select("div[class=meta-info]").get(i);
							  //System.out.println(links);
							  String links1 = links.select("div[class = title]").get(0).text();
							  if(!(links1.equalsIgnoreCase("report") || links1.equalsIgnoreCase("permissions")))
							  {
								  String links2 = links.select("div[class = content").get(0).text();
								  if(links1.equals("Updated")){
									  String tempdate=links.select("div[class = content").get(0).text().replaceAll("[,]", "");
									  lastupdated = new SimpleDateFormat("MMM dd yyyy").parse(tempdate);
								  }
								  if(links1.equals("Installs")){
									numofInstalls=  links.select("div[class = content").get(0).text();
									numinstallsupperbound=Long.parseLong(numofInstalls.split("-")[1].trim().replaceAll("[,]",""));
									numinstallslowerbound=Long.parseLong(numofInstalls.split("-")[0].trim().replaceAll("[,]", ""));
								  }
								  if(links1.contains("Requires")){
									
									  String temp1=links.select("div[class = content").get(0).text();
									  if(!temp1.contains("Varies with device")){
										  OSreq=  links.select("div[class = content").get(0).text();
									  }
									  else{
										  OSreq= null;
									  }
								  }
								  if(links1.equals("Offered By")){
									offeredBy=  links.select("div[class = content").get(0).text();
								  }
								  if(links1.equals("In-app Products")){
									  inapp_cost=links.select("div[class = content").get(0).text();
								  }
								  if(links1.equals("Size")){
									  String temp1=links.select("div[class = content").get(0).text();
									  if(!temp1.contains("Varies with device")){
										  String tempsize=links.select("div[class = content").get(0).text();
										  
										  if(tempsize.contains("k")){
											  appsize=Double.parseDouble(tempsize.substring(0,tempsize.length()-1).replaceAll("[,]", ""))/1024;
										  }
										  else if(tempsize.contains("M")){
											  appsize=Double.parseDouble(tempsize.substring(0,tempsize.length()-1).replaceAll("[,]", ""));
										  }
										  else if(tempsize.contains("G")){
											  appsize=Double.parseDouble(tempsize.substring(0,tempsize.length()-1).replaceAll("[,]", ""))*1024;
										  }
									  }
									  else{
										  appsize=0.0;
									  }
								  }
								  if(links1.contains("Version")){
									  String temp1=links.select("div[class = content").get(0).text();
									  if(!temp1.contains("Varies with device")){
										  version=links.select("div[class = content").get(0).text();
									  }
									  else{
										  version=null;
									  }
								  }
							  }
							  i++;
						  }
						  //Developer info ex: developers mail
						  if(doc.select("a[class=dev-link]").size()>0){
							  i=0;
							  while(i<doc.select("a[class=dev-link]").size()){
								  Element devinfolnk=doc.select("a[class=dev-link]").get(i);
								  String hrefs=devinfolnk.attr("href").trim();
								  if(hrefs.contains("mailto")){
									  Developerinfo=hrefs;
								  }
								  i++;
							  }
							  
						  }
					  }
					  
					 //Collecting Whatsnew information 
					 if(doc.select("div[class=details-section-contents show-more-container]").size()>0)
					 {
						  Element metalink = doc.select("div[class=details-section-contents show-more-container]").get(0); 
						  int i=0;
						  while(i<metalink.select("div[class=recent-change]").size())
						   {
							  whatsNew.append(" "+metalink.select("div[class=recent-change]").get(i).text()) ; 
							   i++;
						   }
					 }
				}
					 System.out.println("seed_id : "+ seed_id);
					 System.out.println("app_id : "+app_id);
					 System.out.println("app_url : "+app_url);
					 System.out.println("appname : "+appname);
					 System.out.println("Crawltime : "+crawledDate);
					 System.out.println("Total number of people rated: "+peopleRated);
					 System.out.println("Category : "+category.toString());
					 System.out.println("version: "+version);
					 System.out.println("Appsize: "+appsize);
					 System.out.println("Cost: "+cost);
					 System.out.println("inapp_products: "+inapp_cost);
					 System.out.println("Average rating: "+numberOfStars);
					 System.out.println("App description : "+desc);
					 System.out.println("dev_ email : "+Developerinfo);
					 System.out.println("Last updated : "+ lastupdated);
					 System.out.println("rate_5 : "+numofratings[4]);
					 System.out.println("rate_4 : "+numofratings[3]);
					 System.out.println("rate_3 : "+numofratings[2]);
					 System.out.println("rate_2 : "+numofratings[1]);
					 System.out.println("rate_1 : "+numofratings[0]);
					 System.out.println("Installs : "+numofInstalls);
					 System.out.println("OS_requirements : "+OSreq);
					 System.out.println("Offeredby : "+offeredBy);
					 System.out.println("Whatsnew : "+whatsNew);
					 
					 String insertquery="insert into apptable values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					 PreparedStatement insertstmt=conn.prepareStatement(insertquery);
					 insertstmt.setString(1, app_id);
					 insertstmt.setString(2,appname);
					 insertstmt.setTimestamp(3, (Timestamp) crawledDate);
					 insertstmt.setString(4,category.toString());
					 insertstmt.setString(5,offeredBy);
					 insertstmt.setString(6,version);
					 insertstmt.setDouble(7, cost);
					 insertstmt.setString(8,inapp_cost);
					 insertstmt.setString(9, desc);
					 insertstmt.setLong(10, numofratings[4]);
					 insertstmt.setLong(11, numofratings[3]);
					 insertstmt.setLong(12, numofratings[2]);
					 insertstmt.setLong(13, numofratings[1]);
					 insertstmt.setLong(14, numofratings[0]);
					 insertstmt.setDouble(15, numberOfStars);
					 insertstmt.setLong(16,peopleRated );
					 insertstmt.setString(17, whatsNew.toString());
					 java.sql.Date sqlDate = new java.sql.Date(lastupdated.getTime());
					 insertstmt.setDate(18,sqlDate );
					 if(appsize==0.0){
						 insertstmt.setString(19,null);	 
					 }
					 else{
						 insertstmt.setDouble(19,appsize);
					 }
					 insertstmt.setString(20, Developerinfo);
					 if(numinstallsupperbound==0){
						 insertstmt.setString(21, null);	 
					 }
					 else{
						 insertstmt.setLong(21, numinstallsupperbound);	 
					 }
					 if(numinstallslowerbound==0){
						 insertstmt.setString(22, null);	 
					 }
					 else{
						 insertstmt.setLong(22, numinstallslowerbound);	 
					 }					 
					 insertstmt.executeUpdate();
			}
					    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
