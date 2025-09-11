package Let.s._Talk.Service;

import Let.s._Talk.Model.Person;
import Let.s._Talk.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Person> opt = personRepo.findByEmail(email);

        if(opt.isPresent()){
            Person person = opt.get();

            List<GrantedAuthority> authorities = new ArrayList<>();

            SimpleGrantedAuthority sga = new SimpleGrantedAuthority(person.getRole());
            authorities.add(sga);

            return new User(person.getEmail(), person.getPassword(), authorities);
        }
        else{
            throw new BadCredentialsException("Person doesn't exists with this username...........");
        }
    }
}
