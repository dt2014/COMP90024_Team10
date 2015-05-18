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
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
 * This verticle is responsible for logging for the application.
 */
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
