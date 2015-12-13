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


