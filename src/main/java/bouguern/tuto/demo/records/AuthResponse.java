package bouguern.tuto.demo.records;

import java.util.Set;

import bouguern.tuto.demo.constants.Role;

// Auth response (with token)
public record AuthResponse(

		String token, 
		String type, // "Bearer"
		Long userId, 
		String username, 
		String email, 
		Set<Role> roles

) {
}