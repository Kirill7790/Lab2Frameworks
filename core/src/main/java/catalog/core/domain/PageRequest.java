package catalog.core.domain;


public class PageRequest {
    private final int page;
    private final int size;
    private final String sort;

    public PageRequest(int page, int size, String sort) {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0 || size > 100) throw new IllegalArgumentException("size must be in 1..100");
        this.page = page;
        this.size = size;
        this.sort = (sort == null || sort.isBlank()) ? "title,asc" : sort;
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public String getSort() { return sort; }
    public int getOffset() { return page * size; }
}
