package Let.s._Talk.Controller;

import Let.s._Talk.DTO.LoginRequest;
import Let.s._Talk.Model.Person;
import Let.s._Talk.Service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personServ;

    @PostMapping("/create")
    public ResponseEntity<Person> createPerson(@RequestBody Person person){
        Person person1 = personServ.createPerson(person);

        return new ResponseEntity<>(person1, HttpStatus.CREATED);
    }

    @GetMapping("/getPerson/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable Long id){
        Person person = personServ.getPersonById(id);

        return new ResponseEntity<>(person, HttpStatus.OK);

    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Person>> getAllPerson(){
        List<Person> persons = personServ.getAllUsers();

        return ResponseEntity.ok(persons);
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> logInHandler(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        try {
            Person person = personServ.getPersonByUsername(loginRequest.getEmail());

            // Verify password manually
            if(!personServ.passwordMatches(loginRequest.getPassword(), person.getPassword())){
                return ResponseEntity.status(401).body("Invalid email or password");
            }

            // Create session manually
            request.getSession(true).setAttribute("user", person);

            return ResponseEntity.ok(person.getName() + " Logged in successfully");

        } catch(Exception e){
            return ResponseEntity.status(401).body("Invalid email or password");
        }





//        Person person = personServ.getPersonByUsername(auth.getName());
//
//        return ResponseEntity.ok(person.getName() + " Logged in successfully");
    }
}
