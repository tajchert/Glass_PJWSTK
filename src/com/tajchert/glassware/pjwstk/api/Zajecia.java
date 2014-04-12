package com.tajchert.glassware.pjwstk.api;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zajecia{
	public Date date;
	public Date endDate;

	@SerializedName("Budynek")
	@Expose
	private String budynek;
	@SerializedName("Data_roz")
	@Expose
	private String data_roz;
	@SerializedName("Data_zak")
	@Expose
	private String data_zak;
	@SerializedName("Kod")
	@Expose
	private String kod;
	@SerializedName("Nazwa")
	@Expose
	private String nazwa;
	@SerializedName("Nazwa_sali")
	@Expose
	private String nazwa_sali;
	@SerializedName("TypZajec")
	@Expose
	private String typZajec;
	@Expose
	private Integer idRealizacja_zajec;

	public String getBudynek() {
		return budynek;
	}

	public void setBudynek(String budynek) {
		this.budynek = budynek;
	}

	public String getData_roz() {
		return data_roz;
	}

	public void setData_roz(String data_roz) {
		this.data_roz = data_roz;
	}

	public String getData_zak() {
		return data_zak;
	}

	public void setData_zak(String data_zak) {
		this.data_zak = data_zak;
	}

	public String getKod() {
		return kod;
	}

	public void setKod(String kod) {
		this.kod = kod;
	}

	public String getNazwa() {
		return nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public String getNazwa_sali() {
		return nazwa_sali;
	}

	public void setNazwa_sali(String nazwa_sali) {
		this.nazwa_sali = nazwa_sali;
	}

	public String getTypZajec() {
		return typZajec;
	}

	public void setTypZajec(String typZajec) {
		this.typZajec = typZajec;
	}

	public Integer getIdRealizacja_zajec() {
		return idRealizacja_zajec;
	}

	public void setIdRealizacja_zajec(Integer idRealizacja_zajec) {
		this.idRealizacja_zajec = idRealizacja_zajec;
	}

}