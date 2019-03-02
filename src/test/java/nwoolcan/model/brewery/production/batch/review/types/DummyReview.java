package nwoolcan.model.brewery.production.batch.review.types;

import nwoolcan.model.brewery.production.batch.review.BatchReviewType;
import nwoolcan.model.brewery.production.batch.review.EvaluationType;

import java.util.Arrays;
import java.util.Collection;

/**
 * Dummy Review for testing.
 */
public enum DummyReview implements BatchReviewType {
    /**
     * Test only.
     */
    DUMMY_REVIEW;
    enum DummyReviewCategories implements EvaluationType {
        CAT1("CAT1", 10),
        CAT2("CAT2", 2);

        private String name;
        private int score;

        DummyReviewCategories(final String name, final int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getMaxScore() {
            return this.score;
        }
    }

    @Override
    public Collection<EvaluationType> getCategories() {
        return Arrays.asList(DummyReviewCategories.values());
    }

    @Override
    public String getName() {
        return "DUMMY";
    }

    @Override
    public int getMaxScore() {
        return 10 + 2;
    }
}
