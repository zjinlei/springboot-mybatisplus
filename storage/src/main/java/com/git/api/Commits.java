package com.git.api;

public class Commits {

    private Commit commit;

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }


    static class Commit {
        private String message;
        private Author author;
        private Verification verification;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public Verification getVerification() {
            return verification;
        }

        public void setVerification(Verification verification) {
            this.verification = verification;
        }
    }

    static class Author {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class Verification {
        private boolean verified;
        private String reason;

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
