package com.jpmc.midascore;


import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.repository.UserRepository;


@Component
public class UserPopulator {
    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository;


    public void populate() {
        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");
        for (String userLine : userLines) {
            String[] userData = userLine.split(", ");
            UserRecord user = new UserRecord(userData[0], Float.parseFloat(userData[1]));
            userRepository.save(user);
        }
    }
}
