package alex.model;

import java.util.Date;

public class TelegramMess {
    private long mess_id;
    private String mess_content;
    private int mess_date;
    private String mess_type;

    public TelegramMess(long mess_id, String mess_content, int mess_date, String mess_type) {
        this.mess_id = mess_id;
        this.mess_content = mess_content;
        this.mess_date = mess_date;
        this.mess_type = mess_type;
    }

    public long getMess_id() {
        return mess_id;
    }

    public String getMess_content() {
        return mess_content;
    }

    public int getMess_date() {
        return mess_date;
    }

    public String getMess_type() {
        return mess_type;
    }
}

