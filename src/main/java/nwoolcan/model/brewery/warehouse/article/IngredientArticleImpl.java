package nwoolcan.model.brewery.warehouse.article;

import nwoolcan.model.utils.UnitOfMeasure;
import nwoolcan.utils.Result;

import java.util.Objects;

/**
 * Ingredient Article implementation.
 */
public final class IngredientArticleImpl extends AbstractArticle implements IngredientArticle {

    private final IngredientType ingredientType;

    /**
     * Constructor of the class IngredientArticleImpl.
     * @param id identifier of this article. It should be generated by the {@link ArticleManager}.
     * @param name the name of the ingredient article.
     * @param unitOfMeasure used for this article.
     * @param ingredientType the type of ingredient article.
     */
    // Package-private
    IngredientArticleImpl(final int id,
                                 final String name,
                                 final UnitOfMeasure unitOfMeasure,
                                 final IngredientType ingredientType) {
        super(id, name, unitOfMeasure);
        this.ingredientType = ingredientType;
    }

    @Override
    public Result<IngredientArticle> toIngredientArticle() {
        return Result.of(this);
    }

    @Override
    public ArticleType getArticleType() {
        return ArticleType.INGREDIENT;
    }

    @Override
    public IngredientType getIngredientType() {
        return this.ingredientType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ingredientType);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && obj instanceof IngredientArticleImpl
            && this.ingredientType == ((IngredientArticleImpl) obj).getIngredientType();
    }

    @Override
    public String toString() {
        return "[IngredientArticleImpl] { "
            + "Id:" + getId() + ", "
            + "Name:" + getName() + ", "
            + "IngredientType:" + getIngredientType().toString() + ", "
            + "UnitOfMeasure:" + getUnitOfMeasure()
            + "}";
    }

}
