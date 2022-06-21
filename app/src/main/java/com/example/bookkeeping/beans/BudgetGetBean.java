package com.example.bookkeeping.beans;

public class BudgetGetBean {

    /**
     * code : 200
     * msg : 获取成功
     * data : {"bgId":1,"bgUserid":"1","bgMoney":"1000"}
     */

    private String code;
    private String msg;
    private DataDTO data;

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

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * bgId : 1
         * bgUserid : 1
         * bgMoney : 1000
         */

        private Integer bgId;
        private String bgUserid;
        private String bgMoney;

        public Integer getBgId() {
            return bgId;
        }

        public void setBgId(Integer bgId) {
            this.bgId = bgId;
        }

        public String getBgUserid() {
            return bgUserid;
        }

        public void setBgUserid(String bgUserid) {
            this.bgUserid = bgUserid;
        }

        public String getBgMoney() {
            return bgMoney;
        }

        public void setBgMoney(String bgMoney) {
            this.bgMoney = bgMoney;
        }
    }
}
