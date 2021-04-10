package alex.model;


public class Dialog {
    private Long id;
    private String title;
    private String icon;
    private String note = "empty";
    private String lastMsg_text = "";
    private int lastMsg_date;
    private String lastMsg_type;
    private boolean is_favorite = false;
    private String type = "ordinary";
    private int photo_id;

    public void setType(String type) {
        this.type = type;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public String getNote() {
        return note;
    }

    public String getLastMsg_text() {
        return lastMsg_text;
    }

    public int getLastMsg_date() {
        return lastMsg_date;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public String getType() {
        return type;
    }

    public void setLastMsg_text(String lastMsg_text) {
        this.lastMsg_text = lastMsg_text;
    }

    public void setLastMsg_date(int lastMsg_date) {
        this.lastMsg_date = lastMsg_date;
    }

    public Dialog(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Dialog(Long id, String title, String icon, String lastMsg_text, int lastMsg_date) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.lastMsg_text = lastMsg_text;
        this.lastMsg_date = lastMsg_date;
    }
}
