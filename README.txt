permissions.java
----------------
Objective:
	To parse and extract app permission from raw HTML webpages stored in database.

Working:
	Inputs:
		database connection string

	Permissions are the dynamic content displayed when user clicks on `View details`
	button under permissions category.
	Permissions are categorised into following:
		+---------------------------------------------+
		| calendar                                    |
		| camera                                      |
		| cellular data settings                      |
		| contacts                                    |
		| device & app history                        |
		| device id & call information                |
		| identity                                    |
		| location                                    |
		| microphone                                  |
		| Other                                       |
		| phone                                       |
		| photos/media/files                          |
		| sms                                         |
		| wearable sensors/activity data body sensors |
		| wi-fi connection information                |
		+---------------------------------------------+

	To achieve the objective, following is adopted:
	1) `importDict()` imports `appstore`.`permission_dictionary` into memory as HashMap.
	Importing into memory saves SQL communications.
	2) `perMission(String appId, String page)` receives appID and raw HTML page
	as arguments. 
	All the permissions are contained in "div[class=permissions-container bucket-style]" 
	segment. Iterating through this list gives us permissions displayed on webpage.
	"div[class=bucket-icon-and-title]" contain the category.
	"ul[class=bucket-description]" contain the actual permissions.
	We ignore "display:none" segments since permissions in this segment are not used
	by app. Although they may appear in page source, but they are not displayed to
	user.

Dependencies:

	permissions.java utilizes functionalities from following external libraries:
		a. jsoup
		b. mysql-connector-java
	You will find .jar for above mentioned dependencies in lib/.

How to run:
	
	1) Download permissions.java. 
	2) javac -cp .:./lib/* -d . permissions.java
	3) java -jar permissions.java

	Suggested JDK -> 1.8 or above.

Database dependency:
	permissions.java reads unparsed HTML pages from `appraw` 
	and stores app-permission mapping in appPermissionMap 
	having following schema:
	
	`appstore`.`appraw`
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


	`appstore`.`permission_dictionary`

	+----------+--------------+------+-----+---------+----------------+
	| Field    | Type         | Null | Key | Default | Extra          |
	+----------+--------------+------+-----+---------+----------------+
	| id       | mediumint(9) | NO   | PRI | NULL    | auto_increment |
	| name     | varchar(200) | NO   | PRI |         |                |
	| category | varchar(200) | YES  |     | NULL    |                |
	+----------+--------------+------+-----+---------+----------------+


	`appstore`.`appPermissionMap`

	+-------+--------------+------+-----+---------+-------+
	| Field | Type         | Null | Key | Default | Extra |
	+-------+--------------+------+-----+---------+-------+
	| appid | varchar(200) | YES  |     | NULL    |       |
	| pid   | int(11)      | YES  |     | NULL    |       |
	+-------+--------------+------+-----+---------+-------+




ReviewCrawler.java
------------------
Objective:
	To crawl the reviews of apps and store then into appreview table.

Working:
	Inputs:
		1. database connection string.
		2. AppIDs from appraw table.

	Initially the static app page contains only 4 reviews. We need to click the next button besides review manually to get the reviews as dynamic content.
	We have used Selenium to make that 'next' button click.

	To achieve the objective, following is adopted:
	1) "WebElement expandButton = driver.findElement(By.className("expand-next"));" this line will get the expand next button control and "expandButton.click()" will make the button click.
	2) Each review is contained in "div[class=single-review]". 
	Iterating through this list gives us reviews displayed on webpage.
	"("div[class=review-info]").select("span[class=author-name]").get(0).text()" will get the review author name.
	("div[class=review-info]").select("span[class=author-name]").get(0) will give yu the author name.
	("div[class=review-info]").select("span[class=review-date]") will get the review date.
	("div[class=review-info]").select("div[class=tiny-star star-rating-non-editable-container]").attr("aria-label").trim().split(" ") will get the reviewer's rating.
	"div[class=review-text]" will get the review text.
	
Dependencies:

	ReviewsCrawler.java utilizes functionalities from following external libraries:
		a. jsoup
		b. mysql-connector-java
		c. selenium-server
	You will find .jar for above mentioned dependencies in lib/.

How to run:
	
	Download reviewcrawler.jar. Suggested JDK -> 1.8 or above.

Database dependency:
	reviewcrawler.java get all the urls of the apps from appraw table and store the reviews into appreview table.
	
	Appstore.appreview
	
+---------------+--------------+------+-----+---------+-------+
| Field         | Type         | Null | Key | Default | Extra |
+---------------+--------------+------+-----+---------+-------+
| app_id        | varchar(500) | YES  |     | NULL    |       |
| rev_auth_name | varchar(50)  | YES  |     | NULL    |       |
| review_date   | date         | YES  |     | NULL    |       |
| review        | mediumtext   | YES  |     | NULL    |       |
| ratingGiven   | double       | YES  |     | NULL    |       |
| authorID      | varchar(100) | YES  |     | NULL    |       |
+---------------+--------------+------+-----+---------+-------+




reviewanalysis.java
-------------------
Objective:
	To get the word count for each app according to their ratings. 

Working:
	Inputs:
		1. database connection string.
		2. Appreview table.

	We will get all the reviews into datasets according to the app and its rating. 
	Store all the stopwords that needs to be excluded from the inverted index in a Hashset.
	To achieve the objective, following is adopted:
	1) Create a Hashmap with word and wordcount as its key and value.
	2) update the word count for each word in the hashmap.
	
Dependencies:

	Reviewanalysis.java utilizes functionalities from following external libraries:
		a. mysql-connector-java
	You will find .jar for above mentioned dependencies in lib/.

How to run:
	
	1) Download reviewanalysis.java. 
	2) javac -cp .:./lib/* -d . reviewanalysis.java
	3) java -jar reviewanalysis.java

	Suggested JDK -> 1.8 or above.


Database dependency:
	reviewanalysis.java stores the inverted index in appstore.review_wordcount table.
	
	Appstore.review_wordcount
	
+-----------+--------------+------+-----+---------+-------+
| Field     | Type         | Null | Key | Default | Extra |
+-----------+--------------+------+-----+---------+-------+
| app_id    | varchar(200) | NO   |     | NULL    |       |
| word      | varchar(200) | NO   |     | NULL    |       |
| wordcount | int(11)      | NO   |     | NULL    |       |
| rating    | int(11)      | NO   |     | NULL    |       |
+-----------+--------------+------+-----+---------+-------+

