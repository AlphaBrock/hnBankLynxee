package com.sino.hnbank.screen.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class TransDayData {
    private Integer id;

    private Integer sortNum;

    private String dataDate;

    private Date lastUpdateTime;

    private String branchCode;

    private String branchName;

    private BigDecimal accountOpenNumPub;

    private BigDecimal accountOpenNumPri;

    private BigDecimal transCountTotal;

    private BigDecimal transOutAmount;

    private BigDecimal transInAmount;

    private BigDecimal publicOutAmount;

    private BigDecimal publicInAmount;

    private BigDecimal privateOutAmount;

    private BigDecimal privateInAmount;

    private BigDecimal otherBankOutAmount;

    private BigDecimal otherBankInAmount;

    private BigDecimal otherBankInAmountNew;
    private BigDecimal otherBankOutAmountNew;

    private BigDecimal depositAmount;
    private BigDecimal loanAmount;

    public BigDecimal getOtherBankInAmountNew() {
        return otherBankInAmountNew;
    }

    public void setOtherBankInAmountNew(BigDecimal otherBankInAmountNew) {
        this.otherBankInAmountNew = otherBankInAmountNew;
    }

    public BigDecimal getOtherBankOutAmountNew() {
        return otherBankOutAmountNew;
    }

    public void setOtherBankOutAmountNew(BigDecimal otherBankOutAmountNew) {
        this.otherBankOutAmountNew = otherBankOutAmountNew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate == null ? null : dataDate.trim();
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode == null ? null : branchCode.trim();
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName == null ? null : branchName.trim();
    }

    public BigDecimal getAccountOpenNumPub() {
        return accountOpenNumPub;
    }

    public void setAccountOpenNumPub(BigDecimal accountOpenNumPub) {
        this.accountOpenNumPub = accountOpenNumPub;
    }

    public BigDecimal getAccountOpenNumPri() {
        return accountOpenNumPri;
    }

    public void setAccountOpenNumPri(BigDecimal accountOpenNumPri) {
        this.accountOpenNumPri = accountOpenNumPri;
    }

    public BigDecimal getTransCountTotal() {
        return transCountTotal;
    }

    public void setTransCountTotal(BigDecimal transCountTotal) {
        this.transCountTotal = transCountTotal;
    }

    public BigDecimal getTransOutAmount() {
        return transOutAmount;
    }

    public void setTransOutAmount(BigDecimal transOutAmount) {
        this.transOutAmount = transOutAmount;
    }

    public BigDecimal getTransInAmount() {
        return transInAmount;
    }

    public void setTransInAmount(BigDecimal transInAmount) {
        this.transInAmount = transInAmount;
    }

    public BigDecimal getPublicOutAmount() {
        return publicOutAmount;
    }

    public void setPublicOutAmount(BigDecimal publicOutAmount) {
        this.publicOutAmount = publicOutAmount;
    }

    public BigDecimal getPublicInAmount() {
        return publicInAmount;
    }

    public void setPublicInAmount(BigDecimal publicInAmount) {
        this.publicInAmount = publicInAmount;
    }

    public BigDecimal getPrivateOutAmount() {
        return privateOutAmount;
    }

    public void setPrivateOutAmount(BigDecimal privateOutAmount) {
        this.privateOutAmount = privateOutAmount;
    }

    public BigDecimal getPrivateInAmount() {
        return privateInAmount;
    }

    public void setPrivateInAmount(BigDecimal privateInAmount) {
        this.privateInAmount = privateInAmount;
    }

    public BigDecimal getOtherBankOutAmount() {
        return otherBankOutAmount;
    }

    public void setOtherBankOutAmount(BigDecimal otherBankOutAmount) {
        this.otherBankOutAmount = otherBankOutAmount;
    }

    public BigDecimal getOtherBankInAmount() {
        return otherBankInAmount;
    }

    public void setOtherBankInAmount(BigDecimal otherBankInAmount) {
        this.otherBankInAmount = otherBankInAmount;
    }

    public void setDepositAmount(BigDecimal n) {
        this.depositAmount = n;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setLoanAmount(BigDecimal n) {
        this.loanAmount = n;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
}
