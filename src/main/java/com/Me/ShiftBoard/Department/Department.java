package com.Me.ShiftBoard.Department;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Transient
    public static final String SEQUENCE_NAME = "department_sequence";

    @Id
    private long departmentId;
    private String name;
    private String email;
    private int noOfEmployees;
    private List<Long> employeesId;

    public final static List<String> properties = List.of("name","departmentId","email","noOfEmployees");

    public boolean checkAnyNull()
    {
        if(this.getName() != null && this.getEmail() != null) return false;
        return true;
    }
}
