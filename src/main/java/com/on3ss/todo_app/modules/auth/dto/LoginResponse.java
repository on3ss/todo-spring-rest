package com.on3ss.todo_app.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        String token,
        @JsonProperty("type") String type
) {}