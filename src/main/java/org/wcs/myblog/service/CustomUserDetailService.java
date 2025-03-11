package org.wcs.myblog.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.wcs.myblog.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("No user found with email : " + email));
  }
}
