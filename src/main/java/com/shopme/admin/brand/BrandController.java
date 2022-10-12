package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {

	@Autowired
	private BrandService service;
	
	@Autowired
	private CategoryService categService;
	
	
	@GetMapping("/brands")
	public String listFirstPage(Model model) {
		List<Brand> listBrands = service.listBrand();
		model.addAttribute("listBrands", listBrands);
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newCategory(Model model) {
		List<Category> lisCategories = categService.listCategoriesUsedInForm();
		
		model.addAttribute("lisCategories", lisCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create new Brand");
		
		return "brands/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String savedBrand(Brand brand, RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException
	{
		
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = service.save(brand);

			String uploadDir = "../brand-images/" + savedBrand.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			
			service.save(brand);
		}

		redirectAttributes.addFlashAttribute("message", "Brand a été sauvegardé avec succès");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name="id") Integer id,  RedirectAttributes redirectAttributes , Model model)
	{
		try {
			Brand brand = service.get(id);
			List<Category> lisCategories = categService.listCategoriesUsedInForm();
			model.addAttribute("lisCategories", lisCategories);
			model.addAttribute("brand", brand);
			model.addAttribute("pageTitle", "Update Brand ID:" + id);
			return "brands/brand_form";
		}catch(Exception e)
		{
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteUser(@PathVariable(name="id") Integer id , RedirectAttributes redirectAttributes , Model model) throws Exception
	{
		
		try {
			service.delete(id);
			String brandyDir = "../brand-images/"+id;
			FileUploadUtil.removeDir(brandyDir);
			redirectAttributes.addFlashAttribute("message", "Brand a été supprimé avec succès id_brand : " + id);
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			
		}
		
		return "redirect:/brands";
	}
}
