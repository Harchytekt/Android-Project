package be.heh.models;

/**
 * This class creates a user.
 *
 * @author DUCOBU Alexandre
 */
public class User {

    private int id;
    private String lastname;
    private String firstname;
    private String password;
    private String email;
    private String rights;

    public User(){}

    /**
     * Constructor of a user.
     * It initializes the details of the user.
     *
     * @param lastname
     *      The lastname of the user
     * @param lastname
     *      The lastname of the user
     * @param password
     *      The password of the user
     * @param email
     *      The email address of the user
     * @param rights
     *      The rights of the user
     *
     */
    public User(String lastname, String firstname, String password, String email, String rights) {
        this.lastname  = lastname;
        this.firstname = firstname;
        this.password  = password;
        this.email     = email;
        this.rights    = rights;
    }

    /**
     * Constructor of a user.
     * It initializes the details of the user.
     *
     * @param lastname
     *      The lastname of the user
     * @param lastname
     *      The lastname of the user
     * @param password
     *      The password of the user
     * @param email
     *      The email address of the user
     *
     */
    public User(String lastname, String firstname, String password, String email) {
        this.lastname  = lastname;
        this.firstname = firstname;
        this.password  = password;
        this.email     = email;
        rights         = "2";
    }

    /**
     * Get the ID of the user.
     *
     * @return the ID of the user.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID of the user
     *
     * @param id
     *      The ID of the user.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the lastname of the user.
     *
     * @return the lastname of the user.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Set the lastname of the user
     *
     * @param lastname
     *      The lastname of the user.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Get the firstname of the user.
     *
     * @return the firstname of the user.
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Set the firstname of the user
     *
     * @param firstname
     *      The firstname of the user.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Get the password of the user.
     *
     * @return the password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password of the user
     *
     * @param password
     *      The password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the email address of the user.
     *
     * @return the email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email address of the user
     *
     * @param email
     *      The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the rights of the user.
     *
     * @return the rights of the user.
     */
    public String getRights() {
        return rights;
    }

    /**
     * Set the rights of the user
     *
     * @param rights
     *      The rights of the user.
     */
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

    /**
     * Return a string representation of the user.
     *
     * @return a string representation of the user.
     */
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
