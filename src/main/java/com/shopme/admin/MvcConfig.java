package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		String dirName = "user-photos";
		Path userPhotoDir = Paths.get(dirName);
		
		String userPhotosPath = userPhotoDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/" + dirName + "/**")
			.addResourceLocations("file:/" + userPhotosPath + "/");
		
		String categoryImagesDirname = "../category-images";
		Path categoryImagesDir = Paths.get(categoryImagesDirname);
		
		String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/category-images/**")
			.addResourceLocations("file:/" + categoryImagesPath + "/");
		
		String brandImagesDirname = "../brand-images";
		Path brandImagesDir = Paths.get(brandImagesDirname);
		
		String brandImagesPath = brandImagesDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/brand-images/**")
			.addResourceLocations("file:/" + brandImagesPath + "/");
	}
	
	

}
