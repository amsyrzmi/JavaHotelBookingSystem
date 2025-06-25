public class Domain {
    private int id;
    private String type;
    private double price;
    private boolean available;

    public Domain(int id, String type, double price, boolean available) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.available = available;
    }
    // getters omitted for brevity
}

class Booking {
    private int id;
    private int userId;
    private int roomId;
    private java.sql.Date date;
    private String status;

    public Booking(int id, int userId, int roomId, java.sql.Date date, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.date = date;
        this.status = status;
    }
    // getters omitted for brevity
}
