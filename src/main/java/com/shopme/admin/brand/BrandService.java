package com.shopme.admin.brand;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.repo.brand.BrandRepository;
import com.shopme.common.entity.Brand;

@Service
@Transactional
public class BrandService {
	
	public static final int BRANDS_PER_PAGE = 2;
	
	@Autowired
	private BrandRepository brandRepository;
	
	
	public List<Brand> listBrand(){
		return (List<Brand>) brandRepository.findAll();
	}

	public List<Brand> listAll(){
		return (List<Brand>) brandRepository.findAll(Sort.by("name").ascending());
	}
	
	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String Keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE, sort);
		
		if(Keyword != null) {
			return brandRepository.search(Keyword,pageable); 
		}
		
		return brandRepository.findAll(pageable);
	}

	public Brand save(Brand brand) {
		
		return brandRepository.save(brand);
	}


	public Brand get(Integer id) {
		return brandRepository.findById(id).get();
	}


	public void delete(Integer id) throws Exception {
		Long countById = brandRepository.countBrandById(id);
		if(countById == null || countById == 0) {
			throw new Exception("could not find any brand with id " + id);
		}
		
		
		brandRepository.deleteById(id);	
	}


	public String checkUnique(Integer id, String name ) {
			boolean isCreatingNew = (id == null || id == 0);
			
			Brand BrandByName = brandRepository.findByName(name);
			if(isCreatingNew)
			{
				if(BrandByName != null)
				{
					return "DuplicateName";
				}
			}else{
				if(BrandByName != null  && BrandByName.getId() != id)
				{
					return "DuplicateName";
				}
		}
			return "ok";	
		}	

}
