package db.entities.user;

public enum UserType {
    ADMIN(1), AUTHOR(2), RESPONDER(3);
    private final int id;

    UserType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
