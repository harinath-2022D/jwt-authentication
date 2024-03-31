package com.zm.auth.service;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zm.auth.models.Roles;
import com.zm.auth.models.User;
import com.zm.auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		 User user = userRepository.findByEmail(username);
		 
		 Set<Roles> rolesList = user.getRoles();
		 String[] rolesArr = new String[rolesList.size()];
		 Iterator<Roles> itr = rolesList.iterator();
		 int j = 0;
		 while(itr.hasNext()) {
			 rolesArr[j] = itr.next().getRole();
			 j++;
		 }

	        UserDetails userDetails =
	                org.springframework.security.core.userdetails.User.builder()
	                        .username(user.getEmail())
	                        .password(user.getPassword())
	                        .roles(rolesArr)
	                        .build();
	        return userDetails;
	}

}
