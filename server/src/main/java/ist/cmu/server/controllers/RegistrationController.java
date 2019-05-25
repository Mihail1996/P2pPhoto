package ist.cmu.server.controllers;


import ist.cmu.server.service.LoggerFactory;
import ist.cmu.server.service.UserService;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResponseEntity addUser(String username, String password, String token) {
        LoggerFactory.log("Registration username: " + username);
        if (userService.addUser(username, password, token)) {
            return new ResponseEntity(HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/checkConnection", method = RequestMethod.GET)
    public ResponseEntity checkConnection() {
        return new ResponseEntity(HttpStatus.OK);
    }


}


