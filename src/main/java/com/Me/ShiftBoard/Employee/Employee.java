package com.Me.ShiftBoard.Employee;

import com.Me.ShiftBoard.Util.Address;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Document(collection = "Employee")
@Data
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private Address address;
    private String email;
    private long departmentId;
    private HashMap<LocalDate,Long> schedule = new HashMap<>();



    public  boolean checkAnyNull()
    {

        if(
                this.getEmail() != null &&
                        !this.getAddress().checkAnyNull() &&
                        this.getContactNumber() != null &&
                        this.getFirstName() != null &&
                        this.getLastName() != null
        )return false;
        return true;
    }

}
