package team10;

/*
 * @author Fengmin Deng
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

public class DBVerticle extends Verticle {

	private int dbInsertFailCounter = 0;
	
	public void start() {
		final Logger log = container.logger();
		log.info("DB Verticle started.");
		final JsonObject config = container.config();
		final HttpClient client = vertx.createHttpClient()
				.setPort(config.getInteger("dbPort"))
				.setHost(config.getString("dbHost"));
		final int dbConflictCount = config.getInteger("dbConflictCount");
		final EventBus evenBus = vertx.eventBus();
		evenBus.registerHandler(Constants.QUEUE_TWDATA, new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				final JsonObject tweet = message.body();
		       	final String id = tweet.getString("id_str");
		        HttpClientRequest request = client.request("PUT", config.getString("dbName") + id, new Handler<HttpClientResponse>() {
					public void handle(HttpClientResponse resp) {
						if (resp.statusCode() != Constants.DB_STATUS_CREATED) {
							JsonObject errMsg = new JsonObject();
							errMsg.putString(Constants.DB_INSERT_FAIL, id);
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
						errMsg.putString(Constants.TW_QUERY_FAIL, t.getMessage());
						evenBus.send(Constants.QUEUE_LOG, errMsg);
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
