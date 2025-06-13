package com.example.dto;

public class UserDto {
    private String userId;
    private String name;
    private String email;

    public static class Builder {
        private String userId;
        private String name;
        private String email;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public UserDto build() {
            UserDto dto = new UserDto();
            dto.userId = this.userId;
            dto.name = this.name;
            dto.email = this.email;
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
