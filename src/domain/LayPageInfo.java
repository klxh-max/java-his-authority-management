package domain;

import java.util.List;

//装载layui-table组件分页时的数据结构
public class LayPageInfo {
        private List<?> data ;

        private Integer count ;
        private String msg ;
        private Integer code ;

        public List<?> getData() {
            return data;
        }

        public void setData(List<?> data) {
            this.data = data;
        }

        public int getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public LayPageInfo(List<?> data, Integer count, String msg, Integer code) {
            this.data = data;
            this.count = count;
            this.msg = msg;
            this.code = code;
        }

        public LayPageInfo() {
        }
    }

