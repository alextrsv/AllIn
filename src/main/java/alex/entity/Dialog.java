package alex.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dialog")
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "api_dialog_id")
    private long apiDialogId;

    @Column(name = "note")
    String note;

    /*Каждый диалог принадлежит определенному мессенджеру
    в одном мессенджере может быть множество диалогов
    */
    @ManyToOne
    @JoinColumn(name = "messenger_id")
    private Messenger messenger;

    //for DTO
    @Transient
    private int messId;

    //реализация связи М:М (диалоги - пользователи)
    @OneToMany(mappedBy = "dialog")
    private Collection<DialogToUser> dialogToUserCollection;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getApiDialogId() {
        return apiDialogId;
    }

    public void setApiDialogId(long apiDialogId) {
        this.apiDialogId = apiDialogId;
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

    public int getMessId() {
        return messId;
    }

    public void setMessId(int messId) {
        this.messId = messId;
    }
}
