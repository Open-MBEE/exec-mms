package org.openmbee.mms.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class BaseRequest {

    @Schema(nullable = true)
    private String source;

    @Schema(nullable = true)
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
