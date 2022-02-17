package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class handles all the user objects.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class UserRegister {
    private Map<String,User> userHashMap;

    private LinkedList<User> userLinkedList;

    public UserRegister() {
        userHashMap = new HashMap<>();
        userLinkedList = new LinkedList<>();
    }

    public Map<String, User> getUserHashMap() {
        return userHashMap;
    }

    public HashMap setUserHashMap(HashMap userList) {
        this.userHashMap = userList;
        return userList;
    }

    public LinkedList<User> getUserLinkedList() {
        return userLinkedList;
    }

    public LinkedList<User> setUserLinkedList(LinkedList<User> userLinkedList) {
        this.userLinkedList = userLinkedList;
        return userLinkedList;
    }

    /**
     * Updates the HashMap and LinkedList with a new updated User object.
     * @param updatedUser
     */
    public boolean updateUser(User updatedUser) {
        boolean success = false;
        userHashMap.remove(updatedUser.getUsername());
        userHashMap.put(updatedUser.getUsername(), updatedUser);

        for (User user : userLinkedList) {
            if (user.getUsername().equals(updatedUser.getUsername())) {
                userLinkedList.remove(user);
                success=  true;
            }
        }
        userLinkedList.add(updatedUser);
        return success;
    }
}
