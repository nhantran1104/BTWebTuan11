package vn.iotstar.demo_sitemesh_thymeleaf.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.demo_sitemesh_thymeleaf.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    Page<Category> findByNameContaining(String name, Pageable pageable);
}
