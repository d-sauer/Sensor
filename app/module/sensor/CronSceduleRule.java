package module.sensor;

import java.util.Calendar;
import java.util.Date;

import util.data.WDate;

public class CronSceduleRule {
    public String dan = "";
    public String mjesec = "";
    public String godina = "";
    public String sat = "";
    public String minuta = "";

    private Date baseDate;
    private Calendar cBase;
    private Calendar cNext;
    private Integer iDan;

    /**
     * 
     * dan:mjesec:godina:sat:minuta
     * 
     * @return
     */
    public String getDescriptor() {
        cleanData();

        return "dan_" + dan + ":mjesec_" + mjesec + ":godina_" + godina + ":sat_" + sat + ":min_" + minuta;
    }

    /**
     * dan:mjesec:godina:sat:minuta
     * 
     * @param descriptor
     * @return
     */
    public CronSceduleRule setDescriptor(String descriptor) {
        if (descriptor != null) {
            String[] tmp = descriptor.split(":");
            if (tmp.length == 5) {
                this.dan = tmp[0].replace("dan_", "");
                this.mjesec = tmp[1].replace("mjesec_", "");
                this.godina = tmp[2].replace("godina_", "");
                this.sat = tmp[3].replace("sat_", "");
                this.minuta = tmp[4].replace("min_", "");
            }
        }
        return this;
    }

    public Date getNext() {
        if (baseDate == null)
            baseDate = new Date();

        return getNext(baseDate);
    }

    private void cleanData() {
        // clean data
        dan = dan.replace("dan_", "");
        mjesec = mjesec.replace("mjesec_", "");
        godina = godina.replace("godina_", "");
        sat = sat.replace("sat_", "");
        minuta = minuta.replace("min_", "");
    }

    public Date getNext(Date afterDate) {
        cleanData();

        cBase = Calendar.getInstance();
        cBase.setTime(afterDate);
        cBase.set(Calendar.SECOND, 0);
        cBase.set(Calendar.MILLISECOND, 0);

        if (dan != null && mjesec != null && godina != null && sat != null && minuta != null && dan.length() != 0 && mjesec.length() != 0 && godina.length() != 0 && sat.length() != 0 && mjesec.length() != 0) {
            cNext = Calendar.getInstance();
            cNext.setTimeInMillis(cBase.getTimeInMillis());

            setMinute();

            setSati();

            boolean bDan = setDefDan();

            boolean bMjesec = setDefMjesec();

            boolean bGodina = setDefGodina();

            baseDate = cNext.getTime();
            return cNext.getTime();
        } else {
            return null;
        }

    }

    private boolean setMinute() {
        return setMinute(minuta);
    }

    private boolean setMinute(String tMin) {
        Integer iMin = Integer.parseInt(tMin);
        cNext.set(Calendar.MINUTE, iMin);
        return true;
    }

    private boolean setSati() {
        return setSati(sat);
    }

    private boolean setSati(String tSat) {
        if (tSat.equals("all") && cNext.getTimeInMillis() <= cBase.getTimeInMillis()) {
            cNext.roll(Calendar.HOUR_OF_DAY, 1);
        }
        else if (!tSat.equals("all")) {
            Integer iSat = Integer.parseInt(tSat);
            if (iSat != cNext.get(cNext.get(Calendar.HOUR)))
                cNext.set(Calendar.HOUR_OF_DAY, iSat);
        }
        return true;
    }

    private boolean setDefDan() {
        return setDan(dan);
    }

    private boolean setDan(String tDan) {
        boolean bDan = false;

        if (tDan.equals("all") && cNext.getTimeInMillis() <= cBase.getTimeInMillis()) {
            cNext.roll(Calendar.DAY_OF_MONTH, 1);
            bDan = true;
        }
        else if (tDan.equals("end")) {
            Integer maxD = cNext.getActualMaximum(Calendar.DAY_OF_MONTH);
            cNext.set(Calendar.DAY_OF_MONTH, maxD);
            bDan = true;
        }
        else if (tDan.equals("pon") || tDan.equals("ut") || tDan.equals("sri") || tDan.equals("cet") || tDan.equals("pet") || tDan.equals("sub") || tDan.equals("ned")) {
            Integer mj = cNext.get(Calendar.MONTH);

            if (tDan.equals("pon") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                bDan = true;
            }
            else if (tDan.equals("ut") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                bDan = true;
            }
            else if (tDan.equals("sri") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                bDan = true;
            }
            else if (tDan.equals("cet") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                bDan = true;
            }
            else if (tDan.equals("pet") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                bDan = true;
            }
            else if (tDan.equals("sub") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                bDan = true;
            }
            else if (tDan.equals("ned") && cNext.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                cNext.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                bDan = true;
            }
            
            // ako je datum manji, tada je to dan u sljedećem tjednu
            if (cNext.getTimeInMillis() <= cBase.getTimeInMillis()) {
                cNext.add(Calendar.DAY_OF_MONTH, 7);

            }

            // Ako je promjena dana u tjednu izmjenila mjesec, postavi stari mjesec i korigiraj na prvi sljedeći dan u tjednu u tom mjesecu
            Integer mjN = cNext.get(Calendar.MONTH);
            if (mj != mjN) {
                if (mj > mjN)
                    cNext.add(Calendar.DAY_OF_MONTH, 7);
                else
                    cNext.add(Calendar.DAY_OF_MONTH, -7);
            }

        }
        else if (!tDan.equals("all")) {
            iDan = Integer.parseInt(tDan);
            Integer iMaxDan = cNext.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (iDan != cNext.get(Calendar.DAY_OF_MONTH) && iDan <= iMaxDan) {
                cNext.set(Calendar.DAY_OF_MONTH, iDan);
                bDan = true;
            }
        }

        if (bDan == true && sat.equals("all")) {
            cNext.set(Calendar.HOUR_OF_DAY, 0);
        }

        return bDan;
    }

    private boolean setDefMjesec() {
        return setMjesec(mjesec);
    }

    private boolean setMjesec(String tMjesec) {
        boolean bMjesec = false;
        
        if (tMjesec.equals("all")) {
            if (cNext.getTimeInMillis() <= cBase.getTimeInMillis()) {
                cNext.roll(Calendar.MONTH, 1);
                bMjesec = true;

                if (!dan.equals("all") && !dan.equals("end")) {
                    for (int i = 0; i < 12; i++) {
                        Integer maxDay = cNext.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if (iDan > maxDay) {
                            cNext.roll(Calendar.MONTH, 1);
                        } else {
                            cNext.set(Calendar.DAY_OF_MONTH, iDan);
                            break;
                        }
                    }
                }
            }
        }
        else if (!tMjesec.equals("all")) {
            Integer iMjesec = (Integer.parseInt(tMjesec) - 1);

            if (iMjesec != cNext.get(Calendar.MONTH)) {
                cNext.set(Calendar.MONTH, iMjesec);
                bMjesec = true;
            }
        }

        if (bMjesec == true) {
            if (dan.equals("all"))
                cNext.set(Calendar.DAY_OF_MONTH, 1);
            else if (dan.equals("end")) {
                Integer maxD = cNext.getActualMaximum(Calendar.DAY_OF_MONTH);
                cNext.set(Calendar.DAY_OF_MONTH, maxD);
            }
            else if (dan.equals("pon") || dan.equals("ut") || dan.equals("sri") || dan.equals("cet") || dan.equals("pet") || dan.equals("sub") || dan.equals("ned")) {
                cNext.set(Calendar.DAY_OF_MONTH, 1);
                setDan(dan);
            }

            if (sat.equals("all"))
                cNext.set(Calendar.HOUR_OF_DAY, 0);
        }

        return bMjesec;
    }

    private boolean setDefGodina() {
        return setGodina(godina);
    }

    private boolean setGodina(String tGodina) {
        boolean bGodina = false;
        if (tGodina.equals("all") && cNext.getTimeInMillis() <= cBase.getTimeInMillis()) {
            cNext.add(Calendar.YEAR, 1);
            bGodina = true;
        }
        else if (!tGodina.equals("all") && cNext.getTimeInMillis() <= cBase.getTimeInMillis()) {
            Integer iGodina = Integer.parseInt(tGodina);

            if (iGodina != cNext.get(Calendar.YEAR)) {
                cNext.set(Calendar.YEAR, iGodina);
                bGodina = true;
            }
        }

        if (bGodina == true) {
            if (mjesec.equals("all"))
                cNext.set(Calendar.MONTH, 0);

            if (dan.equals("all"))
                cNext.set(Calendar.DAY_OF_MONTH, 1);
            else if (dan.equals("end")) {
                Integer maxD = cNext.getActualMaximum(Calendar.DAY_OF_MONTH);
                cNext.set(Calendar.DAY_OF_MONTH, maxD);
            }

            if (sat.equals("all"))
                cNext.set(Calendar.HOUR_OF_DAY, 0);
        }

        return bGodina;
    }

    private void pn() {
        WDate tmp = new WDate("dd.MM.yyyy HH:mm");
        tmp.set(cNext.getTimeInMillis());
        System.out.println(tmp.getFormatted());
    }

    /**
     * test...
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CronSceduleRule csr = new CronSceduleRule();
        String rule = "dan_ned:mjesec_all:godina_all:sat_17:min_30";
        System.out.println("rule:" + rule);
        csr.setDescriptor(rule);

        String sTrenutnoVrijeme = "17.02.2013 17:28:00";
        WDate trenutnoVrijeme = new WDate(WDate.ddMMyyyy_HHmmss);
        trenutnoVrijeme.set(sTrenutnoVrijeme);

        Date d = csr.getNext(trenutnoVrijeme.get());
        WDate wd = new WDate(d);
        String fwd = wd.getFormatted(WDate.ddMMyyyy_HHmmss);
        System.out.println(trenutnoVrijeme.getFormatted() + " -> " + fwd);

        // for (int i = 0; i < 28; i++) {
        // d = csr.getNext();
        // wd.set(d);
        // fwd = wd.getFormatted(WDate.ddMMyyyy_HHmmss);
        // System.out.printf("%2d.                 -> %s\n", i, fwd);
        // }

    }
}