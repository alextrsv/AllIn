package alex.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @Column(name = "msg_token")
    private String msgToken;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<Category> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<UsersMessengers> usMes;

    //реализация связи М:М (диалоги - пользователи)
    @OneToMany(mappedBy = "user")
    private Collection<DialogToUser> dialogToUserCollection;


    public User() {
    }

    protected User(String firstName, String lastName, String token, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
        this.phone = phone;
    }

    @JsonIgnore
    public Collection<DialogToUser> getDialogToUserCollection() {
        return dialogToUserCollection;
    }

    public void setDialogToUserCollection(Collection<DialogToUser> dialogToUserCollection) {
        this.dialogToUserCollection = dialogToUserCollection;
    }

    public Collection<UsersMessengers> getUsMes() {
        return usMes;
    }

    public void setUsMes(Collection<UsersMessengers> usMes) {
        this.usMes = usMes;
    }

    public String getMsgToken() {
        return msgToken;
    }

    public void setMsgToken(String msgToken) {
        this.msgToken = msgToken;
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

    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }
}
