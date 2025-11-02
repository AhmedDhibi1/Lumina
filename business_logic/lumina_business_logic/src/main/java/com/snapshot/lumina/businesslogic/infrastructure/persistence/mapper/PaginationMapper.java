package com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.SortCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {
    public Pageable toSpringPageable(PageRequest pageRequest) {
        if (pageRequest.hasSorting()) {
            SortCriteria sortCriteria = pageRequest.getSortCriteria();
            Sort.Direction direction =
                    sortCriteria.isAscending()
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

            Sort sort = Sort.by(direction, sortCriteria.getField());

            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPageNumber(),
                    pageRequest.getPageSize(),
                    sort
            );
        }

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }
}
