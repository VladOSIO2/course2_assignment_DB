package db;

public enum DBUserType {
    ADMIN(1), AUTHOR(2), RESPONDER(3);
    private final int id;

    DBUserType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
