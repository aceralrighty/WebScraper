package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Events {
    private String win_loss;
    private String fighter;
    private String kd;
    private String str;
    private String td;
    private String sub;
    private String weight_class;
    private String method;
    private String round;
    private String time;

    @Override
    public String toString() {
        return "{\"win_loss\":\"" + win_loss +
                "\", \"fighter\":\"" + fighter +
                "\", \"kd\":\"" + kd +
                "\", \"str\":\"" + str +
                "\", \"td\":\"" + td +
                "\", \"sub\":\"" + sub +
                "\", \"weight_class\":\"" + weight_class +
                "\", \"method\":\"" + method +
                "\", \"round\":\"" + round +
                "\", \"time\":\"" + time +
                "\"}";
    }
}
