import javax.swing.JFrame;

public abstract class User {
    protected int id;
    protected String username;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getUsername() { return username; }

    // Polymorphic factory for dashboards
    public abstract JFrame createDashboard();
}

// package‑private Customer class (same file)
class Customer extends User {
    public Customer(int id, String username) { super(id, username); }
    @Override public JFrame createDashboard() { return new DashboardFrame(this); }
}

// package‑private Admin class (same file)
class Admin extends User {
    public Admin(int id, String username) { super(id, username); }
    @Override public JFrame createDashboard() { return new DashboardFrame(this); }
}

