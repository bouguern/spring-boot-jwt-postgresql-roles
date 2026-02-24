package bouguern.tuto.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import bouguern.tuto.demo.records.ApiResponse;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getAdminDashboard() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin dashboard accessed", "Welcome Admin!"));
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getStats() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Stats retrieved", "System stats here..."));
    }
}
