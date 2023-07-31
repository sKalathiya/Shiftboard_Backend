package com.Me.ShiftBoard.User;

import com.Me.ShiftBoard.User.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,Integer> {

    Optional<User> findUserByEmail(String email);
    User deleteUserByEmail(String email);
    User findUserByUserId(long id);
    boolean existsByUserId(long id);
}
