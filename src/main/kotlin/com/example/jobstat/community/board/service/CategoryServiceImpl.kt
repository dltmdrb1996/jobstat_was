package com.example.jobstat.community.board.service

import com.example.jobstat.community.board.entity.BoardCategory
import com.example.jobstat.community.board.entity.ReadBoardCategory
import com.example.jobstat.community.board.repository.CategoryRepository
import com.example.jobstat.core.error.AppException
import com.example.jobstat.core.error.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
) : CategoryService {
    companion object {
        private const val ERROR_DUPLICATE_CATEGORY = "이미 존재하는 카테고리 이름입니다"
    }

    override fun createCategory(
        name: String,
        displayName: String,
        description: String,
    ): ReadBoardCategory {
        if (categoryRepository.existsByName(name)) {
            throw AppException.fromErrorCode(
                ErrorCode.DUPLICATE_RESOURCE,
                ERROR_DUPLICATE_CATEGORY,
            )
        }
        return categoryRepository.save(BoardCategory.create(name, displayName, description))
    }

    @Transactional(readOnly = true)
    override fun getCategoryById(id: Long): ReadBoardCategory = categoryRepository.findById(id)

    @Transactional(readOnly = true)
    override fun getAllCategories(): List<ReadBoardCategory> = categoryRepository.findAll()

    override fun updateCategory(
        id: Long,
        name: String,
        displayName: String,
        description: String,
    ): ReadBoardCategory {
        val category = categoryRepository.findById(id)
        category.updateCategory(name, displayName, description)
        return categoryRepository.save(category)
    }

    override fun deleteCategory(id: Long) {
        categoryRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun isCategoryNameAvailable(name: String): Boolean = !categoryRepository.existsByName(name)
}
