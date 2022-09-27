package com.shopme.admin.repo.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category,Integer> {
	
	public Category findByName(String name);
	
	public Category findByAlias(String name);
	
	public Long countCategoryById(Integer id);
	
	public void deleteById(Integer id);
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1% ")
	public Page<Category> search(String keyword, Pageable pageable);
	
	
	@Query("UPDATE Category c SET c.enabled = ?2 where c.id = ?1")
	@Modifying 
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL ")
	public Page<Category> findRootCategories(Pageable pageable);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort); 

}
