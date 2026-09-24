package model;

public abstract class Administrator extends Person {
    private String adminId;

    public Administrator(String adminId, String name, String email, String phone) {
        super(name, email, phone);
        this.adminId = adminId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}