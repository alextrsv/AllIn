package alex.model;

import java.util.Date;

public class TelegramMess {
    private long mess_id;
    private String mess_text;
    private int mess_time;
    private String mess_type;

    public TelegramMess(long mess_id, String mess_text, int mess_time, String mess_type) {
        this.mess_id = mess_id;
        this.mess_text = mess_text;
        this.mess_time = mess_time;
        this.mess_type = mess_type;
    }

    public long getMess_id() {
        return mess_id;
    }

    public String getMess_text() {
        return mess_text;
    }

    public int getMess_time() {
        return mess_time;
    }

    public String getMess_type() {
        return mess_type;
    }
}

