package alex.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

///////////////////////////////////////////////////////////////////////////
    @JsonIgnore
    @OneToMany(mappedBy = "messenger", fetch = FetchType.EAGER)
    private Collection<UsersMessengers> usMes;
///////////////////////////////////////////////////////////////////////////


    @Transient
    private boolean isActivated;


    public Messenger() {
    }


    public Messenger(String title) {
        this.title = title;
    }

    public Collection<UsersMessengers> getUsMes() {
        return usMes;
    }

    public void setUsMes(Collection<UsersMessengers> usMes) {
        this.usMes = usMes;
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
