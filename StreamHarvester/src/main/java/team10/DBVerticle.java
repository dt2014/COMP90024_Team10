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
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class DBVerticle extends Verticle {
	//For iterating multiple couchdb databases to share the storage load
	int iterator = 0;
	
	public void start() {
		final Logger log = container.logger();
		log.info("DB Verticle started.");
		final JsonObject config = container.config();
		final HttpClient client = vertx.createHttpClient()
				.setPort(config.getInteger("dbPort"))
				.setHost(config.getString("dbHost"));
		final EventBus evenBus = vertx.eventBus();
		evenBus.registerHandler("tweetData.queue", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				final JsonObject tweet = message.body();
		       	final String id = tweet.getString("id_str");
		        HttpClientRequest request = client.request("PUT", config.getString("dbName") + id, new Handler<HttpClientResponse>() {
					public void handle(HttpClientResponse resp) {
						if (resp.statusCode() != 201) {
							JsonObject errMsg = new JsonObject();
							errMsg.putString("Failed to insert", id);
						}
					}
				}).exceptionHandler(new Handler<Throwable>() {
					@Override
					public void handle(Throwable t) {
						JsonObject errMsg = new JsonObject();
						errMsg.putString("Failed to insert", t.getMessage());
					}
				});
				request.setChunked(true);
				request.headers().set("Content-Type", "application/json");
				request.write(tweet.toString(), "utf-8");
				request.end();
			}
		});
	}
}
