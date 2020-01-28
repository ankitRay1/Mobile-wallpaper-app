package wallsplash.ankitray.com.bean;

/**
 * Created by Ankit on 03/10/2019.
 */

public class ExploreCatBean {
    private String id;
    private String title;
    private String regular;

    boolean isSelected = false;



    public ExploreCatBean(String title, boolean isSelected) {

        this.title = title;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }
}
