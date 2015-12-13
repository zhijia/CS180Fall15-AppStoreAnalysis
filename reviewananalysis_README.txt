reviewanalysis.java
----------------
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
