package fzzyhmstrs.emi_loot.util;

public class FloatTrimmer {

    public static String trimFloatString(float weight){
        return trimFloatString(weight,1);
    }

    public static String trimFloatString(float weight, int places){
        String f = Float.toString(weight);
        int fDot = f.indexOf(".");
        String fTrim;
        if (fDot > 0) {
            if (weight < 10f) {
                fTrim = f.substring(0, Math.min(f.length(), fDot + 1 + places));
            } else {
                fTrim = f.substring(0, Math.min(f.length(), fDot));
            }
        } else {
            if (weight < 10f) {
                fTrim = f + ".0";
            } else {
                fTrim = f;
            }
        }
        return fTrim;
    }

}
