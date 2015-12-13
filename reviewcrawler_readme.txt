ReviewCrawler.java
----------------
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
