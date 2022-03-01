package com.team9.questgame.gamemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body for POST/DELETE /api/register
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private boolean confirmed;
    private String name;
}
