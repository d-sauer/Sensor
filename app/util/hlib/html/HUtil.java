package util.hlib.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import util.StringUtils;
import util.hlib.HData;
import util.hlib.HDataContainer;
import util.hlib.HDataValue;
import util.hlib.HDataValueType;

public class HUtil {

    /**
     * Check if two HtmlTag objects is equals, same class, and non of them is
     * null, and call equals on object.
     * 
     * @param ht1
     * @param ht2
     * @return
     */
    public static boolean equals(HData ht1, HData ht2) {
        if (ht1 != null && ht2 != null) {
            if (ht1.getClass() == ht2.getClass()) {
                return ht1.equals(ht2);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Compare HtmlTag elements by same attributes names.
     * 
     * @param ht1
     * @param ht2
     * @param dataValues
     * @return
     */
    public static <E extends HData> boolean equalsByValue(E ht1, E ht2, String dataValue) {
        if (ht1 != null && ht2 != null) {
            boolean isEquals = false;
            HDataValue hdv1 = ht1.getValue(dataValue);
            HDataValue hdv2 = ht2.getValue(dataValue);

            if (hdv1 != null && hdv2 != null) {
                if (hdv1 instanceof HDataValueType && hdv2 instanceof HDataValueType) {
                    HDataValueType hdvt1 = (HDataValueType) hdv1;
                    HDataValueType hdvt2 = (HDataValueType) hdv2;

                    isEquals = hdvt1.getValue().equals(hdvt2.getValue());
                } else {
                    isEquals = hdv1.getValue().equals(hdv2.getValue());
                }
            } else {
                isEquals = false;
            }

            return isEquals;
        } else
            return false;
    }

    public static <E extends HData> boolean isSet(E hdata, String dataValue) {
        boolean isValidAndSet = true;

        HDataValue hdv = hdata.getValue(dataValue);
        if (hdv != null) {
            Object o = hdv.getValue();
            if (o != null) {
                if (o instanceof HDataValueType) {
                    HDataValueType hdvt = (HDataValueType) o;
                    isValidAndSet = hdvt.isSet();
                }
                else {
                    String s = o.toString();
                    if (s.length() == 0)
                        isValidAndSet = false;
                }
            }
            else {
                isValidAndSet = false;
            }

        }
        else {
            isValidAndSet = false;
        }

        return isValidAndSet;
    }

    /**
     * return true if HData is not NULL, and if dataValue is defined, id data is
     * HDataValueType, check isSet,
     * otherwise, convert to string and string is not empty
     * 
     * @param hdata
     * @param dataValue
     * @return
     */
    public static <E extends HData> boolean isSet(E hdata, String... dataValues) {
        boolean isValidAndSet = true;
        if (hdata != null) {
            if (dataValues != null && dataValues.length != 0) {
                for (String dataValue : dataValues) {
                    isValidAndSet = isSet(hdata, dataValue);

                    if (isValidAndSet == false)
                        break;
                }
            }
            else
                isValidAndSet = false;
        } else {
            isValidAndSet = false;
        }

        return isValidAndSet;
    }

    /**
     * return true if HData is not NULL, and if dataValue is defined, id data is
     * HDataValueType, check isSet,
     * otherwise, convert to string and string is not empty
     * 
     * @param hdata
     * @param dataValue
     * @return
     */
    public static <E extends HData> boolean isSet(String dataValues, E... hdata) {
        boolean isSet = true;
        if (dataValues == null || hdata == null) {
            isSet = false;
        } else {
            for (HData hd : hdata) {
                if (hd == null) {
                    isSet = false;
                    break;
                } else {
                    HDataValue hdv = hd.getValue(dataValues);
                    if (hdv.isSet() == false) {
                        isSet = false;
                        break;
                    }
                }
            }
        }

        return isSet;
    }

    /**
     * Check if dataValue on data {@link #isSet(HData, String)}, and if not
     * blank (not blank ignore whitespace)
     * 
     * @param dataValues
     * @param hdata
     * @return
     */
    public static <E extends HData> boolean isBlank(E hdata, String dataValues) {
        boolean isBlank = false;
        if (dataValues == null || hdata == null) {
            isBlank = true;
        } else {
            if (hdata == null) {
                isBlank = true;
            } else {
                HDataValue hdv = hdata.getValue(dataValues);
                if (hdv.isSet() == false) {
                    isBlank = true;
                } else {
                    if (hdv instanceof HDataValueType) {
                        isBlank = StringUtils.isBlank(((HDataValueType) hdv).getValue());
                    } else {
                        isBlank = StringUtils.isBlank(hdv.getValue().toString());
                    }
                }
            }
        }

        return isBlank;
    }

    public static <E extends HData> boolean isBlank(String dataValues, E... hdata) {
        boolean isBlank = true;
        if (hdata != null) {
            for (HData hd : hdata) {
                isBlank = isBlank(hd, dataValues);

                if (isBlank == false)
                    break;
            }
        } else {
            isBlank = false;
        }

        return isBlank;
    }

    public static <E extends HData> boolean isNotBlank(E hdata, String dataValues) {
        boolean isNotBlank = true;
        isNotBlank = !isBlank(hdata, dataValues);
        return isNotBlank;
    }

    public static <E extends HData> boolean isNotBlank(String dataValues, E... hdata) {
        boolean isNotBlank = true;
        if (hdata != null) {
            for (HData hd : hdata) {
                isNotBlank = isNotBlank(hd, dataValues);

                if (isNotBlank == false)
                    break;
            }
        } else {
            isNotBlank = false;
        }

        return isNotBlank;
    }

    /**
     * Get all elements by data value name, e.g. 'id', 'type'.
     * Use regex to filter elements by value in data value
     * 
     * @param fromContainer
     * @param dataValueName
     * @param regex
     * @return
     */
    public static List<HData> getAllHData(HDataContainer fromContainer, String dataValueName, String regex) {
        List<HData> lHd = new ArrayList<HData>();

        List<HData> tmpHDC = fromContainer.get();
        for (HData hd : tmpHDC) {
            HDataValue<String> hdv = hd.getValue(dataValueName);
            if (hdv != null) {

                String value = hdv.getValue();
                if (value.matches(regex))
                    lHd.add(hd);
            }
        }

        return lHd;
    }

    /**
     * Get first element by data value name, e.g. 'id', 'type'.
     * Use regex to filter element by value in data value
     * 
     * @param fromContainer
     * @param dataValueName
     * @param regex
     * @return
     */
    public static HData getHData(HDataContainer fromContainer, String dataValueName, String regex) {
        HData hdata = null;
        
        List<HData> tmpHDC = fromContainer.get();
        for (HData hd : tmpHDC) {
            HDataValue<String> hdv = hd.getValue(dataValueName);
            if (hdv != null) {
                
                String value = hdv.getValue();
                if (value.matches(regex)) {
                    hdata = hd;
                    break;
                }
            }
        }
        
        return hdata;
    }
}
