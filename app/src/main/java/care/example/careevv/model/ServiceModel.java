package care.example.careevv.model;

public class ServiceModel {
    private String id,name,status,Imgurl;

    public ServiceModel(String id, String name, String status,String Imgurl) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.Imgurl=Imgurl;
    }

    public ServiceModel() {

    }


    public String getImgurl() {
        return Imgurl;
    }

    public void setImgurl(String imgurl) {
        Imgurl = imgurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
