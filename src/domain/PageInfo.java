package domain;

import java.util.List;

//装载（自定义）分页相关的信息
//扩展：增加layui-table组件分页相关的信息
public class PageInfo {
    private Integer page;
    private Integer rows;
    private Integer total;
    private Integer max;
    private Integer start;
    private Integer end;
    private List<?> data;

    public PageInfo() {}

    public PageInfo(Integer page, Integer rows, Integer total, Integer max, Integer start, Integer end, List<?> data) {
        this.page = page;
        this.rows = rows;
        this.total = total;
        this.max = max;
        this.start = start;
        this.end = end;
        this.data = data;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getRows() {
        return rows;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public List<?> getData() {
        return data;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
