package github.oineh.monitoring.config.auth.jwt;

public enum TokenType {
    ACCESS,
    REFRESH;

    public long life() {
        if (this == ACCESS) {
            return 600;
        }
        if (this == REFRESH) {
            return 24 * 60 * 60;
        }
        return 0;
    }
}