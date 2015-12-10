package company.bigtree.bigtree.entity;

/**
 * Created by shenzebang on 15/11/18.
 */
public class User {
    private boolean success;
    private String userName;

    public User(boolean success, String userName) {
        this.success = success;
        this.userName = userName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
