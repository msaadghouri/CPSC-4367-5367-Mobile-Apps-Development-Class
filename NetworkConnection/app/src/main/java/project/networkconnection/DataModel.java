package project.networkconnection;

/**
 * Created by Mohammad-Ghouri on 11/1/17.
 */

public class DataModel {
    private String firstName;
    private String lastName;

    public DataModel(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "{\"firstName\":" + "\"" + firstName
                + "\"" + ", \"lastName\":" + "\"" + lastName
                + "\"" + "}";
    }
}
