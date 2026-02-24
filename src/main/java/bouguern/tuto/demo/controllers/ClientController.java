package bouguern.tuto.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import bouguern.tuto.demo.records.ApiResponse;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<String>> getClientProfile() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Client profile accessed", "Client data here..."));
    }
}
