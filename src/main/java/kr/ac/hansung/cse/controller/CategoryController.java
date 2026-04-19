package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // 카테고리 전체 목록 조회 후 뷰에 전달
    @GetMapping
    public String listCategories(Model model) {
        // 모든 카테고리 정보 모델에 저장
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList";
    }

    // 신규 카테고리 생성을 위한 화면 이동
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm";
    }

    // 실제 카테고리 등록 데이터 처리
    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute CategoryForm categoryForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 데이터 검증 오류가 있으면 화면으로 돌아감
        if (bindingResult.hasErrors()) {
            return "categoryForm";
        }
        try {
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "등록 완료");
        } catch (DuplicateCategoryException e) {
            // 중복 시 에러 메시지
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm";
        }
        return "redirect:/categories";
    }

    // 툭정 ID 카테고리 삭제 요청
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "삭제 완료");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }  return "redirect:/categories";
    }
}