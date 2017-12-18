package be.heh.models;

/**
 * Created by alexandre on 1/12/17.
 */

public class User {

    private int id;
    private String lastname;
    private String firstname;
    private String password;
    private String email;
    private String rights;

    public User(){}

    public User(String lastname, String firstname, String password, String email, String rights) {
        this.lastname  = lastname;
        this.firstname = firstname;
        this.password  = password;
        this.email     = email;
        this.rights    = rights;
    }

    public User(String lastname, String firstname, String password, String email) {
        this.lastname  = lastname;
        this.firstname = firstname;
        this.password  = password;
        this.email     = email;
        rights         = "2";
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

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    /**
     * Verify if the user is a superuser.
     *
     * @return true if the user rights is 1, false otherwise.
     */
    public boolean isSuper() {
        return rights.equals("0");
    }

    /**
     * Set the user rights to super.
     */
    public void setSuper() {
        rights = "0";
    }

    /**
     * Set the user rights to normal with R/W privileges.
     */
    public void setReadWrite() {
        rights = "1";
    }

    /**
     * Set the user rights to normal with R privileges.
     */
    public void setRead() {
        rights = "2";
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
                "Type : " + getRights());
        return sb.toString();
    }

}
