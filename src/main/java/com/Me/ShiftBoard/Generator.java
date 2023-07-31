package com.Me.ShiftBoard;

import com.Me.ShiftBoard.Department.Department;
import com.Me.ShiftBoard.Department.DepartmentService;
import com.Me.ShiftBoard.Employee.Employee;
import com.Me.ShiftBoard.Employee.EmployeeService;
import com.Me.ShiftBoard.Util.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class Generator {


    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    public  void generateDepartment(int size) {
        int i;
        for (i = 0; i < size; i++) {
            Department department = new Department();
            department.setEmployeesId(new ArrayList<>());
            department.setNoOfEmployees(0);
            department.setEmail(getAlphaNumericString(5)+"@gmail.com");
            department.setName(getAlphaNumericString(5));

            departmentService.addDepartment(department);
        }
    }

    public void generateEmployee(int size)

    {

        int i;
        for(i=0;i<size;i++)
        {
            Employee employee = new Employee();
            employee.setId(getRandomNumber(1000,4000));
            employee.setFirstName(getAlphaNumericString(8));
            employee.setLastName(getAlphaNumericString(6));
            employee.setContactNumber("438-458-1086");
            employee.setAddress(new Address(String.valueOf(getRandomNumber(1000,3000)),getAlphaNumericString(5),getAlphaNumericString(6),getAlphaNumericString(2),String.valueOf(getRandomNumber(10000,100000))));
            String email = getAlphaNumericString(8)+"@gmail.com";
            employee.setEmail(email);
            employee.setDepartmentId(getRandomNumber(6,16));
            employeeService.addEmployee(employee);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    static String getAlphaNumericString(int n) {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (n > 0)) {

                r.append(ch);
                n--;
            }
        }

        // return the resultant string
        return r.toString();
    }


    public void changeS(){
        employeeService.updateScheduleWeekly();
    }
}