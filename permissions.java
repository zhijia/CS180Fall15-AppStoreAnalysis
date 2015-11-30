package Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.gargoylesoftware.htmlunit.javascript.host.Element;


public class permissions 
{
	public static HashMap<String, Integer> PerDict = new HashMap<String, Integer> ();
	public static int maxId=0;
	public static void importDict() throws SQLException, ClassNotFoundException
	{
		Class.forName("com.mysql.jdbc.Driver");
		String urldb = "jdbc:mysql://localhost/appstore";
		
		String user = "root";
		String password = "googleplay123";
		Connection myConn3 = DriverManager.getConnection(urldb, user, password);
		Statement st1 = null;
		
		st1 = myConn3.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
	    st1.setFetchSize(Integer.MIN_VALUE); 
	    
	    ResultSet rs1= st1.executeQuery("SELECT id, name FROM permission_dictionary order by 1");
	    while(rs1.next())
	    {
	    	int id = rs1.getInt("id");
	    	String name=rs1.getString("name");
	    	if(id>maxId)
	    		maxId=id;
	    	PerDict.put(name.toLowerCase(),id);
	    }
	   rs1.close();
	   st1.close();
	   myConn3.close();
	}
	
	public static void perMission(String appId, String page)throws IOException, SQLException, JSONException, InterruptedException  // the main metho
, ClassNotFoundException
	{
		
		Class.forName("com.mysql.jdbc.Driver");
		String urldb = "jdbc:mysql://localhost/appstore";
		
		String user = "root";
		String password = "googleplay123";
		Connection myConn2 = DriverManager.getConnection(urldb, user, password);
		PreparedStatement preparedStatement = null;
	
		//File input = new File("1.html");     
		try
		{
		    Document doc = Jsoup.parse(page, "UTF-8"); 
		    Elements bucketList = doc.select("div[class=permissions-container bucket-style]").select("div[class=permission-bucket]");
		    String heading=null;
		    for(org.jsoup.nodes.Element bucket: bucketList)
		    {
		    	int headingCnt = 0, descCnt = 0;
			    Elements jslList =bucket.select("div[class=bucket-icon-and-title]").select("jsl > jsl");
			    for(org.jsoup.nodes.Element jsl: jslList)
			    {
			    	headingCnt++;
			    	if(!(jsl.attr("style").equals("display:none")))
			    	{
			    		 heading = jsl.select("span[class=bucket-title]").text().toLowerCase().trim();
			    		//System.out.println("heading :" + heading);
			    	}
			    	//System.out.println(jsl);
			    }
			    boolean once = false;
			    Elements permissionList =  bucket.select("ul[class=bucket-description]").select("li");
			    for(org.jsoup.nodes.Element permission : permissionList) 
			    {
			    	descCnt++;
			        String currList = permission.text().toLowerCase().trim();
			        if(descCnt > headingCnt && once == false) {
			        	heading ="Other";
			        	//System.out.println("heading : "+heading);
			        	once = true;
			        }
			        if(!(permission.attr("style").equals("display:none")))
			    	{
			        	if(!PerDict.containsKey(currList)) {
			        		PreparedStatement upDict = myConn2.prepareStatement("INSERT IGNORE into permission_dictionary(name, category) VALUES (?, ?)");
		        			upDict.setString(1, currList);
		        			upDict.setString(2, heading);
		        			upDict.executeUpdate();
		        			upDict.close();
		        			importDict();
		  	
		        			
			        	}
			        
			        	String sql = "INSERT IGNORE into appPermissionMap values (?, ?)";
			        	//String sql = "update appPermissionMap set p" + PerDict.get(currList) + "= ?  where appid=?";
	        		    preparedStatement = myConn2.prepareStatement(sql);
	        					
    					preparedStatement.setString(1, appId);
    					preparedStatement.setInt(2, PerDict.get(currList));
    					preparedStatement.executeUpdate();
    					preparedStatement.close();
	        		
			        	/*
			        	for(Entry<String,Integer> entry : PerDict.entrySet())
			        	{
			        		if(entry.getKey().contains(currList))
			        		{

			        			String sql = "update appPermissionMap set p"+entry.getValue()+"= ?  where appid=?";
			        		    preparedStatement = myConn2.prepareStatement(sql);
			        					
	        					preparedStatement.setInt(1, 1);
	        					preparedStatement.setString(2, appId);
	        					preparedStatement.executeUpdate();
	        					preparedStatement.close();
			        			present = 1;
			        			break;
			        		}
			        		
			        	}
			        	if(present == 0)
		        		{
		        			PreparedStatement upDict = myConn2.prepareStatement("INSERT IGNORE into permission_dictionary(name, category) VALUES (?, ?)");
		        			upDict.setString(1, currList.toLowerCase().trim());
		        			upDict.setString(2, heading.toLowerCase().trim());
		        			upDict.executeUpdate();
		        			upDict.close();
		        			importDict();
		        			
		        			
		        			FileWriter fw1 = new FileWriter("extraPermission.txt",true);
		        			BufferedWriter bw1 = new BufferedWriter(fw1);
		        			bw1.write(""+appId+" "+currList);
		    				bw1.write("\r\n");
		    				bw1.close();
		        		}*/
			        	//System.out.println(currList);	        	
			    	}
			      
			    }
	
		    }
		   
		   myConn2.close();
		    		  

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException, JSONException, InterruptedException
	{
		Class.forName("com.mysql.jdbc.Driver");
		String urldb = "jdbc:mysql://localhost/appstore";
		
		String user = "root";
		String password = "googleplay123";
		Connection myConn1 = DriverManager.getConnection(urldb, user, password);
		importDict();
		
		Statement st = null;
		
		st = myConn1.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
	    st.setFetchSize(Integer.MIN_VALUE); 
	    
	    ResultSet rs= st.executeQuery("SELECT appid,app_page FROM appraw");
	    while(rs.next())
	    {
	    	String id= rs.getString("appid");
	    	String page=rs.getString("app_page");
	    	perMission(id, page);
	    }
		myConn1.close();
	}
}
