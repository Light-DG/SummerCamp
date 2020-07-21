package com.example.garbagesort.result;

import java.util.List;

public class Result {


    /**
     * code : 10000
     * charge : false
     * remain : 0
     * msg : 查询成功
     * result : {"garbage_info":[{"cate_name":"湿垃圾","city_id":"310000","city_name":"上海市","confidence":1,"garbage_name":"坚果炒货","ps":"投放建议：容器与外包装为可回收物"}],"message":"success","status":0}
     */

    private String code;
    private boolean charge;
    private int remain;
    private String msg;
    private ResultBean result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * garbage_info : [{"cate_name":"湿垃圾","city_id":"310000","city_name":"上海市","confidence":1,"garbage_name":"坚果炒货","ps":"投放建议：容器与外包装为可回收物"}]
         * message : success
         * status : 0
         */

        private String message;
        private int status;
        private List<GarbageInfoBean> garbage_info;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<GarbageInfoBean> getGarbage_info() {
            return garbage_info;
        }

        public void setGarbage_info(List<GarbageInfoBean> garbage_info) {
            this.garbage_info = garbage_info;
        }

        public static class GarbageInfoBean {
            /**
             * cate_name : 湿垃圾
             * city_id : 310000
             * city_name : 上海市
             * confidence : 1
             * garbage_name : 坚果炒货
             * ps : 投放建议：容器与外包装为可回收物
             */

            private String cate_name;
            private String city_id;
            private String city_name;
            private double confidence;
            private String garbage_name;
            private String ps;

            public String getCate_name() {
                return cate_name;
            }

            public void setCate_name(String cate_name) {
                this.cate_name = cate_name;
            }

            public String getCity_id() {
                return city_id;
            }

            public void setCity_id(String city_id) {
                this.city_id = city_id;
            }

            public String getCity_name() {
                return city_name;
            }

            public void setCity_name(String city_name) {
                this.city_name = city_name;
            }

            public double getConfidence() {
                return confidence;
            }

            public void setConfidence(int confidence) {
                this.confidence = confidence;
            }

            public String getGarbage_name() {
                return garbage_name;
            }

            public void setGarbage_name(String garbage_name) {
                this.garbage_name = garbage_name;
            }

            public String getPs() {
                return ps;
            }

            public void setPs(String ps) {
                this.ps = ps;
            }
        }
    }
}
