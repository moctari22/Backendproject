package com.shopme.admin.brand;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.repo.brand.BrandRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Service
@Transactional
public class BrandService {
	
	@Autowired
	private BrandRepository brandRepository;
	
	
	public List<Brand> listBrand(){
		return (List<Brand>) brandRepository.findAll();
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
