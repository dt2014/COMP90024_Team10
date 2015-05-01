package team10;

/*
 * @author Fengmin Deng
 * 
 * This verticle is for organizing and sending query requests for twitter Search
 * API. The search line could be either backwards or forwards by indicating either
 * "next_results" or "refresh_url" for the URL type in the configuration file.
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class QueryVerticle extends Verticle {
	
	// updateUrl is from either "refresh_url" or "next_results" in "search_metadata"
	private String updateUrl = null; 
	
	public void start() {
		final Logger log = container.logger();
		log.info("Query Verticle started.");
		final JsonObject config = container.config();
		final HttpClient client = vertx.createHttpClient()
				.setPort(config.getInteger("twPort"))
				.setHost(config.getString("twHost"))
				.setSSL(true)
				.setTrustAll(true)
				.setTryUseCompression(true);
		final String urlPath = config.getString("urlPath");
		final String urlPara = config.getString("urlPara");
		final String tag = config.getString("tag");
		final int interval = config.getInteger("interval");
		final EventBus evenBus = vertx.eventBus();
		evenBus.setDefaultReplyTimeout(config.getLong("timeout"));
		
		vertx.setPeriodic(interval, new Handler<Long>() {
			public void handle(Long timerID) {
		    	if (updateUrl == null) { // for the initial search
		    		updateUrl = urlPara;
		    	}
	    		log.info(updateUrl);
		    	final JsonObject appendInfo = appendInfoBuilder(tag, updateUrl);
		    	HttpClientRequest request = client.request("GET", urlPath + updateUrl, new Handler<HttpClientResponse>() {
					public void handle(HttpClientResponse resp) {
						int status = resp.statusCode();
						if (status == Constants.TW_STATUS_OK) {
							resp.bodyHandler(new Handler<Buffer>() {
					            public void handle(Buffer body) {
					            	JsonObject returnedData = new JsonObject(body.toString("utf-8"));
					            	updateUrl = returnedData.getObject("search_metadata").getString(config.getString("updateUrlType"));
					            	JsonArray tweets = returnedData.getArray("statuses");
					            	for(int i = 0; i < tweets.size(); ++i) {
					            		JsonObject tweet = tweets.get(i);
					            		tweet.putObject("harvester_appends", appendInfo);
					            		evenBus.send(Constants.QUEUE_CANDIDATE, tweet);
					            	}
					            }
					        });
						} else {
							JsonObject errMsg = new JsonObject();
							errMsg.putObject(Constants.TW_QUERY_FAIL, appendInfo);
							evenBus.send(Constants.QUEUE_LOG, errMsg);
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
				request.headers().set("Content-Length", "0")
								 .set("Authorization", config.getString("token"))
								 .set("Accept-Encoding", "gzip")
								 .set("Accept-Charset", "utf-8");
				request.end();
		        
		    }
		});
		log.info("Sending request...");
	}
	
	private static JsonObject appendInfoBuilder(String tag, String urlPara) {
		final JsonObject appendInfo = new JsonObject();
		appendInfo.putString("method", "search");
		appendInfo.putString("tag", tag);
		appendInfo.putString("url_para", urlPara);
        return appendInfo;
	}
}
