package util.sms;

public enum BulkSmsStatus {
    UNKNOWN(null),
    NO_SENDING(-2),
    IN_PROGRESS(0),
    INVALID_CREDENTIALS(23);

    private Integer status;

    BulkSmsStatus(Integer status) {
        this.status = status;
    }

    public static BulkSmsStatus getStatus(Integer code) {
        BulkSmsStatus rStat = BulkSmsStatus.UNKNOWN;
        for (BulkSmsStatus stat : BulkSmsStatus.values()) {
            if (code != null && stat.status != null && stat.status.intValue() == code.intValue()) {
                rStat = stat;
                break;
            }
        }

        return rStat;
    }
}
