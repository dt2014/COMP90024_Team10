package team10;

/*
 * @author Fengmin Deng
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;


public class AppStarter extends Verticle {

	public void start() {
		final Logger log = container.logger();
		JsonObject config = container.config();
		final JsonObject appConfig = config.getObject("appConfig");
		final JsonObject logConfig = config.getObject("logConfig");
		final JsonObject queryConfig = config.getObject("queryConfig");
		final JsonObject dbConfig = config.getObject("dbConfig");
		
		container.deployVerticle(LogVerticle.class.getCanonicalName(), logConfig, appConfig.getInteger("logVerticleInstances"), new AsyncResultHandler<String>() {
		    public void handle(AsyncResult<String> asyncResult) {
		        if (asyncResult.succeeded()) {
		        	container.deployVerticle(DBVerticle.class.getCanonicalName(), dbConfig, appConfig.getInteger("dbVerticleInstances"), new AsyncResultHandler<String>() {
		    		    public void handle(AsyncResult<String> asyncResult) {
		    		        if (asyncResult.succeeded()) {
		    		        	container.deployVerticle(QueryVerticle.class.getCanonicalName(), queryConfig, appConfig.getInteger("queryVerticleInstances"), new AsyncResultHandler<String>() {
		    		    		    public void handle(AsyncResult<String> asyncResult) {
		    		    		    	if (asyncResult.succeeded()) {
		    		    		        	log.info("All verticles deployed.");
		    		    		        } else {
		    		    		            asyncResult.cause().printStackTrace();
		    		    		            container.exit();
		    		    		        }
		    		    		    }
		    		    		});
		    		        } else {
		    		            asyncResult.cause().printStackTrace();
		    		            container.exit();
		    		        }
		    		    }
		    		});
		        } else {
		            asyncResult.cause().printStackTrace();
		            container.exit();
		        }
		    }
		});
	}
}
