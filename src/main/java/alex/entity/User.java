package alex.entity;


import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "token")
    private String token;
    @Column(name = "phone")
    private String phone;

    ///////////////////////////////////////////////////////////////////////////
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<UsersMessengers> usMes;
///////////////////////////////////////////////////////////////////////////

    public User() {
    }

    protected User(String firstName, String lastName, String token, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
        this.phone = phone;
    }


    public Collection<UsersMessengers> getUsMes() {
        return usMes;
    }

    public void setUsMes(Collection<UsersMessengers> usMes) {
        this.usMes = usMes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
