package alex.dto;

import alex.entity.Messenger;

public class DTOMessenger extends Messenger {
    int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
