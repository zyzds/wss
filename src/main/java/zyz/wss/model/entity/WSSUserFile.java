package zyz.wss.model.entity;


public class WSSUserFile extends WSSComponent {

    //getter and setter
    public WSSFile getFile() {
        return super.getFile();
    }

    public WSSComponent setFile(WSSFile file) {
        super.setFile(file);
        return this;
    }

    public WSSCategory getCategory() {
        return super.getCategory();
    }

    public WSSComponent setCategory(WSSCategory category) {
        super.setCategory(category);
        return this;
    }

    public Long getSize() {
        return super.getSize();
    }
}
