package com.example.bookkeeping.beans;

import java.util.List;

public class ConsumptionBean {

    /**
     * code : 200
     * msg : 操作成功
     * data : [{"con_money":"6.0","con_record":"外快","con_refundMoney":"1","con_refundRemarks":"1","con_id":26,"con_remarks":"","con_consume":"0","con_refund":"0","con_refundTime":"1","con_userId":21,"con_dissipate":"2021-7-31","con_date":"2021-07-31 21:39:22"}]
     */

    private String code;
    private String msg;
    private List<DataDTO> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * con_money : 6.0
         * con_record : 外快
         * con_refundMoney : 1
         * con_refundRemarks : 1
         * con_id : 26
         * con_remarks :
         * con_consume : 0
         * con_refund : 0
         * con_refundTime : 1
         * con_userId : 21
         * con_dissipate : 2021-7-31
         * con_date : 2021-07-31 21:39:22
         */

        private String con_money;
        private String con_record;
        private String con_refundMoney;
        private String con_refundRemarks;
        private Integer con_id;
        private String con_remarks;
        private String con_consume;
        private String con_refund;
        private String con_refundTime;
        private Integer con_userId;
        private String con_dissipate;
        private String con_date;

        public String getCon_money() {
            return con_money;
        }

        public void setCon_money(String con_money) {
            this.con_money = con_money;
        }

        public String getCon_record() {
            return con_record;
        }

        public void setCon_record(String con_record) {
            this.con_record = con_record;
        }

        public String getCon_refundMoney() {
            return con_refundMoney;
        }

        public void setCon_refundMoney(String con_refundMoney) {
            this.con_refundMoney = con_refundMoney;
        }

        public String getCon_refundRemarks() {
            return con_refundRemarks;
        }

        public void setCon_refundRemarks(String con_refundRemarks) {
            this.con_refundRemarks = con_refundRemarks;
        }

        public Integer getCon_id() {
            return con_id;
        }

        public void setCon_id(Integer con_id) {
            this.con_id = con_id;
        }

        public String getCon_remarks() {
            return con_remarks;
        }

        public void setCon_remarks(String con_remarks) {
            this.con_remarks = con_remarks;
        }

        public String getCon_consume() {
            return con_consume;
        }

        public void setCon_consume(String con_consume) {
            this.con_consume = con_consume;
        }

        public String getCon_refund() {
            return con_refund;
        }

        public void setCon_refund(String con_refund) {
            this.con_refund = con_refund;
        }

        public String getCon_refundTime() {
            return con_refundTime;
        }

        public void setCon_refundTime(String con_refundTime) {
            this.con_refundTime = con_refundTime;
        }

        public Integer getCon_userId() {
            return con_userId;
        }

        public void setCon_userId(Integer con_userId) {
            this.con_userId = con_userId;
        }

        public String getCon_dissipate() {
            return con_dissipate;
        }

        public void setCon_dissipate(String con_dissipate) {
            this.con_dissipate = con_dissipate;
        }

        public String getCon_date() {
            return con_date;
        }

        public void setCon_date(String con_date) {
            this.con_date = con_date;
        }

        @Override
        public String toString() {
            return "DataDTO{" +
                    "con_money='" + con_money + '\'' +
                    ", con_record='" + con_record + '\'' +
                    ", con_refundMoney='" + con_refundMoney + '\'' +
                    ", con_refundRemarks='" + con_refundRemarks + '\'' +
                    ", con_id=" + con_id +
                    ", con_remarks='" + con_remarks + '\'' +
                    ", con_consume='" + con_consume + '\'' +
                    ", con_refund='" + con_refund + '\'' +
                    ", con_refundTime='" + con_refundTime + '\'' +
                    ", con_userId=" + con_userId +
                    ", con_dissipate='" + con_dissipate + '\'' +
                    ", con_date='" + con_date + '\'' +
                    '}';
        }
    }
}

