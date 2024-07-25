import java.util.List;

public class TreeItem {
    private Integer key;
    private String value;
    private Item data;
    private List<TreeItem> children;
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Item getData() {
        return data;
    }

    public void setData(Item data) {
        this.data = data;
    }

    public List<TreeItem> getChildren() {
        return children;
    }

    public void setChildren(List<TreeItem> children) {
        this.children = children;
    }
}
