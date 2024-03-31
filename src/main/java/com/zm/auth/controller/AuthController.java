package com.zm.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zm.auth.dto.LoginReqDto;
import com.zm.auth.dto.LoginRespDto;
import com.zm.auth.dto.RoleDto;
import com.zm.auth.dto.UserRoleDto;
import com.zm.auth.jwt.JwtUtil;
import com.zm.auth.models.Roles;
import com.zm.auth.models.User;
import com.zm.auth.repository.RolesRepository;
import com.zm.auth.repository.UserRepository;
import com.zm.auth.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@PostMapping("/login")
	public ResponseEntity<?> userLogin(@RequestBody LoginReqDto loginDto) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		String email = authentication.getName();
		System.out.println(email);
		
		UserDetails user = userDetailsService.loadUserByUsername(email);
		
		String token = jwtUtil.generateToken(user);
		System.out.println(token);
		
		System.out.println(authentication.getAuthorities());
		
		LoginRespDto resp = new LoginRespDto();
		resp.setToken(token);
		resp.setUsername(email);
		
		return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/user/save")
	public ResponseEntity<?> saveUser(@RequestBody User user){
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return new ResponseEntity<>("user saved", HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/user/new/role/save")
	public ResponseEntity<?> addUserRoles(@RequestBody UserRoleDto userRoleDto){
		User user = userRepository.findByEmail(userRoleDto.getUsername());
		for(String r : userRoleDto.getRoles()) {
			Roles role = rolesRepository.findByRole(r);
			if(role != null) {
				user.getRoles().add(role);
			}
		}
		userRepository.save(user);
		return new ResponseEntity<>("user roles saved", HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/new/role/save")
	public ResponseEntity<?> addNewRole(@RequestBody RoleDto roleDto){
		Roles role = new Roles();
		role.setRole(roleDto.getRoleName());
		role.setRoleDesc(roleDto.getRoleDesc());
		rolesRepository.save(role);
		return new ResponseEntity<>("role saved", HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/role")
	public ResponseEntity<?> getRole(@RequestParam Integer id){
		Optional<Roles> role = rolesRepository.findById(id);
		if(role.isPresent())
		 return new ResponseEntity<>(role.get(), HttpStatus.ACCEPTED);
		else
			return new ResponseEntity<>("invalid role id", HttpStatus.BAD_REQUEST);
	}
	
	

}
