package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.repo.category.CategoryRepository;
import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	
	@MockBean
	private CategoryRepository repo;
	
	@InjectMocks
	private CategoryService service;
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicatedName()
	{
		Integer id = null;
		String name = "Catgeory1";
		String alias = "abc";
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicatedAlias()
	{
		Integer id = null;
		String name = "abc";
		String alias = "Catgeory1";
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnok()
	{
		Integer id = null;
		String name = "abc";
		String alias = "abc";
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		assertThat(result).isEqualTo("ok");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnok()
	{
		Integer id = 1;
		String name = "Category1";
		String alias = "abc";
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		assertThat(result).isEqualTo("ok");
	}

}
