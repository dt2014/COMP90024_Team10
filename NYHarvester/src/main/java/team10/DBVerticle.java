package team10;

/*
 * @author Fengmin Deng
 * 
 * This verticle is for storing complete tweet records among which each one is unique
 * in distributed CouchDB databases.
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
		final JsonArray dbs = config.getArray("distributed_dbs");
		final int numOfDbs = dbs.size();
		final HttpClient clients[] = new HttpClient[numOfDbs];
		for (int i = 0; i < numOfDbs; ++i) {
			clients[i] = vertx.createHttpClient()
					.setPort(((JsonObject)dbs.get(i)).getInteger("dbPort"))
					.setHost(((JsonObject)dbs.get(i)).getString("dbHost"));
		}
		final EventBus evenBus = vertx.eventBus();
		evenBus.registerHandler(Constants.QUEUE_TWDATA, new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				final JsonObject tweet = message.body();
		       	final String id = tweet.getString("id_str");
		        HttpClientRequest request = clients[iterator].request("PUT", ((JsonObject)dbs.get(iterator)).getString("dbName") + id, new Handler<HttpClientResponse>() {
					public void handle(HttpClientResponse resp) {
						if (resp.statusCode() != Constants.DB_STATUS_CREATED) {
							JsonObject errMsg = new JsonObject();
							errMsg.putString(Constants.DB_INSERT_FAIL, id);
							evenBus.send(Constants.QUEUE_LOG, errMsg);
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
		        if (++iterator >= numOfDbs) {
		        	iterator = 0;
		        }
				request.setChunked(true);
				request.headers().set("Content-Type", "application/json");
				request.write(tweet.toString(), "utf-8");
				request.end();
			}
		});
	}
}
