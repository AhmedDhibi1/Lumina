package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Domain value object representing a paginated response.
 * Contains the page content along with pagination metadata.
 *
 * @param <T> the type of elements in the page
 */
@Data
@Builder
@AllArgsConstructor
public class PageResponse<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    /**
     * Creates a PageResponse with pagination metadata.
     *
     * @param content the content of this page
     * @param pageNumber the current page number (zero-based)
     * @param pageSize the size of the page
     * @param totalElements the total number of elements across all pages
     */
    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements) {
        validatePageNumber(pageNumber);
        validatePageSize(pageSize);
        validateTotalElements(totalElements);

        this.content = content != null ? List.copyOf(content) : Collections.emptyList();
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = calculateTotalPages(pageSize, totalElements);
    }

    /**
     * Creates an empty PageResponse.
     */
    public static <T> PageResponse<T> empty(int pageNumber, int pageSize) {
        return new PageResponse<>(Collections.emptyList(), pageNumber, pageSize, 0);
    }

    /**
     * Creates a PageResponse from a list and total count.
     */
    public static <T> PageResponse<T> of(List<T> content, PageRequest pageRequest, long totalElements) {
        return new PageResponse<>(
                content,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                totalElements
        );
    }

    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
    }

    private void validatePageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
    }

    private void validateTotalElements(long totalElements) {
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements must be non-negative");
        }
    }

    private int calculateTotalPages(int pageSize, long totalElements) {
        if (totalElements == 0 || pageSize == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    /**
     * Maps the content of this page to another type.
     *
     * @param mapper the mapping function
     * @param <U> the type of elements in the new page
     * @return a new PageResponse with mapped content
     */
    public <U> PageResponse<U> map(Function<T, U> mapper) {
        Objects.requireNonNull(mapper, "Mapper function cannot be null");
        List<U> mappedContent = content.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new PageResponse<>(mappedContent, pageNumber, pageSize, totalElements);
    }

    /**
     * Checks if this is the first page.
     */
    public boolean isFirst() {
        return pageNumber == 0;
    }

    /**
     * Checks if this is the last page.
     */
    public boolean isLast() {
        return pageNumber >= totalPages - 1;
    }

    /**
     * Checks if there is a next page.
     */
    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    /**
     * Checks if there is a previous page.
     */
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    /**
     * Checks if the page has any content.
     */
    public boolean hasContent() {
        return !content.isEmpty();
    }

    /**
     * Checks if the page is empty.
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }

    /**
     * Gets the number of elements in the current page.
     */
    public int getNumberOfElements() {
        return content.size();
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageResponse<?> that = (PageResponse<?>) o;
        return pageNumber == that.pageNumber &&
                pageSize == that.pageSize &&
                totalElements == that.totalElements &&
                totalPages == that.totalPages &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, pageNumber, pageSize, totalElements, totalPages);
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "numberOfElements=" + getNumberOfElements() +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + isFirst() +
                ", last=" + isLast() +
                '}';
    }
}