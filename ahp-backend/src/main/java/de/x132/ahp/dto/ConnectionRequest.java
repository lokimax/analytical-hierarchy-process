package de.x132.ahp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequest {
    
    @NotBlank(message = "Source node name is required")
    private String sourceNodeName;
    
    @NotBlank(message = "Target node name is required")
    private String targetNodeName;
}
