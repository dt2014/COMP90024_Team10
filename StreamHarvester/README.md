    Team: 10
    City: New York
    Students: 
    	Full Names  		Student IDs
   		Shuangchao Yin  	612511
    	Weijia Chen  		616213
    	Yun Shi  			621761
    	Jiajie Li 			631482
    	Fengmin Deng  		659332


# Streaming Tweets Harvester
----------
A system that harvests tweets using twitter Stream API.

## Enviroment Requirements ##
For installing and launching the system, these tools/environments are needed:

- JDK 1.7 or above
- Vert.x 2.1.5 or above
- Maven
- CouchDB

## To run the system ##
- Change the 'config.json' file with parameters desired
- Maven Install
- Launch via command: $ vertx runzip StreamHarvester-0.1-mod.zip -conf config.json

## Parameters of Application Configuration ##

###**"appConfig":** Configuration for the application starter, currently empty

###**"dbConfig":** Configuration for the database verticle
- **"dbHost"**: The CouchDB host for saving the harvested tweets
- **"dbPort"**: The CouchDB post for saving the harvested tweets
- **"dbName"**: The CouchDB name for saving the harvested tweets

###**"queryConfig":** Configuration for the query verticle
- **"OAuthConsumerKey"**: The OAth Consumer Key
- **"OAuthConsumerSecret"**: The OAth Consumer Secrety
- **"OAuthAccessToken"**: The OAth Access Token
- **"OAuthAccessTokenSecret"**: The OAth Access Token Secret
- **"boundingBox"**: The two longitude-latitude pairs which defines the bounding box of New York City
  - **"lon1"**: -74.255641
  - **"lat1"**: 40.495865
  - **"lon2"**: -73.699793
  - **"lat2"**: 40.91533

## The executable files ##
There are two pre-built executable files in this folder. There are basically for those who want to run directly the harvester program but not having maven or vertx installed.

- StreamHarvester-0.1-mod.zip
- StreamHarvester-0.1-fat.jar

