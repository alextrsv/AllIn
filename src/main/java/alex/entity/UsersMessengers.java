package alex.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "users_messengers")
public class UsersMessengers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "position")
    private int position;


    ///////////////////////////////////////////////////////////////////////////
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(optional = false)
    @JoinColumn(name = "messenger_id")
    public Messenger messenger;
///////////////////////////////////////////////////////////////////////////

    public Integer getMessengerId() {
        return messenger.getId();
    }

    @JsonIgnore
    public Messenger getMessenger(){
        return messenger;
    }


    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) { this.position = position; }
}
