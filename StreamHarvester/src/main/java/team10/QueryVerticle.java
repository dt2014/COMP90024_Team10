package team10;

/*
 * @author Fengmin Deng
 * 
 * This verticle is for organizing and sending query requests for twitter Search
 * API. The search line could be either backwards or forwards by indicating either
 * "next_results" or "refresh_url" for the URL type in the configuration file.
 */

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class QueryVerticle extends Verticle {
	
	public void start() {
		final Logger log = container.logger();
		final EventBus evenBus = vertx.eventBus();
		final JsonObject config = container.config();
		final String OAuthConsumerKey = config.getString("OAuthConsumerKey");
		final String OAuthConsumerSecret = config.getString("OAuthConsumerSecret");
		final String OAuthAccessToken = config.getString("OAuthAccessToken");
		final String OAuthAccessTokenSecret = config.getString("OAuthAccessTokenSecret");
		final JsonObject bbx = config.getObject("boundingBox");
		//{{longitude1, latitude1}, {longitude2, latitude2}}
		final double[][] boundingBox = {{bbx.getNumber("lon1").doubleValue(), bbx.getNumber("lat1").doubleValue()}, 
				{bbx.getNumber("lon2").doubleValue(), bbx.getNumber("lat2").doubleValue()}};
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setJSONStoreEnabled(true);
        cb.setOAuthConsumerKey(OAuthConsumerKey);
        cb.setOAuthConsumerSecret(OAuthConsumerSecret);
        cb.setOAuthAccessToken(OAuthAccessToken);
        cb.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(new StatusListener() {
			@Override
			public void onException(Exception ex) {
				log.error(ex.toString());
			}
			@Override
			public void onStatus(Status status) {
				String rawJSON = TwitterObjectFactory.getRawJSON(status);
				JsonObject tweet = new JsonObject(rawJSON);
//				log.info(tweet);
				evenBus.send("tweetData.queue", tweet);
			}
			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				log.info("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}
			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				log.info("Got track limitation notice:" + numberOfLimitedStatuses);
				
			}
			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				log.info("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}
			@Override
			public void onStallWarning(StallWarning warning) {
				log.info("Got Stall Warning:" + warning.getMessage());
			}
        });
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.locations(boundingBox);
        
        twitterStream.filter(filterQuery);
	}
}
