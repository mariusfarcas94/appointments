package com.motioncare.user.security;

import java.util.HashSet;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.motioncare.user.model.User;
import com.motioncare.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
		User user = userRepository.findByUsername( username )
				.orElseThrow( () -> new UsernameNotFoundException(
						"User not found with username: " + username ) );

		// Load roles for the user
		user.setRoles( new HashSet<>( userRepository.findRolesByUserId( user.getId() ) ) );

		return user;
	}
}
