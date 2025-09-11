package Let.s._Talk.Service;

import Let.s._Talk.Model.Person;
import Let.s._Talk.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServImpl implements PersonService{

    @Autowired
    private PersonRepository personRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Person createPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        if(!person.getRole().startsWith("ROLE_")){
            person.setRole("ROLE_" + person.getRole());
        }
        return personRepo.save(person);
    }

    @Override
    public Person getPersonByUsername(String email) {
        Person person = personRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if(person == null){
            throw new RuntimeException("User not found");
        }

        return person;
    }

    @Override
    public Person getPersonById(Long id) {
        Person p = personRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        return p;
    }

    @Override
    public List<Person> getAllUsers() {
        return personRepo.findAll();
    }

    @Override
    public boolean passwordMatches(String rawPass, String encodedPass) {
        return passwordEncoder.matches(rawPass, encodedPass);
    }
}
