package com.shopme.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.Category;

public class CategoryExporter extends AbstractExporter{
	
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", "csv","categories_");
		
		ICsvBeanWriter csvWritter = new CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Category ID", "Category Name", "Alias","Enabled"};
		String[] fieldMapping = {"id" , "name" , "alias","enabled"};
		
		
		csvWritter.writeHeader(csvHeader);
		
		for(Category category : listCategories)
		{
			csvWritter.write(category, fieldMapping);
		}
		csvWritter.close();
	
	}

}
