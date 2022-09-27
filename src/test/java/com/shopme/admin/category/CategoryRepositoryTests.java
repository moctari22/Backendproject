package com.shopme.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import com.shopme.admin.repo.category.CategoryRepository;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		
		Category category = new Category("Category2");
		Category savedCategory = repo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(9);
		Category subCategory = new Category("Category1.2.1.2", parent);
		Category savedCategory = repo.save(subCategory);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(4).get();
		System.out.println("category name = " + category.getName());
		
		Set<Category> children = category.getChildren();
		
		for(Category subCategory : children) {
			System.out.println("category subname" + subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories =  repo.findAll();
		
		for(Category category : categories) {
			if(category.getParent() == null) {
				System.out.println(category.getName());
				Set<Category> children = category.getChildren();
				for(Category subCategory : children) {
					System.out.println("--"+subCategory.getName());
					printChildren(subCategory,1);
				}
			}
		}
	}
	
	private void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		for(Category subCategory : children) {
			for (int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName());
			printChildren(subCategory, newSubLevel);
		}
	}
	
	@Test
	public void listParentCategory()
	{
		int pageNumber = 0;
		int pageSize = 5 ;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		
		Page<Category> page = (Page<Category>) repo.findAll(pageable);
		
		List<Category> listParent = page.getContent();
		
		
		//List<Category> listParent = repo.listRootCategories();
		
		listParent.forEach(cat -> System.out.println(cat.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name  = "Category1";
		Category category = repo.findByName(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByAlias() {
		String alias  = "Category1";
		Category category = repo.findByName(alias);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(alias);
	}
	
	

}
