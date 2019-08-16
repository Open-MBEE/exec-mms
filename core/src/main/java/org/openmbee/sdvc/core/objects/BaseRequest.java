package org.openmbee.sdvc.core.objects;

public abstract class BaseRequest {

    private String source;

    private String comment;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
