package com.eajon.my.model;

public class Token extends BaseResponse {


    /**
     * result : {"userInfo":{"avatar":"user/20190119/logo-2_1547868176839.png","birthday":"2018-12-05 00:00:00","createBy":"","createTime":"2018-12-21 17:54:10","delFlag":"0","email":"11@qq.com","id":"e9ca23d68d884d4ebb19d07889727dae","password":"e376559e4e02ab50","phone":"18566666233","realname":"管理员","salt":"ZvBVXOxv","sex":2,"status":1,"updateBy":"admin","updateTime":"2019-05-13 16:28:51","username":"admin"},"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NjA3MzgxNzksInVzZXJuYW1lIjoiYWRtaW4ifQ.hUN237PahTXFaWndoEORN589KgtKK-rNEKZWg37QcuU"}
     * success : true
     * timestamp : 1560736377766
     */

    private ResultBean result;
    private boolean success;
    private long timestamp;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class ResultBean {
        /**
         * userInfo : {"avatar":"user/20190119/logo-2_1547868176839.png","birthday":"2018-12-05 00:00:00","createBy":"","createTime":"2018-12-21 17:54:10","delFlag":"0","email":"11@qq.com","id":"e9ca23d68d884d4ebb19d07889727dae","password":"e376559e4e02ab50","phone":"18566666233","realname":"管理员","salt":"ZvBVXOxv","sex":2,"status":1,"updateBy":"admin","updateTime":"2019-05-13 16:28:51","username":"admin"}
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NjA3MzgxNzksInVzZXJuYW1lIjoiYWRtaW4ifQ.hUN237PahTXFaWndoEORN589KgtKK-rNEKZWg37QcuU
         */

        private UserInfoBean userInfo;
        private String token;

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public static class UserInfoBean {
            /**
             * avatar : user/20190119/logo-2_1547868176839.png
             * birthday : 2018-12-05 00:00:00
             * createBy :
             * createTime : 2018-12-21 17:54:10
             * delFlag : 0
             * email : 11@qq.com
             * id : e9ca23d68d884d4ebb19d07889727dae
             * password : e376559e4e02ab50
             * phone : 18566666233
             * realname : 管理员
             * salt : ZvBVXOxv
             * sex : 2
             * status : 1
             * updateBy : admin
             * updateTime : 2019-05-13 16:28:51
             * username : admin
             */

            private String avatar;
            private String birthday;
            private String createBy;
            private String createTime;
            private String delFlag;
            private String email;
            private String id;
            private String password;
            private String phone;
            private String realname;
            private String salt;
            private int sex;
            private int status;
            private String updateBy;
            private String updateTime;
            private String username;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getDelFlag() {
                return delFlag;
            }

            public void setDelFlag(String delFlag) {
                this.delFlag = delFlag;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getRealname() {
                return realname;
            }

            public void setRealname(String realname) {
                this.realname = realname;
            }

            public String getSalt() {
                return salt;
            }

            public void setSalt(String salt) {
                this.salt = salt;
            }

            public int getSex() {
                return sex;
            }

            public void setSex(int sex) {
                this.sex = sex;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }
    }
}
