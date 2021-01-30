package alex.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dialog")
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    String title;

    @Column(name = "icon")
    String icon;

    @Column(name = "note")
    String note;

    /*Каждый диалог принадлежит определенному мессенджеру
    в одном мессенджере может быть множество диалогов
    */
    @ManyToOne
    @JoinColumn(name = "messenger_id")
    private Messenger messenger;


    //реализация связи М:М (диалоги - пользователи)
    @OneToMany(mappedBy = "dialog")
    private Collection<DialogToUser> dialogToUserCollection;



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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }
}
