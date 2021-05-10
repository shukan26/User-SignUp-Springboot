package com.example.cisco.assignment.controller;
import com.example.cisco.assignment.model.UserContactInfo;
import com.example.cisco.assignment.entity.UserInfo;
import com.example.cisco.assignment.model.UserInfoRequest;
import com.example.cisco.assignment.repository.UserRepository;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        ResponseEntity<List<UserInfo>> response = new ResponseEntity(userRepo.findAll(), HttpStatus.FOUND);
        return response;
    }

    //TODO fix error
    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable String id) {
        ResponseEntity<UserInfo> response = new ResponseEntity(userRepo.findById((UUID.fromString(id))), HttpStatus.FOUND);
        return response;
    }

    @GetMapping("/users/contact")
    public ResponseEntity<List<UserContactInfo>> getUserByContact() {
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


    //TODO Add validations and precondition
    @GetMapping("/users/city/{city}/tech/{tech}")
    public ResponseEntity<List<UserInfo>> getUserByLocationAndTech(@PathVariable String city, @PathVariable String tech) {
        Preconditions.checkNotNull(StringUtils.isNotEmpty(city),"Please Enter City");
        Preconditions.checkNotNull(StringUtils.isNotEmpty(tech),"Please Enter Tech");
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

    //TODO Add validations and precondition
    @GetMapping("/users/city/{city}/profession/{profession}")
    public ResponseEntity<List<UserInfo>> getUserByLocationAndProfession(@PathVariable String city, @PathVariable String profession) {
        Preconditions.checkNotNull(StringUtils.isNotEmpty(city),"Please Enter City");
        Preconditions.checkNotNull(StringUtils.isNotEmpty(profession),"Please Enter Tech");
        List<UserInfo> UsersByCityAndProfession = userRepo.findByCityAndProfession(city, profession);
        ResponseEntity<List<UserInfo>> response = new ResponseEntity(UsersByCityAndProfession, HttpStatus.OK);
        return response;
    }

    //TODO: Add validations and precondition
    @GetMapping("/users/env-tech/devEnvironment/{devEnvironment}/tech/{tech}")
    public ResponseEntity<List<UserInfo>> getUserByEnvTech(@PathVariable String devEnvironment, @PathVariable String tech) {
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

    //TODO : Add validations and precondition
    //TODO : how to handle casing while adding a user and matching them?
    @PostMapping("/addUser")
    public ResponseEntity<UserInfo> addUser(@RequestBody UserInfoRequest request) {
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

    //TODO - Fix update method. Fetch user and don't overwrite it as a new user
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserInfo> updateUser(@PathVariable String id , @RequestBody UserInfoRequest request) {

        if(StringUtils.isEmpty(id)) {
            return new ResponseEntity("Please enter UserId", HttpStatus.BAD_REQUEST);
        }

        Optional <UserInfo> userInfo = userRepo.findById(UUID.fromString(id));

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

        ResponseEntity<UserInfo> response = new ResponseEntity(userInfo,HttpStatus.CREATED);
        return response;
    }
}
