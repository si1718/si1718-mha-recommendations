package data.streaming.test;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.RecommenderBuildException;

import data.streaming.dto.KeywordDTO;
import data.streaming.utils.MongoUtils;
import data.streaming.utils.Utils;

public class TestBatch {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void executeBatch() {
		final Runnable action = new Runnable() {
			public void run() {
				try {
					Set<KeywordDTO> set = Utils.getKeywords();
					ItemRecommender irec = Utils.getRecommender(set);
					Utils.saveModel(irec, set);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RecommenderBuildException e) {
					e.printStackTrace();
				}

			}
		};

		scheduler.scheduleAtFixedRate(action, 0, 1, TimeUnit.HOURS);
	}

	public static void main(String... args) {
		MongoUtils.initialize();
		new TestBatch().executeBatch();
	}
}