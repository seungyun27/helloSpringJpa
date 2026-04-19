package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    // 모든 카테고리 목록 조회 후 반환
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 새로운 카테고리 등록 기능
    @Transactional
    public Category createCategory(String name) {
        // 카테고리 이름 중복 검사
        categoryRepository.findByName(name)
                .ifPresent(c -> { throw new DuplicateCategoryException(name);});
        // 중복이 없는 경우 새로운 엔티티 생성 및 저장
        return categoryRepository.save(new Category(name));
    }

    // 특정 카테고리 삭제 기능
    @Transactional
    public void deleteCategory(Long id) {
        long count = categoryRepository.countProductsByCategoryId(id);
        // 연결된 상품 있으면 중단
        if (count > 0) {
            throw new IllegalStateException("상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다.");
        }
        categoryRepository.delete(id);
    }
}