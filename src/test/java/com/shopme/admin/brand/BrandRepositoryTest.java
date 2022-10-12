package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repo.brand.BrandRepository;
import com.shopme.admin.repo.category.CategoryRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
	
	@Autowired
	private BrandRepository repo;
	
	@Autowired
	private CategoryRepository categRepo;

	
	@Test
	public void testCreateBrand() {

		Category category1 = new Category(24);
		Category category2 = new Category(29);
		Brand brand = new Brand("Samsung");
		
		brand.setLogo("brand-logo.png");
		brand.getCategories().add(category1);
		brand.getCategories().add(category2);
		
		Brand saveBrand = repo.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void findAllBrandsTest() {
		 Iterable<Brand> allBrands = repo.findAll();
		 allBrands.forEach(System.out::println);
		 assertThat(allBrands).isNotEmpty();
	}
	
	@Test
	public void countBrandRepoTest()
	{
		Integer val = 3;
		Long countBrand = repo.countBrandById(val);
		assertThat(countBrand).isNotNull();
	}

}
