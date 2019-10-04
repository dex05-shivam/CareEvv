package care.example.careevv.model;

public class CheckInModel {

    private String sch_id,agency_id,client_id,user_id,checkin_date,checkin_time,checkin_location,location_status,complete_status;


    public CheckInModel() {

    }

    public CheckInModel(String location_status,String complete_status,String sch_id, String agency_id, String client_id, String user_id, String checkin_date, String checkin_time, String checkin_location) {
        this.sch_id = sch_id;
        this.agency_id = agency_id;
        this.client_id = client_id;
        this.user_id = user_id;
        this.checkin_date = checkin_date;
        this.checkin_time = checkin_time;
        this.checkin_location = checkin_location;
        this.location_status = location_status;
        this.complete_status = complete_status;
    }


    public String getLocation_status() {
        return location_status;
    }

    public void setLocation_status(String location_status) {
        this.location_status = location_status;
    }

    public String getComplete_status() {
        return complete_status;
    }

    public void setComplete_status(String complete_status) {
        this.complete_status = complete_status;
    }

    public String getSch_id() {
        return sch_id;
    }

    public void setSch_id(String sch_id) {
        this.sch_id = sch_id;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCheckin_date() {
        return checkin_date;
    }

    public void setCheckin_date(String checkin_date) {
        this.checkin_date = checkin_date;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public String getCheckin_location() {
        return checkin_location;
    }

    public void setCheckin_location(String checkin_location) {
        this.checkin_location = checkin_location;
    }
}
