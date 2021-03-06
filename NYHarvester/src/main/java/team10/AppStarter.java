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

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/* * 
 * This verticle is for deloying the verticles in this application. Firstly
 * the log verticle, then remove-duplicate (tweets) verticle, and database
 * verticle and finally the query verticle.
 */
public class AppStarter extends Verticle {

	public void start() {
		final Logger log = container.logger();
		JsonObject config = container.config();
		final JsonObject appConfig = config.getObject("appConfig");
		final JsonObject logConfig = config.getObject("logConfig");
		final JsonObject queryConfig = config.getObject("queryConfig");
		final JsonObject removeDuplicateConfig = config.getObject("removeDuplicateConfig");
		final JsonObject dbConfig = config.getObject("dbConfig");
		
		container.deployVerticle(LogVerticle.class.getCanonicalName(), logConfig, appConfig.getInteger("logVerticleInstances"), new AsyncResultHandler<String>() {
		    public void handle(AsyncResult<String> asyncResult) {
		        if (asyncResult.succeeded()) {
		        	container.deployVerticle(RemoveDuplicateVerticle.class.getCanonicalName(), removeDuplicateConfig, appConfig.getInteger("dbVerticleInstances"), new AsyncResultHandler<String>() {
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
		        } else {
		            asyncResult.cause().printStackTrace();
		            container.exit();
		        }
		    }
		});
	}
}
