package data.streaming.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.flink.shaded.com.google.common.collect.Maps;
import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventCollectionDAO;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.event.Event;
import org.grouplens.lenskit.data.event.MutableRating;
import org.grouplens.lenskit.knn.user.UserUserItemScorer;
import org.grouplens.lenskit.scored.ScoredId;

import data.streaming.dto.KeywordDTO;
import data.streaming.dto.Recommendation;

public class Utils {
	public static final String PROPERTIES_FILE = "resources/data.properties";
	private static final int MAX_RECOMMENDATIONS = 3;

	public static Set<KeywordDTO> getKeywords() throws IOException {
		Set<KeywordDTO> keywords =  MongoUtils.getRatings();
		return keywords;
	}
	
	public static ItemRecommender getRecommender(Set<KeywordDTO> dtos) throws RecommenderBuildException {
		LenskitConfiguration config = new LenskitConfiguration();
		EventDAO myDAO = EventCollectionDAO.create(createEventCollection(dtos));

		config.bind(EventDAO.class).to(myDAO);
		config.bind(ItemScorer.class).to(UserUserItemScorer.class);

		Recommender rec = LenskitRecommender.build(config);
		ItemRecommender recommender = rec.getItemRecommender();
		return recommender;
	}

	private static Collection<? extends Event> createEventCollection(Set<KeywordDTO> ratings) {
		List<Event> result = new LinkedList<>();

		for (KeywordDTO dto : ratings) {
			MutableRating r = new MutableRating();
			r.setItemId(dto.getIdProject1().hashCode());
			r.setUserId(dto.getIdProject2().hashCode());
			r.setRating(dto.getScore());
			result.add(r);
		}
		return result;
	}

	public static void saveModel(ItemRecommender irec, Set<KeywordDTO> set) throws IOException {
		List<Recommendation> model = new ArrayList<Recommendation>();
		Map<String, Long> keys = Maps.asMap(set.stream().map((KeywordDTO x) -> x.getIdProject1()).collect(Collectors.toSet()),
				(String y) -> new Long(y.hashCode()));
		Map<Long, List<String>> reverse = set.stream().map((KeywordDTO x) -> x.getIdProject1())
				.collect(Collectors.groupingBy((String x) -> new Long(x.hashCode())));

		for (String key : keys.keySet()) {
			List<ScoredId> recommendations = irec.recommend(keys.get(key), MAX_RECOMMENDATIONS);
			if (recommendations.size() > 0) {
				model.add(new Recommendation(key,recommendations.stream().map(x -> reverse.get(x.getId()).get(0))
						.collect(Collectors.toSet())));
			}
		}
		
		MongoUtils.addRecommendations(model);

	}

}
