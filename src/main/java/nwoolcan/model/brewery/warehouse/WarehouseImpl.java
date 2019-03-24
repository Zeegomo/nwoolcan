package nwoolcan.model.brewery.warehouse;

import nwoolcan.model.brewery.warehouse.article.Article;
import nwoolcan.utils.Empty;
import nwoolcan.utils.Result;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;


/**
 * Warehouse implementation.
 */
public final class WarehouseImpl implements Warehouse {

    private static final String ARTICLE_ALREADY_REGISTERED = "The article was already registered.";
    private final Map<Stock, Stock> stocks;
    private final Set<Article> articles;

    /**
     * Constructor of the Warehouse.
     */
    // Package protected
    WarehouseImpl() {
        this.stocks = new HashMap<>();
        this.articles = new HashSet<>();
    }

    @Override
    public Result<Set<Stock>> getStocks(final QueryStock queryStock) {
        return Result.of(stocks.values()
                               .stream()
                               // remove when article is present in queryStock but the article of
                               // the current stock is different from the one of the query.
                               .filter(stock -> !(queryStock.getArticle().isPresent()
                                   && !queryStock.getArticle().get().equals(stock.getArticle())))
                               // remove those without expiration date if expiresBefore is present.
                               .filter(stock -> !(queryStock.getExpiresBefore().isPresent()
                                   && !stock.getExpirationDate().isPresent()))
                               // remove those with expiration date after upper limit of
                               // expiration date
                               .filter(stock -> !(queryStock.getExpiresBefore().isPresent()
                                               && stock.getExpirationDate().get()
                                                       .after(queryStock.getExpiresBefore().get())))
                               // remove those with expiration date before the lower limit
                               // Stocks with no expiration date are left, since it is like they
                               // won't ever expire.
                               .filter(stock -> !(queryStock.getExpiresAfter().isPresent()
                                               && stock.getExpirationDate().isPresent()
                                               && stock.getExpirationDate().get()
                                                       .before(queryStock.getExpiresAfter().get())))
                               .collect(Collectors.toSet()));
    }

    @Override
    public Result<Set<Article>> getArticles(final QueryArticle queryArticle) {
        return Result.of(articles); //TODO once queryArticle will be implemented.
    }

    @Override
    public Result<Empty> addArticle(final Article newArticle) {
        return Result.ofEmpty()
                     .require(() -> !this.articles.contains(newArticle), new IllegalArgumentException(ARTICLE_ALREADY_REGISTERED))
                     .peek(res -> updateArticles(newArticle));
    }

    @Override
    public Result<Empty> addRecord(final Article article, @Nullable final Date expirationDate, final Record record) {
        return Result.of(new StockImpl(article, expirationDate))
                     .peek(stock -> updateArticles(article))
                     .flatMap(stock -> Result.of(this.getStock(stock)))
                     .flatMap(stock -> stock.addRecord(record))
                     .toEmpty();
    }

    @Override
    public Result<Empty> addRecord(final Article article, final Record record) {
        return addRecord(article, null, record);
    }
    /**
     * Adds an {@link Article} to the {@link Set} of articles if not present yet.
     * @param article the article to be added.
     */
    private void updateArticles(final Article article) {
        if (!this.articles.contains(article)) {
            this.articles.add(article);
        }
    }

    /**
     * Given a {@link Stock}, if present it will return the {@link Stock} present in the
     * {@link Warehouse}, otherwise it adds it to the {@link Warehouse}.
     * @param stock to check and return.
     * @return the {@link Stock} in the {@link Warehouse} if present, otherwise it returns the given
     * {@link Stock} adding it to the {@link Warehouse}.
     */
    private Stock getStock(final Stock stock) {
        if (!this.stocks.containsKey(stock)) {
            this.stocks.put(stock, stock);
        }
        return this.stocks.get(stock);
    }

}
