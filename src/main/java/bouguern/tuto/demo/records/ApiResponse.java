package bouguern.tuto.demo.records;

// Generic API response
public record ApiResponse<T>(
		
		boolean success,
        String message,
        T data
        
		) {

}
