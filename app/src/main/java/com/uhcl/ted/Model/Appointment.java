package com.uhcl.ted.Model;

/**
 * Created by LENOVO on 29-03-2018.
 */

public class Appointment {

    String doctorName;
    String patientName;
    String doctorId;
    String patientId;
    String subject;
    String appointmentDate;
    String fileUrl;
    String notes;
    boolean accepted;
    boolean rejected;
    String appointmentId;

    public Appointment() {

    }

    public Appointment(String appointmentId, String doctorName, String patientName, String doctorId, String patientId, String subject, String appointmentDate, String fileUrl, String notes, boolean accepted, boolean rejected) {
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.subject = subject;
        this.appointmentDate = appointmentDate;
        this.fileUrl = fileUrl;
        this.notes = notes;
        this.accepted = accepted;
        this.rejected = rejected;
    }


    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {this.appointmentDate = appointmentDate;}

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }
}
