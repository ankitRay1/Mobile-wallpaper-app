package wallsplash.ankitray.com.bean;

/**
 * Created by ankit on 03/10/2019.
 */

public class TrendingBean {
    private String id;
    private String title;
    private String regular;

    boolean isSelected = false;



    public TrendingBean(String id, String title,boolean isSelected) {
        this.id = id;
        this.title = title;
        this.isSelected = isSelected;
    }
    public TrendingBean(String id, String regular ) {
        this.id = id;
        this.regular = regular;

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
