GooglePlayCrawler1
------------------
Objective:
	GooglePlayCrawler1 crawls and stores apps from GooglPlay store. 
	It also includes text appearnig in permission window of app.

Working:
	Inputs:
		crawl cycle number
		filepath containing read URLs.
		Database username
		Database password
	The layout of webpages corresponding to these seed URLs should conform
	to standard GooglePlay store app listing (look at APP_LINK in code).
	After fetching each batch of apps URLs, the app pages are crawled and
	stored into database before getting URLs for next batch.
	POST request for next batch, is then submitted.
	When crawling each app page, we are interested in getting the text in 
	permission window. This window is prompted when user clicks 'View details'
	button under Permissions section. Since this is dynamically driven, 
	we use selenium to perform the user event and obtain dynamically added
	HTML content.

Dependencies:

	GooglePlayCrawler1.java utilizes functionalities from following external libraries:
		a. jsoup
		b. mysql-connector-java
		c. selenium-server
	You will find .jar for above mentioned dependencies in lib/.

How to run:
	Download PlayStoreCrawler.jar. Suggested JDK -> 1.8 or above.

Database dependency:
	GooglePlayCrawler1.java stores fetch app URLs and HTML pages in MySQL Database 
	having following schema:
	
	APPSTORE.APPRAW

	+-----------+--------------+------+-----+---------------------+-------+
	| Field     | Type         | Null | Key | Default             | Extra |
	+-----------+--------------+------+-----+---------------------+-------+
	| seed_id   | int(3)       | YES  |     | NULL                |       |
	| app_url   | varchar(400) | NO   | PRI |                     |       |
	| app_page  | mediumtext   | YES  |     | NULL                |       |
	| crawltime | timestamp    | YES  |     | 0000-00-00 00:00:00 |       |
	| cycle     | int(3)       | NO   | PRI | 0                   |       |
	| appid     | varchar(200) | YES  |     | NULL                |       |
	| review    | int(1)       | YES  |     | NULL                |       |
	+-----------+--------------+------+-----+---------------------+-------+

