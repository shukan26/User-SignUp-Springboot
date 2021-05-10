package com.example.cisco.assignment.controller;
import com.example.cisco.assignment.model.UserContactInfo;
import com.example.cisco.assignment.entity.UserInfo;
import com.example.cisco.assignment.model.UserInfoRequest;
import com.example.cisco.assignment.repository.UserRepository;
import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j
@RestController
public class UserInfoController {

    @Autowired
    UserRepository userRepo;

    @RequestMapping("/")
    public String home(){
        return "Hello Assignment";
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        try {
            ResponseEntity<List<UserInfo>> response = new ResponseEntity(userRepo.findAll(), HttpStatus.FOUND);
            return response;
        }
        catch(Exception e) {
            log.error("Error when getting all users : " + e.getMessage());
            return new ResponseEntity("Error when getting all users ", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable String id) {
        try {
            ResponseEntity<UserInfo> response = new ResponseEntity(userRepo.findById((UUID.fromString(id))), HttpStatus.FOUND);
            return response;
        }
        catch(Exception e) {
            log.error("Error when getting user by Id : " + e.getMessage());
            return new ResponseEntity("Error when getting user by Id ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/contact")
    public ResponseEntity<List<UserContactInfo>> getUserByContact() {
        try {
            List<UserInfo> userInfoList = userRepo.findAll();
            List<UserContactInfo> userContactInfo = new ArrayList<>();
            userInfoList.forEach(userInfo ->{
                userContactInfo.add(UserContactInfo.builder().
                        userId(userInfo.getUserId()).
                        firstName(userInfo.getFirstName()).
                        lastName(userInfo.getLastName()).
                        email(userInfo.getEmail()).
                        twitter(userInfo.getTwitter()).
                        instagram(userInfo.getInstagram()).
                        build());

            });
            ResponseEntity<List<UserContactInfo>> response = new ResponseEntity(userContactInfo, HttpStatus.OK);
            return response;
        }
        catch(Exception e) {
            log.error("Error when getting user contacts : " + e.getMessage());
            return new ResponseEntity("Error when getting user contacts ", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/users/city/{city}/tech/{tech}")
    public ResponseEntity<List<UserInfo>> getUserByLocationAndTech(@PathVariable String city, @PathVariable String tech) {
        try {
            if(StringUtils.isEmpty(city)) {
                return new ResponseEntity("Please Enter City", HttpStatus.BAD_REQUEST);
            }
            if(StringUtils.isEmpty(tech)) {
                return new ResponseEntity("Please Enter tech", HttpStatus.BAD_REQUEST);
            }
            List<UserInfo> UsersByCity = userRepo.findByCity(city);
            List<UserInfo> result = new ArrayList<>();
            for(UserInfo userInfo : UsersByCity) {
                String[] techArray = userInfo.getTechnology().split(",");
                Set<String> techSet = new HashSet(Arrays.asList(techArray));

                if(techSet.contains(tech)) {  //TODO do Only filter out java and not javascript
                    result.add(userInfo);
                }
            }
            ResponseEntity<List<UserInfo>> response = new ResponseEntity(result, HttpStatus.OK);
            return response;
        }
        catch(Exception e) {
            log.error("Error when getting users by city and tech : " + e.getMessage());
            return new ResponseEntity("Error when getting users by city and tech ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/city/{city}/profession/{profession}")
    public ResponseEntity<List<UserInfo>> getUserByLocationAndProfession(@PathVariable String city, @PathVariable String profession) {
        try {
            if(StringUtils.isEmpty(city)) {
                return new ResponseEntity("Please Enter City", HttpStatus.BAD_REQUEST);
            }
            if(StringUtils.isEmpty(profession)) {
                return new ResponseEntity("Please Enter profession", HttpStatus.BAD_REQUEST);
            }
            List<UserInfo> UsersByCityAndProfession = userRepo.findByCityAndProfession(city, profession);
            ResponseEntity<List<UserInfo>> response = new ResponseEntity(UsersByCityAndProfession, HttpStatus.OK);
            return response;
        }
        catch(Exception e) {
            log.error("Error when getting users by city and profession : " + e.getMessage());
            return new ResponseEntity("Error when getting users by city and profession ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/env-tech/devEnvironment/{devEnvironment}/tech/{tech}")
    public ResponseEntity<List<UserInfo>> getUserByEnvTech(@PathVariable String devEnvironment, @PathVariable String tech) {
        try {
            List<UserInfo> userInfoListWithDevAndTech = new ArrayList<>();
            List<UserInfo> userInfoList = userRepo.findByDevEnvironmentContainingIgnoreCase(devEnvironment);
            for(UserInfo userInfo : userInfoList) {
                String[] devEnv = userInfo.getDevEnvironment().split(",");
                String[] technologies = userInfo.getTechnology().split(",");

                Set<String> devEnvSet = new HashSet(Arrays.asList(devEnv));
                Set<String> techSet = new HashSet(Arrays.asList(technologies));

                if(devEnvSet.stream().anyMatch(d -> d.equalsIgnoreCase(devEnvironment)) && techSet.stream().anyMatch(d -> d.equalsIgnoreCase(tech))) {
                    userInfoListWithDevAndTech.add(userInfo);
                }
            }
            ResponseEntity<List<UserInfo>> response = new ResponseEntity(userInfoListWithDevAndTech, HttpStatus.OK);
            return response;
        }
        catch(Exception e) {
            log.error("Error when getting users by env and technology : " + e.getMessage());
            return new ResponseEntity("Error when getting users by env and technology ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserInfo> addUser(@RequestBody UserInfoRequest request) {
        try {
            if(StringUtils.isEmpty(request.getEmail())) {
                return new ResponseEntity("Please Enter email address", HttpStatus.BAD_REQUEST);
            }
            if(StringUtils.isEmpty(request.getFirstName())) {
                return new ResponseEntity("Please enter first name", HttpStatus.BAD_REQUEST);
            }
            if(StringUtils.isEmpty(request.getLastName())) {
                return new ResponseEntity("Please enter last name", HttpStatus.BAD_REQUEST);
            }

            ResponseEntity<UserInfo> userAlreadyExists = new ResponseEntity("User with this email already exists ",HttpStatus.CONFLICT);
            if(!userRepo.findByEmail(request.getEmail()).isEmpty()) return userAlreadyExists;

            String technology = Joiner.on(",").join(request.getTechLists());
            String devEnvironment = Joiner.on(",").join(request.getDevEnvironmentLists());

            UserInfo userInfo =    UserInfo.builder().
                    devEnvironment((devEnvironment)).
                    city(request.getCity()).
                    firstName(request.getFirstName()).
                    lastName(request.getLastName()).
                    email(request.getEmail()).
                    twitter(request.getTwitter()).
                    instagram(request.getInstagram()).
                    profession(request.getProfession()).
                    technology(technology).
                    userId(UUID.randomUUID()).
                    build();
            userRepo.save(userInfo);

            ResponseEntity<UserInfo> response = new ResponseEntity(userInfo,HttpStatus.CREATED);
            return response;
        }
        catch(Exception e) {
            log.error("Error Adding a new user : " + e.getMessage());
            return new ResponseEntity("Error Adding a new user ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserInfo> updateUser(@PathVariable String id , @RequestBody UserInfoRequest request) {
        try {
            if(StringUtils.isEmpty(id)) {
                return new ResponseEntity("Please enter UserId", HttpStatus.BAD_REQUEST);
            }

            if(userRepo.findById(UUID.fromString(id)) == null) {
                return new ResponseEntity("User does not exist!", HttpStatus.BAD_REQUEST);
            }

            String technology = Joiner.on(",").join(request.getTechLists());
            String devEnvironment = Joiner.on(",").join(request.getDevEnvironmentLists());
            UserInfo userInfo1 =    UserInfo.builder().
                    devEnvironment((devEnvironment)).
                    city(request.getCity()).
                    firstName(request.getFirstName()).
                    lastName(request.getLastName()).
                    email(request.getEmail()).
                    twitter(request.getTwitter()).
                    instagram(request.getInstagram()).
                    profession(request.getProfession()).
                    technology(technology).
                    userId(UUID.fromString(id)).
                    build();
            userRepo.save(userInfo1);

            ResponseEntity<UserInfo> response = new ResponseEntity(userInfo1,HttpStatus.CREATED);
            return response;
        }
        catch(Exception e) {
            log.error("Error updating a new user : " + e.getMessage());
            return new ResponseEntity("Error updating a new user ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
