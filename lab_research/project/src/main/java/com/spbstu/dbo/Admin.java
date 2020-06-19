package com.spbstu.dbo;

/**
 *
 */
public class Admin extends UserBase {
    public Role getRole() {
        return Role.ADMIN;
    }
}
