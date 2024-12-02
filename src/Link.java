class Link {
    private String originalUrl;
    private String shortUrl;
    private String userUuid;
    private int visitLimit;
    private long expirationTime;

    public Link(String originalUrl, String shortUrl, String userUuid, int visitLimit, long expirationTime) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.userUuid = userUuid;
        this.visitLimit = visitLimit;
        this.expirationTime = expirationTime;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public int getVisitLimit() {
        return visitLimit;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void decrementVisitLimit() {
        if (visitLimit > 0) {
            visitLimit--;
        }
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public boolean isLimitReached() {
        return visitLimit == 0;
    }

    public void changeLimit(int limit) {
        this.visitLimit = limit;
    }
}
