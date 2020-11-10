package org.openmbee.mms.core.objects;

public class ElementsSearchResponse extends ElementsResponse {
    private Integer total;
    private Integer rejectedTotal;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getRejectedTotal() {
        return rejectedTotal;
    }

    public void setRejectedTotal(Integer rejectedTotal) {
        this.rejectedTotal = rejectedTotal;
    }
}
