package io.github.thefrsh.stratus.security;

import io.github.thefrsh.stratus.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsJpaService implements UserDetailsService
{
    private final UserJpaRepository repository;

    @Autowired
    public UserDetailsJpaService(UserJpaRepository repository)
    {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return repository.findByUsername(username)
                .map(UserDetailsJpaAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " has not been found"));
    }
}
