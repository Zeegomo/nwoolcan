package nwoolcan.model.brewery.warehouse.stock;

import nwoolcan.model.brewery.production.batch.Batch;
import nwoolcan.model.brewery.warehouse.article.Article;
import nwoolcan.model.brewery.warehouse.article.ArticleManager;
import nwoolcan.model.brewery.warehouse.article.ArticleType;
import nwoolcan.model.brewery.warehouse.article.BeerArticle;
import nwoolcan.utils.Result;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for {@link Stock} objects. It is used by tests and by the
 * {@link nwoolcan.model.brewery.warehouse.Warehouse} in order to create {@link Stock}, check the
 * id of the {@link Stock}, avoid repetitions and set name of the {@link Stock}.
 */
public final class StockManager {

    @Nullable private static StockManager instance;
    private final ArticleManager articleManager;
    private final Map<Stock, Integer> stockToId;
    private final Map<Integer, Stock> idToStock;
    private static final int FAKE_ID = -1;
    private int nextAvailableId;

    private StockManager() {
        articleManager = ArticleManager.getInstance();
        nextAvailableId = 1;
        stockToId = new HashMap<>();
        idToStock = new HashMap<>();
    }
    /**
     * Returns the only instance of the {@link StockManager} using a singleton pattern.
     * @return the only instance of the {@link StockManager} using a singleton pattern.
     */
    public static synchronized StockManager getInstance() {
        if (instance == null) {
            instance = new StockManager();
        }
        return instance;
    }
    /**
     * Checks the consistency of the {@link Stock}.
     * @param stock to be checked.
     * @return a boolean denoting whether the id is correct or not.
     */
    public synchronized boolean checkId(final Stock stock) {
        return true; // TODO remove comment and use the other check.
        //return stockToId.containsKey(stock) && stock.getId().equals(stockToId.get(stock));
    }
    /**
     * Constructor of the {@link Stock}.
     * @param article linked to the {@link Stock}.
     * @param expirationDate linked to the {@link Stock}.
     * @return a {@link Result} indicating errors.
     */
    public Result<Stock> createStock(final Article article,
                                     @Nullable final Date expirationDate) {
          return Result.of(article)
                       .require(articleManager::checkId)
                       .require(this::checkNotFinishedBeer)
                       .map(a -> new StockImpl(a, expirationDate)); // TODO register the id and require it was not registered yet.
    }
    /**
     * Constructor of the {@link BeerStock}.
     * @param beerArticle linked to this {@link BeerStock}.
     * @param expirationDate linked to this {@link BeerStock}.
     * @param batch linked to this {@link BeerStock}.
     * @return a {@link Result} indicating errors.
     */
    public Result<BeerStock> createBeerStock(final BeerArticle beerArticle,
                                  @Nullable final Date expirationDate,
                                  final Batch batch) { // TODO register and require it was not registered yet.
        return Result.of(beerArticle)
                     .require(articleManager::checkId)
                     .map(ba -> new BeerStockImpl(ba, expirationDate, batch));
        // return Result.of(new BeerStockImpl(beerArticle, expirationDate, batch));
    }

    /**
     * Return a boolean denoting whether the article is not a {@link BeerArticle}.
     * @param article to be checked.
     * @return a boolean denoting whether the article is not a {@link BeerArticle}.
     */
    private boolean checkNotFinishedBeer(final Article article) {
        return article.getArticleType() != ArticleType.FINISHED_BEER;
    }

}
