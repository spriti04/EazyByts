package Let.s._Talk.Service;

import Let.s._Talk.Model.Person;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PersonService {

    public Person createPerson(Person person);

    public Person getPersonById(Long id);

    public Person getPersonByUsername(String email);

    public List<Person> getAllUsers();

    public boolean passwordMatches(String rawPass, String encodedPass);
}
