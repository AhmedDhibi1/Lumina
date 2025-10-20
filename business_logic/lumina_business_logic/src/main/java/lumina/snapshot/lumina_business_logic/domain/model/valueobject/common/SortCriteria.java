package lumina.snapshot.lumina_business_logic.domain.model.valueobject.common;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * Domain value object representing sorting criteria for paginated queries.
 * Encapsulates the field to sort by and the sort direction.
 */
@Data
@Builder
public class SortCriteria {

    private final String field;
    private final SortOrder order;

    /**
     * Creates sorting criteria.
     *
     * @param field the field name to sort by
     * @param order the sort order (ASC or DESC)
     * @throws IllegalArgumentException if field is null or blank
     */
    public SortCriteria(String field, SortOrder order) {
        validateField(field);
        Objects.requireNonNull(order, "Sort order cannot be null");

        this.field = field.trim();
        this.order = order;
    }

    /**
     * Creates ascending sort criteria.
     */
    public static SortCriteria asc(String field) {
        return new SortCriteria(field, SortOrder.ASC);
    }

    /**
     * Creates descending sort criteria.
     */
    public static SortCriteria desc(String field) {
        return new SortCriteria(field, SortOrder.DESC);
    }

    private void validateField(String field) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("Sort field cannot be null or empty");
        }
    }

    /**
     * Checks if sorting is in ascending order.
     */
    public boolean isAscending() {
        return order == SortOrder.ASC;
    }

    /**
     * Checks if sorting is in descending order.
     */
    public boolean isDescending() {
        return order == SortOrder.DESC;
    }

    /**
     * Creates a new SortCriteria with reversed order.
     */
    public SortCriteria reverse() {
        return new SortCriteria(field, order.reverse());
    }

    public String getField() {
        return field;
    }

    public SortOrder getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortCriteria that = (SortCriteria) o;
        return field.equals(that.field) && order == that.order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, order);
    }

    @Override
    public String toString() {
        return field + " " + order;
    }
}

