package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.repo.category.CategoryRepository;
import com.shopme.admin.user.UserNotFoudException;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

@Service
@Transactional
public class CategoryService {
	
	public static  int ROOT_CATEGORIES_PER_PAGE = 4;


	 @Autowired
	 private CategoryRepository categRepo;
	 
	 
	 public Category save(Category category)
	 {
		 return categRepo.save(category);
	 }
	 
	 public List<Category> listByPage(CategoryPageInfo pageInfo,int pageNum , String sortDir , String keyword){
		 	Sort sort = Sort.by("name");
		 	
		 	if(sortDir.equals("asc"))
		 	{
		 		sort = sort.ascending();
		 	}else {
		 		sort = sort.descending();
		 	}
		 	
		 	Pageable pageable = PageRequest.of(pageNum -1, ROOT_CATEGORIES_PER_PAGE, sort);
		 	Page<Category> pageCaregories = null;
		 	if(keyword != null && !keyword.isEmpty())
		 	{
		 		 pageCaregories = categRepo.search(keyword,pageable);
		 	}else {
		 		pageCaregories = categRepo.findRootCategories(pageable);
		 	}
	
		 	List<Category> rootCaregories =  pageCaregories.getContent();
		 	
		 	pageInfo.setTotalElements(pageCaregories.getTotalElements());
		 	pageInfo.setTotalPages(pageCaregories.getTotalPages());
		 	
		 	if(keyword != null && !keyword.isEmpty())
		 	{
		 		List<Category> searchResult = pageCaregories.getContent();
		 		for(Category category : searchResult) {
		 			category.setHasChildren(category.getChildren().size() > 0);
		 		}
		 		return searchResult;
		 	}else {
		 		return listHierarchicalCategories(rootCaregories,sortDir);
		 	}
		}
	 
	 public List<Category> listByPage( String sortDir){
		 	Sort sort = Sort.by("name");
		 	
		 	if(sortDir.equals("asc"))
		 	{
		 		sort = sort.ascending();
		 	}else {
		 		sort = sort.descending();
		 	}
		 	

		 	
		 	List<Category> rootCaregories  = categRepo.findRootCategories(sort);

			return listHierarchicalCategories(rootCaregories,sortDir);
		}
	 
	 
	 /*
	 
		public Page<Category> listByPage(int pageNum, String sortField, String sortDir, String Keyword){
			Sort sort = Sort.by(sortField);
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			
			Pageable pageable = PageRequest.of(pageNum-1, CATEGORY_PER_PAGE, sort);
			
			if(Keyword != null) {
				return categRepo.findAll(Keyword,pageable); 
			}
			
			return categRepo.findAll(pageable);
		}*/
		
		
		
		public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
			categRepo.updateEnabledStatus(id, enabled);
		}
		
		public void delete(Integer id) throws Exception 
		{
			Long countById = categRepo.countCategoryById(id);
			if(countById == null || countById == 0) {
				throw new Exception("could not find any category with id " + id);
			}
			
			categRepo.deleteById(id);
		}

		public List<Category> listHierarchicalCategories( List<Category>  rootCategories, String sortDir) {
			List<Category> hierarchicalCategories = new ArrayList<>(); 
			
			for(Category rootCategory : rootCategories) {
				
				hierarchicalCategories.add(Category.CopyFull(rootCategory));
				
				Set<Category> children = sortSubCategories(rootCategory.getChildren(),sortDir);
				
				for(Category subCategory : children) {
					String subCategoryName = "--"+subCategory.getName();
					hierarchicalCategories.add(Category.copyFull(subCategory, subCategoryName));
					listSubHierarchicalCategories(hierarchicalCategories,subCategory,1,sortDir);
				}
			}
			
			return hierarchicalCategories;
			
		}
		
		private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel,String sortDir)
		{
			Set<Category> children = sortSubCategories(parent.getChildren(),sortDir);
			int newSubLevel = subLevel + 1;
			
			for(Category subCategory : children) {
				String name = "";
				for (int i = 0; i < newSubLevel; i++) {
					name += "--";
				}
				hierarchicalCategories.add(Category.copyFull(subCategory,name+subCategory.getName()));
				listSubHierarchicalCategories(hierarchicalCategories,subCategory, newSubLevel,sortDir);
			}
		}
		

		public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUserdInForm = new ArrayList<>();
		Iterable<Category> categpriesInDB =	categRepo.findRootCategories(Sort.by("name").ascending());
		
		for(Category category : categpriesInDB) {
			if(category.getParent() == null) {
				
				categoriesUserdInForm.add(Category.CopyIdAndName(category));
				Set<Category> children = sortSubCategories(category.getChildren());
				
				for(Category subCategory : children) {
					String subCategoryName = "--"+subCategory.getName();
					categoriesUserdInForm.add(Category.CopyIdAndName(subCategory.getId(), subCategoryName));
					listChildren(categoriesUserdInForm,subCategory,1);
				}
			}
		}
		
		return categoriesUserdInForm;
		}
		
		private void listChildren(List<Category> categoriesUserdInForm,Category parent, int subLevel) {
			int newSubLevel = subLevel + 1;
			Set<Category> children = sortSubCategories(parent.getChildren());
			for(Category subCategory : children) {
				String name = "";
				for (int i = 0; i < newSubLevel; i++) {
					name += "--";
				}
				categoriesUserdInForm.add(Category.CopyIdAndName(subCategory.getId(),name+subCategory.getName()));
				listChildren(categoriesUserdInForm,subCategory, newSubLevel);
			}
		}
		
		public Category get(Integer id) throws Exception {
			try {
				return categRepo.findById(id).get();
			}catch(NoSuchElementException ex){
				throw new Exception("could not find any category with id " + id);
			}

		}
		
		
		public String checkUnique(Integer id, String name, String alias ) {
			boolean isCreatingNew = (id == null || id == 0);
			
			Category categoryByName = categRepo.findByName(name);
			if(isCreatingNew)
			{
				if(categoryByName != null)
				{
					return "DuplicateName";
				}else {
					Category categoryAlias = categRepo.findByAlias(alias);
					if(categoryAlias != null)
					{
						return "DuplicateAlias";
					}
				}
			}else{
				if(categoryByName != null  && categoryByName.getId() != id)
				{
					return "DuplicateName";
				}
				Category categoryAlias = categRepo.findByAlias(alias);
				if(categoryAlias != null && categoryAlias.getId() != id)	
				{
					return "DuplicateAlias";
				}
		}
			return "ok";	
		}	

		private SortedSet<Category> sortSubCategories( Set<Category> children)
		{
			return sortSubCategories(children,"asc");
		}
		private SortedSet<Category> sortSubCategories( Set<Category> children, String sortDir)
		{
			SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

				@Override
				public int compare(Category cat1, Category cat2) {
					if(sortDir.equals("asc")) {
						return cat1.getName().compareTo(cat2.getName());
					}else {
						return cat2.getName().compareTo(cat1.getName());
					}
					
				}
			});
			sortedChildren.addAll(children);
			return sortedChildren;
			
		}
}
