package alex.entity;

import alex.entity.Messenger;
import alex.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name = "users_messengers")
public class UsersMessengers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "access_token")
    private Integer accessToken;


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

    public Integer getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Integer accessToken) {
        this.accessToken = accessToken;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
