package BDD;

/**
 * Created by alexandre on 1/12/17.
 */

public class User {

    private int id;
    private String lastname;
    private String firstname;
    private String password;
    private String email;
    private String type;

    public User(){}

    public User(String lastname, String firstname, String password, String email, String type) {
        this.lastname  = lastname;
        this.firstname = firstname;
        this.password  = password;
        this.email     = email;
        this.type      = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID : "
            + Integer.toString(getId()) + "\n"+
                "Lastname : " + getLastname() + "\n" +
                "Firstname : " + getFirstname() + "\n" +
                "Password : " + getPassword() + "\n" +
                "Email : " + getEmail() + "\n" +
                "Type : " + getType());
        return sb.toString();
    }

}
