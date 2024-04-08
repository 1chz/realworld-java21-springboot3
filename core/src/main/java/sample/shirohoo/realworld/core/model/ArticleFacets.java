package sample.shirohoo.realworld.core.model;

/**
 * The {@code ArticleFacets} class represents a set of facets for querying articles, including tag,
 * author, favorited status, page, and size.
 *
 * <p>Note: The {@code page} parameter is 0-based, i.e., it starts from 0.
 *
 * @param tag The tag for filtering articles.
 * @param author The author for filtering articles.
 * @param favorited The favorited status for filtering articles.
 * @param page The 0-based page number for paginated results.
 * @param size The size of the result set, limited to a range between 0 and 50.
 * @see ArticleFacets#ArticleFacets(int, int)
 */
public record ArticleFacets(String tag, String author, String favorited, int page, int size) {
    public ArticleFacets {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than 0.");
        }
        if (size < 0 || size > 50) {
            throw new IllegalArgumentException("size must be between 0 and 50.");
        }
    }

    public ArticleFacets(int page, int size) {
        this(null, null, null, page, size);
    }
}
