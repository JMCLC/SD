package pt.tecnico.addressbook.server.domain;

import pt.tecnico.addressbook.grpc.PersonInfo.PhoneType;
import pt.tecnico.addressbook.grpc.PersonInfo;

public class Person {
    private String name;
    private String email;
    private int phoneNumber;
    private PhoneType type;
    private String education_degree;

    public Person(String name, String email, int phoneNumber, PhoneType type, String education_degree) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.education_degree = education_degree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public String getEducationDegree() {
        return education_degree;
    }

    public void setEducationDegree(String education_degree) {
        this.education_degree = education_degree;
    }

    public PersonInfo proto() {
        PersonInfo.PhoneNumber phone = PersonInfo.PhoneNumber.newBuilder().setNumber(phoneNumber).setType(type).build();
        return PersonInfo.newBuilder().setName(name).setEmail(email).setPhone(phone).build();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", type=" + type +
                '}';
    }
}
