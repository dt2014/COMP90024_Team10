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

/*
 * This verticle is for deloying the verticles in this application. Firstly
 * the database verticle, then queryverticle.
 */
public class AppStarter extends Verticle {

	public void start() {
		final Logger log = container.logger();
		JsonObject config = container.config();
		final JsonObject appConfig = config.getObject("appConfig");
		final JsonObject queryConfig = config.getObject("queryConfig");
		final JsonObject dbConfig = config.getObject("dbConfig");
		
		container.deployVerticle(DBVerticle.class.getCanonicalName(), dbConfig, 1, new AsyncResultHandler<String>() {
		    public void handle(AsyncResult<String> asyncResult) {
		        if (asyncResult.succeeded()) {
		        	container.deployVerticle(QueryVerticle.class.getCanonicalName(), queryConfig, 1, new AsyncResultHandler<String>() {
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
	}
}
