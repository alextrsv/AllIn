package alex.model;

import java.util.Date;

public class TelegramMess {
    private long mess_id;
    private String mess_text;
    private int mess_time;
    private String mess_direct;

    public TelegramMess(long mess_id, String mess_text, int mess_time, String mess_direct) {
        this.mess_id = mess_id;
        this.mess_text = mess_text;
        this.mess_time = mess_time;
        this.mess_direct = mess_direct;
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

    public String getMess_direct() {
        return mess_direct;
    }
}

