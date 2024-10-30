package vn.iotstar.demo_sitemesh_thymeleaf.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.iotstar.demo_sitemesh_thymeleaf.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<Category> findByName(String name);

    Page<Category> findByNameContaining(String name, Pageable pageable);

    Optional<Category> findById(Long aLong);

    void deleteAllById(Iterable<? extends Long> longs);

    List<Category> findAll(Sort sort);

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);

    <S extends Category> S save(S entity);

    long count();

    void deleteById(Long aLong);
}
