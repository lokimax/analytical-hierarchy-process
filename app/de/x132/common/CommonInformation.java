package de.x132.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.x132.user.models.Client;

/**
 * Abstrakte Hauptklasse für Entities. Beinhaltet Metinformationen über die
 * Erstellung der Objekte. Gibt Auskunft wann das Objekt angelegt wurde, welche
 * Benutzer dieses Objekt angelegt hat und wann das Objekt zuletzt verändert
 * wurde.
 * 
 * @author Max Wick
 */
@MappedSuperclass
public abstract class CommonInformation {

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date created;

	@Column(nullable = false)
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date lastmodified;

	/**
	 * Konstruktur zur Intitialisierung durch JPA.
	 */
	public CommonInformation() {
	}

	/**
	 * Hauptkonstruktor.
	 * 
	 * @param userName
	 *            des erstellenden Benutzers.
	 * @param date
	 *            der Erstellung.
	 */
	public CommonInformation(String userName, Date date) {
		this.createdBy = userName;
		this.created = date;
		this.lastmodified = date;
	}
	
	/**
	 * Wird aufgerufen, sobald der Entitymananager persistieren soll.
	 * @param client der Die Änderung vornimmt.
	 */
	public void setModifiedBy(Client client) {
		if (null == this.getCreated()) {
			this.setCreated(new Date());
			this.setCreatedBy(client.getNickname());
		}
		this.setLastmodified(new Date());
	}

	public Date getCreated() {
		return this.created;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public Date getLastmodified() {
		return this.lastmodified;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setLastmodified(Date lastmodified) {
		this.lastmodified = lastmodified;
	}


}
