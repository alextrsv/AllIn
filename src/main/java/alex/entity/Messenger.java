package alex.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "messenger")
public class Messenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;
    @Column(name = "icon")
    private String icon;

    @ManyToMany(mappedBy = "messengers")
    private Collection<User> users;

    @Transient
    private boolean isActivated;

    @Transient
    private String accessToken;


    public Messenger() {
    }

    public Messenger(String title, String accessToken) {
        this.title = title;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
