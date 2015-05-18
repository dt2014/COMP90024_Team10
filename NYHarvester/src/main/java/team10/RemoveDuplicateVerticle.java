package team10;

/*
    Team: 10
    City: New York
    Students: 
    	Full Names  		Student IDs
   		Shuangchao Yin  	612511
    	Weijia Chen  		616213
    	Yun Shi  			621761
    	Jiajie Li 			631482
    	Fengmin Deng  		659332
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/* 
 * This verticle is for putting only an id as a document in a centralized 
 * CouchDB database which is for preventing storing duplicated tweets.
 * Only a tweet with an id saved successfully in the id-only database would be
 * stored for sentimental analysis.
 */
public class RemoveDuplicateVerticle extends Verticle {

	private int dbInsertFailCounter = 0;
	
	public void start() {
		final Logger log = container.logger();
		log.info("Remove-Duplicate Verticle started.");
		final JsonObject config = container.config();
		final HttpClient client = vertx.createHttpClient()
				.setPort(config.getInteger("dbPort"))
				.setHost(config.getString("dbHost"));
		final int dbConflictCount = config.getInteger("dbConflictCount");
		final EventBus evenBus = vertx.eventBus();
		evenBus.registerHandler(Constants.QUEUE_CANDIDATE, new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				final JsonObject tweet = message.body();
		       	final String id = tweet.getString("id_str");
		        HttpClientRequest request = client.request("PUT", config.getString("dbName") + id, new Handler<HttpClientResponse>() {
					public void handle(HttpClientResponse resp) {
						if (resp.statusCode() == Constants.DB_STATUS_CREATED) {
							evenBus.send(Constants.QUEUE_TWDATA, tweet);
						} else {
							JsonObject errMsg = new JsonObject();
							errMsg.putString(Constants.DUPLICATED_ID, id);
							evenBus.send(Constants.QUEUE_LOG, errMsg);
							++dbInsertFailCounter;
//							log.info(dbInsertFailCounter);
							if (dbInsertFailCounter > dbConflictCount) {
								log.error("Over " + dbConflictCount + " conflicts in inserting, seems no more new tweets so far...");
								container.exit();
							}
						}
					}
				}).exceptionHandler(new Handler<Throwable>() {
					@Override
					public void handle(Throwable t) {
						JsonObject errMsg = new JsonObject();
						errMsg.putString(Constants.DB_INSERT_FAIL, t.getMessage());
						evenBus.send(Constants.QUEUE_LOG, errMsg);
					}
				});
				request.setChunked(true);
				request.headers().set("Content-Type", "application/json");
				request.write("{}", "utf-8");
				request.end();
			}
		});
	}
}
