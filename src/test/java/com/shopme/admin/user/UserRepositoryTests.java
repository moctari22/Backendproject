package com.shopme.admin.user;


import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.repo.user.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userMoctar = new User("Jeanjack@gmail.com","Jean","Jack","123456");
		userMoctar.addRole(roleAdmin);
		
		User savedUser = repo.save(userMoctar);
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userIbrahima = new User("mohamadou@gmail.com","mohamadou","Moctar","123456");
		Role roleEdidator = new Role(3);
		Role roleAssistant = new Role(5);
		
		userIbrahima.addRole(roleEdidator);
		userIbrahima.addRole(roleAssistant);
		
		User savedUser = repo.save(userIbrahima);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
		
	}
	
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
		
	}
	
	@Test
	public void testGetUserById() {
		
		User userNam = repo.findById(1).get();
		System.out.println(userNam);
		assertThat(userNam).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userNam = repo.findById(1).get();
		userNam.setEnabled(true);
		userNam.setEmail("moctaribrahima94@gmail.com");
		
		repo.save(userNam);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userNam = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		
		userNam.getRoles().remove(roleEditor);
		userNam.addRole(roleSalesperson);
		
		repo.save(userNam);
		
	}
	@Test
	public void testDeleteUserById() {
		
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		
		String email = "moctaribrahima94@gmail.com";
		User user = repo.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 10;
		Long countById = repo.countUserById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisabledUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, true);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4 ;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = (Page<User>) repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "Moctar";
		int pageNumber = 0;
		int pageSize = 4 ;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = (Page<User>) repo.findAll(keyword,pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}

}
