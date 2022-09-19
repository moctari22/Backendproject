package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.user.repo.RoleRepository;
import com.shopme.admin.user.repo.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;

	 @Autowired
	 private UserRepository UseRepo;
	 
	 @Autowired
	 private RoleRepository roleRepo;
	 
	 @Autowired
	 private PasswordEncoder passwordEncoder;
	
	
	
	public List<User> listAll(){
		return (List<User>) UseRepo.findAll(Sort.by("firstName").ascending());
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String Keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum-1, USERS_PER_PAGE, sort);
		
		if(Keyword != null) {
			return UseRepo.findAll(Keyword,pageable); 
		}
		
		return UseRepo.findAll(pageable);
	}
	
	public List<Role> listRoles(){
		return (List<Role>) roleRepo.findAll(); 
	}

	public User save(User user) {
		
		boolean isUpdatingUser = (user.getId() != 0);
		
		System.out.println("isUpdatingUser +++++++ "+ isUpdatingUser);
		
		if(isUpdatingUser) {
			User existingUser = UseRepo.findById(user.getId()).get();
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}else {
				encodePassword(user);
			}
		}else {
			encodePassword(user);
		}
		
		
		return UseRepo.save(user);
	}
	public User updateAccount(User userInForm) {
		User userInDB = UseRepo.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		if(userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		
		return UseRepo.save(userInDB);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public boolean isEmailUnique(Integer id ,String email)
	{
		User userByEmail = UseRepo.getUserByEmail(email);
		
		if(userByEmail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(userByEmail != null) return false;
		}else {
			if(userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public User get(Integer id) throws UserNotFoudException {
		try {
			return UseRepo.findById(id).get();
		}catch(NoSuchElementException ex){
			throw new UserNotFoudException("could not find any user with id " + id);
		}

	}
	public User getUserByEmail(String  email) throws UserNotFoudException {
		try {
			return UseRepo.getUserByEmail(email);
		}catch(NoSuchElementException ex){
			throw new UserNotFoudException("could not find any user with email address " + email);
		}

	}
	
	public void delete(Integer id) throws UserNotFoudException
	{
		Long countById = UseRepo.countUserById(id);
		if(countById == null || countById == 0) {
			throw new UserNotFoudException("could not find any user with id " + id);
		}
		
		UseRepo.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		UseRepo.updateEnabledStatus(id, enabled);
	}
	
	

}
