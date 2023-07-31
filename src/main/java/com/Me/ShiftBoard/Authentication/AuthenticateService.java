package com.Me.ShiftBoard.Authentication;

import com.Me.ShiftBoard.Employee.Employee;
import com.Me.ShiftBoard.Employee.EmployeeService;
import com.Me.ShiftBoard.Sequence.SequenceGeneratorService;
import com.Me.ShiftBoard.User.User;
import com.Me.ShiftBoard.User.UserRepository;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.Role;
import com.Me.ShiftBoard.Util.Status;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final UserRepository userRepository;
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SequenceGeneratorService sequenceGeneratorService;
    public AuthenticationResponse register(User user) {

        if(userRepository.findUserByEmail(user.getEmail()).isPresent())
        {
            return AuthenticationResponse.builder()
                    .token("").build();
        }

            User u = User.builder()
                    .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                    .email(user.getEmail())
                    .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(u);

            String token = jwtService.generateToken(u);

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
    }

    public AuthenticationResponse authenticate(User user) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        var u = userRepository.findUserByEmail(user.getEmail()).orElseThrow();
        String token = jwtService.generateToken(u);

        return AuthenticationResponse.builder()
                .token(token)
                .build();

    }

    public Response change(User user, long userId) {
        Response response = new Response();

        if(!userRepository.existsByUserId(userId))
        {
            response.setOperationStatus(Status.Failure,"NO such user found!!");
            return response;
        }
        User original = userRepository.findUserByUserId(userId);

        if(!original.getEmail().equals(user.getEmail()) && original.getRole().equals(Role.EMPLOYEE))
        {
            if(!employeeService.existsEmployeeByEmail(original.getEmail()))
            {
                response.setOperationStatus(Status.Failure,"No such employee found!!");
                return response;
            }

            Employee e = employeeService.getEmployeeByEmail(original.getEmail());
            e.setEmail(user.getEmail());
            employeeService.saveEmployee(e);
        }

        original.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        original.setEmail(user.getEmail());

        userRepository.save(original);
        response.setOperationStatus(Status.Success,"S");
        return response;

    }
}

@Data
@Builder
class AuthenticationResponse{
    private String token;
}