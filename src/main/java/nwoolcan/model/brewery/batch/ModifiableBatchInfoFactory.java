package nwoolcan.model.brewery.batch;

import nwoolcan.model.brewery.batch.misc.BeerDescription;
import nwoolcan.model.brewery.batch.misc.WaterMeasurement;
import nwoolcan.model.brewery.warehouse.article.IngredientArticle;
import nwoolcan.model.utils.Quantity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

/**
 * Constructs a {@link ModifiableBatchInfo}.
 */
//Package private
final class ModifiableBatchInfoFactory {

    private ModifiableBatchInfoFactory() {
    }

    static ModifiableBatchInfo create(final Collection<Pair<IngredientArticle, Double>> ingredients,
                                      final BeerDescription beerDescription,
                                      final BatchMethod method,
                                      final Quantity size,
                                      final WaterMeasurement measurement) {
        return new ModifiableBatchInfoImpl(ingredients, beerDescription, method, size, measurement);
    }

    static ModifiableBatchInfo create(final Collection<Pair<IngredientArticle, Double>> ingredients,
                                      final BeerDescription beerDescription,
                                      final BatchMethod method,
                                      final Quantity size) {
        return new ModifiableBatchInfoImpl(ingredients, beerDescription, method, size, null);
    }

}
