package nwoolcan.model.brewery.batch.review.types;

import nwoolcan.model.brewery.batch.review.BatchEvaluationType;
import nwoolcan.utils.Result;

import java.util.Set;

/**
 * Scan the codebase to find all implementations of BatchEvaluation.
 */
public interface BatchEvaluationTypeScanner {

    /**
     * Return all BatchEvaluationType found by the scanner.
     * @return a {@link Set} of {@link BatchEvaluationType}
     */
    Result<Set<BatchEvaluationType>> getAvailableBatchEvaluationTypes();
}
