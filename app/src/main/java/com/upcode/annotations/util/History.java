package com.upcode.annotations.util;

public class History {

    public static String findHistoryByTime(long currentDate, long history) {
        int difDays = (int) ((currentDate - history) / (24 * 60 * 60 * 1000));
        if (difDays == 0) {

            int difHours = (int) ((currentDate - history) / (60 * 60 * 1000));
            return getStringByDiffHours(difHours);

        } else {
            return getStringByDiffDays(difDays);
        }
    }

    private static String getStringByDiffDays(int difDays) {

        String stringDays = (difDays > 1) ? " dias " : " dia ";
        String value = difDays + stringDays + "atrás";

        if (difDays == 7) {
            return "1 semana atrás";
        } else if (difDays == 14) {
            return "2 semanas atrás";
        } else if (difDays == 21) {
            return "3 semanas atrás";
        } else if (difDays > 30 && difDays < 60) {
            return "mais de 1 mês atrás";
        }

        for (int i = 1; i < 12; i++) {
            int days = (30 * i);
            if (difDays > days && difDays < (days + 30)) {

                String months = (i == 1) ? " mês" : " meses";

                return "mais de " + i + months + " atrás";
            }
        }

        if (difDays > (365)) {
            return "há mais de um ano atrás";
        }

        return value;
    }

    private static String getStringByDiffHours(int difHours) {
        if (difHours == 0) {
            return "agora mesmo";
        } else if (difHours == 1) {
            return "1 hora atrás";
        } else {
            return difHours + " horas atrás";
        }
    }

}
