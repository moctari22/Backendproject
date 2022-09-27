package com.shopme.admin.user.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.shopme.common.entity.User;

public class AbstractExporter {
	

	public void setResponseHeader(HttpServletResponse response,String contentType, String extension , String type) throws IOException {
		DateFormat deDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timeString = deDateFormat.format(new Date());
		String fileName = type + timeString + "." + extension;
		
		response.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		response.setHeader(headerKey, headerValue);
	}

}
