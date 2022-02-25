package com.team9.questgame.gamemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Request body for POST/DELETE /api/register
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String name;
}
