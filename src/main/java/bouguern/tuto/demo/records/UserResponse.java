package bouguern.tuto.demo.records;

import java.util.Set;

import bouguern.tuto.demo.constants.Role;

//User response (for profile, etc.)
public record UserResponse(
		
		Long id,
        String username,
        String email,
        Set<Role> roles,
        String createdAt
		) {

}
