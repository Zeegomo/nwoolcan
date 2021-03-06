package nwoolcan.model.brewery.warehouse.article;

import nwoolcan.model.utils.UnitOfMeasure;

/**
 * General implementation of Article. It can contains only article of MISC type.
 * Override your class for a particular Article implementation.
 */
public final class ArticleImpl extends AbstractArticle {

    /**
     * Constructor of the class. Only article of type miscellaneous can be constructed.
     * @param id the identifier of the new {@link Article}. It should be generated by the {@link ArticleManager}.
     * @param name the name of the new {@link Article}.
     * @param unitOfMeasure used for this {@link Article}.
     */
    // Package-private
    ArticleImpl(final int id, final String name, final UnitOfMeasure unitOfMeasure) {
        super(id, name, unitOfMeasure);
    }

    @Override
    public ArticleType getArticleType() {
        return ArticleType.MISC;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && obj instanceof ArticleImpl;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "[ArticleImpl]{"
            + "id=" + getId()
            + ", name='" + super.getName() + '\''
            + ", unitOfMeasure=" + getUnitOfMeasure()
            + ", articleType=" + getArticleType()
            + '}';
    }
}
