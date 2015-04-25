# Tweets Harvester Project
==============

##Enviroment Requirements
For installing and launching the system, these tools/environments are needed:
- JDK 1.7 or above
- Vert.x 2.1.5 or above
- Maven
- CouchDB

##To run the system
- Change the 'config.json' file with parameters desired
- Maven Install
- Launch via command: $ vertx runzip NYHarvester-0.1-mod.zip -conf 'config.json'

##Parameters of Application Configuration
1. "appConfig": Configuration for the application starter
		"logVerticleInstances": Number of instances of LogVerticle to be deployed 
		"dbVerticleInstances": Number of instances of DBVerticleInstances to be deployed
		"queryVerticleInstances": Number of instances of ParseVerticleInstances to be deployed

2. "logConfig": Configuration for the log verticle, currently empty

3. "dbConfig": Configuration for the database verticle
		"dbHost": The CouchDB host for saving the harvested tweets
		"dbPort": The CouchDB post for saving the harvested tweets
		"dbName": The CouchDB name for saving the harvested tweets
		dbConflictCount" : The number of conflicts, i.e. repeated tweets, accumulated by inserting data in CouchDB

4. "queryConfig": Configuration for the query verticle
		"twHost": Twitter resource host
		"twPort": Twitter resource post
		"urlPath": URL path
		"urlPara": URL paramenters
		"token": The bearer token obtained by by issuing a request to 'POST oauth2 / token' for application-only requests, see https://dev.twitter.com/oauth/application-only for the way to retrieve a bearer token; the value here should be in the form of "Bearer <...>", where <...> is the value of bearer token granted.
		"urlType": Either "refresh_url" for harvesting newer tweets or "next_results" for harvesting older tweets in the next round of search,here 'new' and 'old' are in terms of time line when tweets are created in twitter
		"timeout": The timeout in milliseconds for replies from query verticle
		"interval": The maximum interval in milliseconds to query twitter without exceeding the rate limit (450 Requests / 15-min window for Application-only authentication)
		"tag": The tag is used to label search region, instance identifier etc  
