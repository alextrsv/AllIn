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

    @JsonIgnore
    @OneToMany(mappedBy = "messenger")
    private Collection<UsersMessengers> usMes;


    //в каждом мессенджере множество диалогов
    @OneToMany(mappedBy = "messenger")
    private Collection<Dialog> dialogs;


    @Transient
    private boolean isActivated;

    @Transient
    private int position;


    public Messenger() {
    }


    public Messenger(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
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
