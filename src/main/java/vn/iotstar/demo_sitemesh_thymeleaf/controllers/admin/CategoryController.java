package vn.iotstar.demo_sitemesh_thymeleaf.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.iotstar.demo_sitemesh_thymeleaf.entity.Category;
import vn.iotstar.demo_sitemesh_thymeleaf.models.CategoryModel;
import vn.iotstar.demo_sitemesh_thymeleaf.services.CategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    private CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @GetMapping("/add")
    public String add(Model model) {
        CategoryModel category = new CategoryModel();
        category.setIsEdit(false);
        model.addAttribute("category", category);
        return "admin/category/add";
    }
    @PostMapping("/save")
    public ModelAndView save(ModelMap model,
                             @Valid @ModelAttribute("category") CategoryModel categoryModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ModelAndView("admin/category/add");
        }
        Category entity = new Category();
        BeanUtils.copyProperties(categoryModel, entity);
        categoryService.save(entity);
        String message="";
        if(categoryModel.getIsEdit() == true) {
            message="Category is Edited!!!";
        }else{
            message="Category is Saved!!!";
        }
        model.addAttribute("message", message);
        return new ModelAndView("forward:/admin/categories/searchpaginated", model);
    }
    @GetMapping("/edit")
    public ModelAndView edit(ModelMap model, @RequestParam("id") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        CategoryModel cateModel = new CategoryModel();
        if (optCategory.isPresent()) {
            Category entity = optCategory.get();

            BeanUtils.copyProperties(entity, cateModel);
            cateModel.setIsEdit(true);
            model.addAttribute("category", cateModel);
            return new ModelAndView("admin/category/add", model);
        }
        model.addAttribute("message", "Category is not exist!!!");
        return new ModelAndView("forward:/admin/categories/searchpaginated", model);
    }
    @GetMapping("delete")
    public ModelAndView delete(ModelMap model,@RequestParam("id") Long categoryId) {
        categoryService.deleteById(categoryId);
        model.addAttribute("message", "Category is Deleted!!!");
        return new ModelAndView("forward:/admin/categories/searchpaginated", model);
    }

    @RequestMapping("/searchpaginated")
    public String search(ModelMap model,
                         @RequestParam(name="name",required = false) String name,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size) {
        int count = (int) categoryService.count();
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
        Page<Category> resultPage = null;
        if(StringUtils.hasText(name)) {
            resultPage = categoryService.findByNameContaining(name,pageable);
            model.addAttribute("name",name);
        }else {
            resultPage = categoryService.findAll(pageable);
        }
        int totalPages = resultPage.getTotalPages();
        if(totalPages > 0) {
            int start = Math.max(1, currentPage-2);
            int end = Math.min(currentPage + 2, totalPages);
            if(totalPages > count) {
                if(end == totalPages) start = end - count;
                else if (start == 1) end = start + count;
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers",pageNumbers);
        }
        model.addAttribute("categoryPage",resultPage);
        return "admin/category/list";
    }
}
