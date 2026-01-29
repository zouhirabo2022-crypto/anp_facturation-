package org.example.anpfacturationbackend.dto;

public class DashboardStatsDTO {
    private Double totalHt;
    private Double totalTr;
    private Double totalTva;
    private Double totalTtc;
    
    // Counts by status
    private Long countBrouillon;
    private Long countValidee;
    private Long countPayee;
    
    // Amounts by status
    private Double amountBrouillon;
    private Double amountValidee;
    private Double amountPayee;

    public Double getTotalHt() {
        return totalHt;
    }

    public void setTotalHt(Double totalHt) {
        this.totalHt = totalHt;
    }

    public Double getTotalTr() {
        return totalTr;
    }

    public void setTotalTr(Double totalTr) {
        this.totalTr = totalTr;
    }

    public Double getTotalTva() {
        return totalTva;
    }

    public void setTotalTva(Double totalTva) {
        this.totalTva = totalTva;
    }

    public Double getTotalTtc() {
        return totalTtc;
    }

    public void setTotalTtc(Double totalTtc) {
        this.totalTtc = totalTtc;
    }

    public Long getCountBrouillon() {
        return countBrouillon;
    }

    public void setCountBrouillon(Long countBrouillon) {
        this.countBrouillon = countBrouillon;
    }

    public Long getCountValidee() {
        return countValidee;
    }

    public void setCountValidee(Long countValidee) {
        this.countValidee = countValidee;
    }

    public Long getCountPayee() {
        return countPayee;
    }

    public void setCountPayee(Long countPayee) {
        this.countPayee = countPayee;
    }

    public Double getAmountBrouillon() {
        return amountBrouillon;
    }

    public void setAmountBrouillon(Double amountBrouillon) {
        this.amountBrouillon = amountBrouillon;
    }

    public Double getAmountValidee() {
        return amountValidee;
    }

    public void setAmountValidee(Double amountValidee) {
        this.amountValidee = amountValidee;
    }

    public Double getAmountPayee() {
        return amountPayee;
    }

    public void setAmountPayee(Double amountPayee) {
        this.amountPayee = amountPayee;
    }
}
