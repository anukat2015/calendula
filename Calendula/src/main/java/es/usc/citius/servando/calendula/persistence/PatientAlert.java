package es.usc.citius.servando.calendula.persistence;

import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An abstract representation of an alert, that allows a simplified alarm
 * persistence strategy, using the same table for different alert types
 *
 * @param <P> Type of the class that models an specific alert type
 * @param <T> Type that encapsulates de specific alarm details
 */
@SuppressWarnings("unused")
@DatabaseTable(tableName = "PatientAlerts")
public class PatientAlert<P extends PatientAlert<P,T>, T> {

    private static final Gson gson = Converters.registerAll(new GsonBuilder()).create();

    public final static class Level {

        private Level() {}

        public static final int LOW = 1;
        public static final int MEDIUM = 2;
        public static final int HIGH = 3;
    }

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DETAILS = "Details";
    public static final String COLUMN_PATIENT = "Patient";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_MEDICINE = "Medicine";
    public static final String COLUMN_LEVEL = "Level";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_TYPE)
    private String type;

    @DatabaseField(columnName = COLUMN_PATIENT, foreign = true, foreignAutoRefresh = true)
    private Patient patient;

    @DatabaseField(columnName = COLUMN_MEDICINE, foreign = true, foreignAutoRefresh = true)
    private Medicine medicine;

    @DatabaseField(columnName = COLUMN_LEVEL)
    private int level;

    @DatabaseField(columnName = COLUMN_DETAILS)
    private String jsonDetails;

    public PatientAlert() {
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public T getDetails() {
        if (getDetailsType() != null) {
            return (T) gson.fromJson(jsonDetails, getDetailsType());
        }
        return null;
    }

    public void setDetails(T details) {
        this.jsonDetails = gson.toJson(details);
    }

    public String getJsonDetails() {
        return jsonDetails;
    }

    public void setJsonDetails(String jsonDetails) {
        this.jsonDetails = jsonDetails;
    }

    public final boolean hasDetails() {
        return getDetailsType() != null;
    }

    // Methods to be overridden by subclasses
    public Class<?> getDetailsType() {
        throw new RuntimeException("This method must be overridden by subclasses");
    }

    public Class<?> viewProviderType(){
        return null;
    }

    public P map(){
        try {
            P result = (P) Class.forName(getType()).newInstance();
            result.setId(id);
            result.setPatient(patient);
            result.setMedicine(medicine);
            result.setJsonDetails(jsonDetails);
            result.setLevel(level);
            result.setType(type);
            return  result;
        } catch (Exception e) {
            Log.e("PatientAlert", "Unable to map alert to an specific alert type",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "PatientAlert{" +
                "id=" + id +
                ", type=" + type +
                ", patient=" + patient +
                ", medicine=" + medicine +
                ", level=" + level +
                ", jsonDetails='" + jsonDetails + '\'' +
                '}';
    }
}