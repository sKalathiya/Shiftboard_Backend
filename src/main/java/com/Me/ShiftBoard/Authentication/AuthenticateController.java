package com.Me.ShiftBoard.Authentication;

import com.Me.ShiftBoard.User.User;
import com.Me.ShiftBoard.Util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticateController {


    private final AuthenticateService authenticateService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerAdmin (
            @RequestBody User user
    )
    {
        return ResponseEntity.ok(authenticateService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody User user
    )
    {
        return ResponseEntity.ok(authenticateService.authenticate(user));
    }

    @PutMapping("/change/{userId}")
    public ResponseEntity<Response> change(@PathVariable long userId , @RequestBody  User user)
    {
        return ResponseEntity.ok(authenticateService.change(user,userId));
    }

}
