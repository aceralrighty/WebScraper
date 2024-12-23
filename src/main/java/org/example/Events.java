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

    public String getFighter1() {
        // If no comma is present, return the entire fighter string
        if (!fighter.contains(",")) {
            return fighter;
        }
        return fighter.split(",")[0];
    }

    public String getFighter2() {
        // If no comma is present, return an empty string
        if (!fighter.contains(",")) {
            return "";
        }
        return fighter.split(",")[1];
    }

    @Override
    public String toString() {
        return "{\"Win/Loss\":\"" + win_loss +
                "\", \"Fighters\":\"" + fighter +
                "\", \"KD\":\"" + kd +
                "\", \"STR\":\"" + str +
                "\", \"TD\":\"" + td +
                "\", \"SUB\":\"" + sub +
                "\", \"Weight Class\":\"" + weight_class +
                "\", \"Method\":\"" + method +
                "\", \"Round\":\"" + round +
                "\", \"Time\":\"" + time +
                "\"}";
    }
}
