package reviewanalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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


public class reviewanalysis {

	static Set<String>Stopwords;
	static{
		Stopwords = new HashSet<String>();
		//String stop="a,about,above,after,again,against,all,am,an,and,any,are,aren't,as,at,be,because,been,before,being,below,between,both,but,by,can't,cannot,could,couldn't,did,didn't,do,does,doesn't,doing,don't,down,during,each,few,for,from,further,had,hadn't,has,hasn't,have,haven't,having,he,he'd,he'll,he's,her,here,here's,hers,herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,it's,its,itself,let's,me,more,most,mustn't,my,myself,no,nor,not,of,off,on,once,only,or,other,ought,our,ours,ourselves,out,over,own,same,shan't,she,she'd,she'll,she's,should,shouldn't,so,some,such,than,that,that's,the,their,theirs,them,themselves,then,there,there's,these,they,they'd,they'll,they're,they've,this,those,through,to,too,under,until,up,very,was,wasn't,we,we'd,we'll,we're,we've,were,weren't,what,what's,when,when's,where,where's,which,while,who,who's,whom,why,why's,with,won't,would,wouldn't,you,you'd,you'll,you're,you've,your,yours,yourself,yourselves,can,dont";
		String stop="a,about,above,after,again,against,all,am,an,and,any,are,as,at,be,because,been,,before,being,below,between,both,but,by,could,did,do,does,doing,down,during,each,few,for,from,further,had,has,have,having,he,he'd,he'll,he's,her,here,here's,hers,herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,it,it's,its,itself,let's,me,more,most,my,myself,no,nor,not,of,off,on,once,only,or,other,ought,our,ours,ourselves,out,over,own,same,she,she'd,she'll,she's,should,so,some,such,than,that,that's,the,their,theirs,them,themselves,then,there,there's,these,they,they'd,they'll,they're,they've,this,those,through,to,too,under,until,up,very,was,we,we'd,we'll,we're,we've,were,what,what's,when,when's,where,where's,which,while,who,who's,whom,why,why's,with,won't,would,you,you'd,you'll,you're,you've,your,yours,yourself,yourselves";
		String[] stops=stop.split(",");
		for(int i=0;i<stops.length;i++){
			  Stopwords.add(stops[i]);
		  }	
	}
	public static void getanalysis(){
		Connection conn = null;
		
		try{
			//conn=DriverManager.getConnection("jdbc:mysql://localhost/appstore?user=root&password=googleplay123");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/playstore","root","");
			Statement smt = conn.createStatement();
			String urlquery="SELECT distinct app_id FROM appreview";
			ResultSet rs_urls=smt.executeQuery(urlquery);
			List<String> appurls=new ArrayList<String>();
			while(rs_urls.next()){
				String appurl=rs_urls.getString("app_id");
				appurls.add(appurl);
			}
			for(String s : appurls){
				for(int j=5;j>0;j--){
					String query="SELECT * FROM appreview where ratingGiven ="+j+" and app_id='"+s+"'";
					Statement smt2 = conn.createStatement();
					ResultSet rs_url = smt2.executeQuery( query );
					HashMap<String,Integer> wordMap=new HashMap<String,Integer>();
					while(rs_url.next()) {
						String rvwcontent=rs_url.getString("review");
						rvwcontent= rvwcontent.replaceAll("[^A-Za-z ]", "");
						StringTokenizer itr=new StringTokenizer(rvwcontent.toLowerCase());
						while(itr.hasMoreTokens()){
							String val=itr.nextToken();
							Pattern p=Pattern.compile("\\w+");
							Matcher m=p.matcher(val);
							while(m.find()){
								String mKey=m.group().toLowerCase();
								if(Stopwords.contains(mKey)){
									continue;
								}
								if(wordMap.containsKey(val)){
									wordMap.put(val, wordMap.get(val)+1);
								}
								else{
									wordMap.put(val, 1);
								}
							}
						}
					}
					System.out.printf("appID : "+s+"  and no of words: "+wordMap.size()+" for rating: "+j);
					for(Map.Entry<String,Integer> entry : wordMap.entrySet()){
					    PreparedStatement insertreview = conn.prepareStatement("INSERT review_wordcount(app_id,word,wordcount,rating) values (?,?,?,?)");
						insertreview.setString(1, s);
						insertreview.setString(2,entry.getKey());
						insertreview.setInt(3,entry.getValue());
						insertreview.setInt(4,j);
						insertreview.executeUpdate();
						insertreview.close();
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getanalysis();
	}

}
