package team10;

/*
 * @author Fengmin Deng
 * 
 * This verticle is responsible for logging for the application.
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class LogVerticle extends Verticle {

	public void start() {
		final Logger log = container.logger();
		log.info("Log Verticle started.");
		
		final EventBus evenBus = vertx.eventBus();
		evenBus.registerHandler(Constants.QUEUE_LOG, new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				log.warn(message.body());
			}
		});
	}
}
