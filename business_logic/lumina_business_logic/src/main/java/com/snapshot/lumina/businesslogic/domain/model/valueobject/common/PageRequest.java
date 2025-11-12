package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import java.util.Objects;

/**
 * Domain value object representing pagination request parameters.
 * Encapsulates page number, size, and sorting criteria with business validation rules.
 */
public class PageRequest {

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    private final int pageNumber;
    private final int pageSize;
    private final SortCriteria sortCriteria;

    /**
     * Creates a PageRequest with specified pagination parameters.
     *
     * @param pageNumber zero-based page number
     * @param pageSize number of items per page
     * @param sortCriteria sorting criteria (can be null for no sorting)
     * @throws IllegalArgumentException if validation fails
     */
    public PageRequest(int pageNumber, int pageSize, SortCriteria sortCriteria) {
        validatePageNumber(pageNumber);
        validatePageSize(pageSize);

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortCriteria = sortCriteria;
    }

    /**
     * Creates a PageRequest without sorting.
     */
    public PageRequest(int pageNumber, int pageSize) {
        this(pageNumber, pageSize, null);
    }

    /**
     * Creates a default PageRequest (page 0, size 20, no sorting).
     */
    public static PageRequest defaultRequest() {
        return new PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, null);
    }

    /**
     * Creates a PageRequest with default size.
     */
    public static PageRequest of(int pageNumber) {
        return new PageRequest(pageNumber, DEFAULT_PAGE_SIZE, null);
    }

    /**
     * Creates a PageRequest with sorting.
     */
    public static PageRequest of(int pageNumber, int pageSize, SortCriteria sortCriteria) {
        return new PageRequest(pageNumber, pageSize, sortCriteria);
    }

    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException(
                    "Page number must be non-negative, got: " + pageNumber
            );
        }
    }

    private void validatePageSize(int pageSize) {
        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("Page size must be between %d and %d, got: %d",
                            MIN_PAGE_SIZE, MAX_PAGE_SIZE, pageSize)
            );
        }
    }

    /**
     * Calculates the offset for database queries.
     */
    public int getOffset() {
        return pageNumber * pageSize;
    }

    /**
     * Creates a new PageRequest for the next page.
     */
    public PageRequest next() {
        return new PageRequest(pageNumber + 1, pageSize, sortCriteria);
    }

    /**
     * Creates a new PageRequest for the previous page.
     */
    public PageRequest previous() {
        if (pageNumber == 0) {
            return this;
        }
        return new PageRequest(pageNumber - 1, pageSize, sortCriteria);
    }

    /**
     * Creates a new PageRequest for the first page.
     */
    public PageRequest first() {
        return new PageRequest(0, pageSize, sortCriteria);
    }

    /**
     * Checks if this is the first page.
     */
    public boolean isFirst() {
        return pageNumber == 0;
    }

    /**
     * Checks if sorting is applied.
     */
    public boolean hasSorting() {
        return sortCriteria != null;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public SortCriteria getSortCriteria() {
        return sortCriteria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageRequest that = (PageRequest) o;
        return pageNumber == that.pageNumber &&
                pageSize == that.pageSize &&
                Objects.equals(sortCriteria, that.sortCriteria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, sortCriteria);
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", sortCriteria=" + sortCriteria +
                '}';
    }
}
