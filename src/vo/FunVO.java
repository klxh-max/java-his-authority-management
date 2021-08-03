package vo;

import java.util.List;

//当前存储的数据是给视图用的
public class FunVO {
    private String title;//fname
    private Integer id;//fid
    private List<FunVO> children;//装载当前菜单的子菜单

    public FunVO() {}

    public FunVO(String title, Integer id, List<FunVO> children) {
        this.title = title;
        this.id = id;
        this.children = children;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<FunVO> getChildren() {
        return children;
    }

    public void setChildren(List<FunVO> children) {
        this.children = children;
    }
}
