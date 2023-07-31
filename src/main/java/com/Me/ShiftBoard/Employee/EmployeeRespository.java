package com.Me.ShiftBoard.Employee;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface EmployeeRespository  extends MongoRepository<Employee,Integer> , QuerydslPredicateExecutor<Employee>{
    boolean existsById(long externalId);

    Employee deleteEmployeeById(long externalId);

    Employee findEmployeeById(long eId);

    boolean existsEmployeeByIdOrEmail(long externalId, String email);

    boolean existsEmployeeByEmail(String email);

    Employee findEmployeeByEmail(String email);

}
