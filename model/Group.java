package model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<User> users;

    public Group(String name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<User> getUsers() { return users; }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }
}
